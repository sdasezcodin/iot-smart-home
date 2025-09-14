package com.smarthome.network;

import com.smarthome.exception.NetworkException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A network client for communicating with the smart home server.
 * <p>
 * This class handles the low-level details of socket connections,
 * allowing the application to send various types of commands
 * (e.g., connect, disconnect, send sensor data) to the server.
 */
public class Client {

    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Initializes a new client instance without connecting.
     * The connection is established later using the {@link #connectToServer()} method.
     *
     * @param host The hostname or IP address of the server to connect to.
     * @param port The port number of the server.
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Establishes a TCP connection to the server using the host and port
     * specified during initialization. This method also sets up the input
     * and output streams for communication.
     *
     * @throws NetworkException if the connection fails.
     */
    public void connectToServer() {
        try {
            // Create a new socket to connect to the server
            socket = new Socket(host, port);

            // Set up the output stream to send data to the server
            out = new PrintWriter(socket.getOutputStream(), true);

            // Set up the input stream to receive data from the server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (Exception e) {
            throw new NetworkException("Client failed to connect to " + host + ":" + port, e);
        }
    }

    /**
     * Sends a message to the server and waits for a reply.
     * This is a private helper method used by the public `send*` methods.
     *
     * @param message The string message to be sent.
     * @throws NetworkException if the client is not connected or the message fails to send.
     */
    private void sendMessage(String message) {
        try {
            // Check for a valid connection before attempting to send
            if (out == null || in == null) {
                throw new NetworkException("Client not connected.");
            }

            // Send the message to the server
            out.println(message);

            // Wait for and read the server's reply silently. This is crucial for synchronizing
            // the client and server communication.
            in.readLine();
        } catch (Exception e) {
            throw new NetworkException("Failed to send message: " + message, e);
        }
    }

    /**
     * Sends a "CONNECT" command to the server, indicating a device is coming online.
     *
     * @param deviceName The name of the device attempting to connect.
     */
    public void sendConnect(String deviceName) {
        sendMessage("CONNECT " + deviceName);
    }

    /**
     * Sends a "DISCONNECT" command to the server, indicating a device is going offline.
     *
     * @param deviceName The name of the device to disconnect.
     */
    public void sendDisconnect(String deviceName) {
        sendMessage("DISCONNECT " + deviceName);
    }

    /**
     * Sends a sensor reading to the server.
     * The reading message should be formatted according to the server's protocol.
     *
     * @param readingMessage A formatted string containing the sensor data.
     */
    public void sendSensorReading(String readingMessage) {
        sendMessage("SENSOR " + readingMessage);
    }

    /**
     * Closes the socket connection and all associated streams.
     * This should be called to properly release network resources.
     *
     * @throws NetworkException if there is a failure while closing the connection.
     */
    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            throw new NetworkException("Failed to close client connection", e);
        }
    }
}