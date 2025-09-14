package com.smarthome.network;

import com.smarthome.exception.NetworkException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.smarthome.util.ConsoleColorsUtil.*;

/**
 * A multi-threaded TCP server for the smart home system.
 * <p>
 * This server listens for incoming client connections (from smart devices),
 * handles each connection in a separate thread to support multiple clients
 * concurrently, and processes simple text-based commands.
 */
public class Server {

    private final int port;
    private volatile boolean running = true;

    // Use an ExecutorService with a cached thread pool to manage client-handling threads.
    // A cached pool creates new threads as needed and reuses old ones.
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    /**
     * Constructs a new Server instance.
     * @param port The port number on which the server will listen for connections.
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Starts the server. This method runs in a loop, continuously accepting new client connections.
     * Each new connection is handed off to a thread from the client pool for processing.
     *
     * @throws NetworkException if the server socket cannot be created or an error occurs.
     */
    public void start() {
        // Use a try-with-resources statement to ensure the ServerSocket is automatically closed.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(CYAN + "Server: Listening on port " + port + RESET);

            // The main server loop. It continues to run as long as the 'running' flag is true.
            while (running) {
                // Blocks until a client connects, then returns a new Socket for that client.
                Socket clientSocket = serverSocket.accept();

                // Submits the handleClient task to the thread pool for execution.
                // This allows the main thread to immediately go back to accepting new clients.
                clientPool.submit(() -> handleClient(clientSocket));
            }
        } catch (Exception e) {
            throw new NetworkException(RED + "Server error on port " + port + RESET, e);
        } finally {
            // Ensure the server shuts down gracefully even if an error occurs.
            stop();
        }
    }

    /**
     * Handles communication with a single client.
     * This method runs in a separate thread for each connected client.
     *
     * @param socket The client's socket.
     */
    private void handleClient(Socket socket) {
        // Use try-with-resources to ensure the streams and socket are closed automatically.
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message;
            // Read messages from the client line by line until the client disconnects.
            while ((message = in.readLine()) != null) {

                String reply;
                String color = WHITE;

                // Process the message based on the command prefix.
                if (message.startsWith("CONNECT ")) {
                    String deviceName = message.substring("CONNECT ".length()).trim();
                    reply = deviceName + " connected";
                    color = GREEN;

                } else if (message.startsWith("DISCONNECT ")) {
                    String deviceName = message.substring("DISCONNECT ".length()).trim();
                    reply = deviceName + " disconnected";
                    color = YELLOW;

                } else if (message.startsWith("SENSOR ")) {
                    reply = message.substring("SENSOR ".length()).trim();
                    // Apply a specific color based on the device type mentioned in the message.
                    if (reply.toUpperCase().contains("AC")) color = CYAN;
                    else if (reply.toUpperCase().contains("FAN")) color = PURPLE;
                    else if (reply.toUpperCase().contains("SPEAKER")) color = BLUE;
                    else color = WHITE;

                } else {
                    reply = "Unknown command";
                    color = RED;
                }

                // Print the processed message to the server console.
                System.out.println(color + "Server: " + reply + RESET);

                // Send a reply back to the client to acknowledge receipt.
                out.println(reply);
            }
        } catch (Exception e) {
            // This block handles exceptions like a client abruptly disconnecting.
            System.err.println(RED + "Server: Client disconnected: " + socket.getRemoteSocketAddress() + RESET);
        } finally {
            // The socket is already handled by try-with-resources, but an explicit close
            // is a good practice if not using try-with-resources or for additional safety.
            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }

    /**
     * Stops the server gracefully.
     * Sets the running flag to false to stop the main loop and shuts down the thread pool.
     */
    public void stop() {
        running = false;
        // Shuts down the thread pool immediately, interrupting any running tasks.
        clientPool.shutdownNow();
        System.out.println(RED + "Server: Shutdown" + RESET);
    }
}