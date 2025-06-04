package Projekt_Quiz;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private boolean spielGestartet = false;
    private List<Spieler> spielerListe = new ArrayList<>();
    private int schwierigkeitsgrad;
    private String hostName;
    private QuizSpielGUI hostGUI;

    // Map für fertige Zeiten
    private final Map<String, Long> fertigeSpieler = new HashMap<>();

    public void start(int port, int schwierigkeitsgrad, String hostName) throws IOException {
        this.schwierigkeitsgrad = schwierigkeitsgrad;
        this.hostName = hostName;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("=== SERVER ERFOLGREICH GESTARTET ===");
            System.out.println("Port: " + port);
            System.out.println("Host: " + hostName);
            System.out.println("Schwierigkeitsgrad: " + schwierigkeitsgrad);
            System.out.println("Warte auf Verbindungen...");
            System.out.println("=====================================");

            // Erstelle Host-Spieler
            Spieler host = new Spieler(hostName);
            spielerListe.add(host);

            while (!spielGestartet) {
                try {
                    System.out.println("Warte auf Client-Verbindung...");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("NEUER CLIENT VERBUNDEN!");
                    System.out.println("Client IP: " + clientSocket.getInetAddress().getHostAddress());

                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();

                    System.out.println("Anzahl verbundene Clients: " + clients.size());

                    // Starte das Spiel, wenn ein Client verbunden ist (Host + 1 Client = 2 Spieler)
                    if (clients.size() == 1 && !spielGestartet) {
                        spielGestartet = true;
                        System.out.println("SPIEL WIRD GESTARTET - 2 Spieler verbunden!");

                        // Füge Client-Spieler hinzu
                        Spieler client = new Spieler("Client");
                        spielerListe.add(client);

                        // Host-GUI erstellen
                        SwingUtilities.invokeLater(() -> {
                            System.out.println("Erstelle Host-GUI...");
                            hostGUI = new QuizSpielGUI(host, schwierigkeitsgrad, new ArrayList<>(spielerListe), null);
                        });

                        // Kurz warten, damit Host-GUI geladen ist
                        Thread.sleep(1000);

                        // Sende das Startsignal an alle Clients
                        System.out.println("Sende Startsignal an Clients...");
                        broadcast("SPIEL_STARTET", spielerListeToString(spielerListe));

                        System.out.println("=== SPIEL ERFOLGREICH GESTARTET ===");
                        System.out.println("Spieler: " + spielerListe.size());
                        for (Spieler spieler : spielerListe) {
                            System.out.println("- " + spieler.getName());
                        }
                        System.out.println("===================================");
                    }
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        System.err.println("Fehler beim Akzeptieren einer Client-Verbindung: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                } catch (InterruptedException e) {
                    System.err.println("Thread unterbrochen: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("FEHLER beim Starten des Servers auf Port " + port);
            System.err.println("Mögliche Ursachen:");
            System.err.println("- Port bereits in Verwendung");
            System.err.println("- Keine Berechtigung für Port");
            System.err.println("- Firewall blockiert Port");
            throw e;
        }
    }

    public synchronized void broadcast(String message, String spielerListe) {
        String fullMessage = message + (spielerListe != null ? "|" + spielerListe : "");
        System.out.println("Broadcasting: " + fullMessage);

        for (ClientHandler client : clients) {
            try {
                client.sendMessage(fullMessage);
            } catch (Exception e) {
                System.err.println("Fehler beim Senden an Client: " + e.getMessage());
            }
        }
    }

    public void broadcast(String message) {
        broadcast(message, null);
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client entfernt. Verbleibende Clients: " + clients.size());
    }

    private String spielerListeToString(List<Spieler> spielerListe) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spielerListe.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(spielerListe.get(i).getName());
        }
        return sb.toString();
    }

    public void stop() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    // Methoden für Rangliste
    public synchronized void spielerFertig(String name, long zeit) {
        fertigeSpieler.put(name, zeit);
        // Wenn alle Spieler ihre Zeit gemeldet haben, sende finale Rangliste an alle
        broadcastRangliste();
        if (fertigeSpieler.size() == spielerListe.size()) {
            broadcast("RANGLISTE_FINAL|" + getFinalRanking());
        }
    }

    private void broadcastRangliste() {
        // Sortiere nach Zeit
        List<Map.Entry<String, Long>> liste = new ArrayList<>(fertigeSpieler.entrySet());
        liste.sort(Comparator.comparingLong(Map.Entry::getValue));
        StringBuilder b = new StringBuilder("RANGLISTE|");
        for (int i = 0; i < liste.size(); i++) {
            if (i > 0) b.append(";");
            b.append(liste.get(i).getKey()).append(",").append(liste.get(i).getValue());
        }
        for (ClientHandler client : clients) {
            client.sendMessage(b.toString());
        }
    }

    private String getFinalRanking() {
        List<Map.Entry<String, Long>> liste = new ArrayList<>(fertigeSpieler.entrySet());
        liste.sort(Comparator.comparingLong(Map.Entry::getValue));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < liste.size(); i++) {
            if (i > 0) sb.append(";");
            sb.append(liste.get(i).getKey()).append(",").append(liste.get(i).getValue());
        }
        return sb.toString();
    }
}