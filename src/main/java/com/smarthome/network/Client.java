package com.smarthome.network;

import com.smarthome.exception.NetworkException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String host, int port) { this.host = host; this.port = port; }

    public void connectToServer() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Client local prints removed
        } catch (Exception e) {
            throw new NetworkException("Client failed to connect to " + host + ":" + port, e);
        }
    }

    private void sendMessage(String message) {
        try {
            if (out == null || in == null) throw new NetworkException("Client not connected.");
            out.println(message);
            in.readLine(); // Read reply silently
        } catch (Exception e) {
            throw new NetworkException("Failed to send message: " + message, e);
        }
    }

    public void sendConnect(String deviceName) {
        sendMessage("CONNECT " + deviceName);
    }

    public void sendDisconnect(String deviceName) {
        sendMessage("DISCONNECT " + deviceName);
    }

    public void sendSensorReading(String readingMessage) {
        sendMessage("SENSOR " + readingMessage);
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (Exception e) {
            throw new NetworkException("Failed to close client connection", e);
        }
    }
}
