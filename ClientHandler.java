package Projekt_Quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private QuizServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientIP;

    public ClientHandler(Socket socket, QuizServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.clientIP = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try {
            System.out.println("=== CLIENT HANDLER GESTARTET ===");
            System.out.println("Client IP: " + clientIP);

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("Client Handler bereit für: " + clientIP);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Empfangen von " + clientIP + ": " + inputLine);

                // NEU: Zeit empfangen und weiterleiten
                if (inputLine.startsWith("FINISHED|")) {
                    String[] split = inputLine.split("\\|");
                    String name = split[1];
                    long zeit = Long.parseLong(split[2]);
                    server.spielerFertig(name, zeit);
                } else {
                    server.broadcast("CLIENT_MESSAGE|" + clientIP + "|" + inputLine);
                }
            }

        } catch (IOException e) {
            System.err.println("Client Handler Fehler für " + clientIP + ": " + e.getMessage());
            if (!clientSocket.isClosed()) {
                e.printStackTrace();
            }
        } finally {
            cleanup();
        }
    }

    public void sendMessage(String message) {
        try {
            if (out != null && !clientSocket.isClosed()) {
                out.println(message);
                System.out.println("Nachricht an " + clientIP + " gesendet: " + message);
            } else {
                System.err.println("Kann nicht an " + clientIP + " senden - Verbindung geschlossen");
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Senden an " + clientIP + ": " + e.getMessage());
        }
    }

    private void cleanup() {
        try {
            System.out.println("Cleaning up client connection: " + clientIP);

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }

            server.removeClient(this);
            System.out.println("Client " + clientIP + " disconnected and cleaned up");

        } catch (IOException e) {
            System.err.println("Error during cleanup for " + clientIP + ": " + e.getMessage());
        }
    }

    public String getClientIP() {
        return clientIP;
    }

    public boolean isConnected() {
        return clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected();
    }
}