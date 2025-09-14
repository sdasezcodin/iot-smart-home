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

public class Server {

    private final int port;
    private boolean running = true;
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    public Server(int port) { this.port = port; }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(CYAN + "Server: Listening on port " + port + RESET);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                clientPool.submit(() -> handleClient(clientSocket));
            }
        } catch (Exception e) {
            throw new NetworkException(RED + "Server error on port " + port + RESET, e);
        } finally {
            stop();
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {

                String reply;
                String color = WHITE;

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
                    if (reply.toUpperCase().contains("AC")) color = CYAN;
                    else if (reply.toUpperCase().contains("FAN")) color = PURPLE;
                    else if (reply.toUpperCase().contains("SPEAKER")) color = BLUE;
                    else color = WHITE;

                } else {
                    reply = "Unknown command";
                    color = RED;
                }

                System.out.println(color + "Server: " + reply + RESET);
                out.println(reply); // reply silently
            }

        } catch (Exception e) {
            System.err.println(RED + "Server: Client disconnected: " + socket.getRemoteSocketAddress() + RESET);
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }

    public void stop() {
        running = false;
        clientPool.shutdownNow();
        System.out.println(RED + "Server: Shutdown" + RESET);
    }
}
