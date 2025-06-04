package Projekt_Quiz;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class QuizClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverAddress;
    private int serverPort;
    private QuizSpielGUI quizSpielGUI;

    // NEU: Für Rangliste im Wartefenster
    private volatile List<String> aktuelleRangliste = new ArrayList<>();
    private volatile List<String> finaleRangliste = new ArrayList<>();
    private volatile boolean finaleRanglisteEmpfangen = false;

    public QuizClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void startConnection(int schwierigkeitsgrad, String playerName) throws IOException {
        try {
            System.out.println("=== CLIENT VERBINDUNG STARTET ===");
            System.out.println("Verbinde zu Server: " + serverAddress + ":" + serverPort);
            System.out.println("Client Name: " + playerName);

            clientSocket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("Verbindung erfolgreich hergestellt!");
            System.out.println("Lokale Adresse: " + clientSocket.getLocalAddress());
            System.out.println("Remote Adresse: " + clientSocket.getRemoteSocketAddress());

            // Starte extra Thread für alle Nachrichten vom Server!
            new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("Nachricht vom Server: " + serverMessage);

                        if (serverMessage.startsWith("SPIEL_STARTET")) {
                            System.out.println("Spiel-Start-Signal empfangen!");

                            final int clientSchwierigkeitsgrad = schwierigkeitsgrad;
                            final String clientName = playerName;

                            // Extrahiere Spielerliste aus der Nachricht
                            String spielerListeString = "";
                            if (serverMessage.contains("|")) {
                                spielerListeString = serverMessage.substring(serverMessage.indexOf("|") + 1);
                                System.out.println("Spielerliste vom Server: " + spielerListeString);
                            }
                            final String finalSpielerListeString = spielerListeString;

                            SwingUtilities.invokeLater(() -> {
                                try {
                                    System.out.println("Erstelle Client-GUI...");
                                    List<Spieler> spielerListe = stringToSpielerListe(finalSpielerListeString, clientName);

                                    // Finde den Spieler für diesen Client
                                    Spieler clientSpieler = null;
                                    for (Spieler spieler : spielerListe) {
                                        if (spieler.getName().equals(clientName) ||
                                                (spieler.getName().equals("Client") && clientName.equals(playerName))) {
                                            clientSpieler = spieler;
                                            break;
                                        }
                                    }

                                    if (clientSpieler == null) {
                                        System.out.println("Client-Spieler nicht gefunden, erstelle neuen...");
                                        clientSpieler = new Spieler(clientName);
                                        spielerListe.add(clientSpieler);
                                    }

                                    System.out.println("Starte GUI für Client: " + clientName);
                                    System.out.println("Spielerliste für GUI: ");
                                    for (Spieler s : spielerListe) {
                                        System.out.println("- " + s.getName());
                                    }

                                    quizSpielGUI = new QuizSpielGUI(clientSpieler, clientSchwierigkeitsgrad, new ArrayList<>(spielerListe), this);
                                    System.out.println("Client-GUI erfolgreich erstellt!");

                                } catch (Exception e) {
                                    System.err.println("Fehler beim Erstellen der Client-GUI:");
                                    e.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Fehler beim Starten der GUI: " + e.getMessage());
                                }
                            });
                        }
                        else if (serverMessage.startsWith("RANGLISTE_FINAL|")) {
                            // Finale Rangliste nach Spielende empfangen
                            String rangDaten = serverMessage.substring("RANGLISTE_FINAL|".length());
                            String[] eintraege = rangDaten.split(";");
                            List<String> rangListe = new ArrayList<>();
                            for (int i = 0; i < eintraege.length; i++) {
                                String[] parts = eintraege[i].split(",");
                                String name = parts[0];
                                long zeit = Long.parseLong(parts[1]);
                                rangListe.add((i + 1) + ". " + name + ": " + (zeit / 1000) + " Sek");
                            }
                            finaleRangliste = rangListe;
                            finaleRanglisteEmpfangen = true;
                            if (quizSpielGUI != null) {
                                quizSpielGUI.aktualisiereFinaleRangliste();
                            }
                        }
                        else if (serverMessage.startsWith("RANGLISTE|")) {
                            // Empfange und speichere die aktuelle Rangliste (live)
                            String rangDaten = serverMessage.substring("RANGLISTE|".length());
                            String[] eintraege = rangDaten.split(";");
                            List<String> rangListe = new ArrayList<>();
                            for (int i = 0; i < eintraege.length; i++) {
                                String[] parts = eintraege[i].split(",");
                                String name = parts[0];
                                long zeit = Long.parseLong(parts[1]);
                                rangListe.add((i + 1) + ". " + name + ": " + (zeit / 1000) + " Sek");
                            }
                            aktuelleRangliste = rangListe;
                            // Optional: Benachrichtige deine GUI, dass sie aktualisieren soll
                            if (quizSpielGUI != null) {
                                quizSpielGUI.aktualisiereWartefensterRangliste();
                            }
                        }
                        else {
                            // Andere Nachrichten vom Server anzeigen
                            final String message = serverMessage;
                            SwingUtilities.invokeLater(() ->
                                    JOptionPane.showMessageDialog(null, message, "Server Info", JOptionPane.INFORMATION_MESSAGE));
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Fehler im Nachrichten-Thread: " + ex.getMessage());
                }
            }).start();

        } catch (IOException e) {
            System.err.println("=== CLIENT VERBINDUNGSFEHLER ===");
            System.err.println("Server: " + serverAddress + ":" + serverPort);
            System.err.println("Fehler: " + e.getMessage());
            System.err.println("Fehlertyp: " + e.getClass().getSimpleName());

            if (e instanceof ConnectException) {
                System.err.println("Connection refused - Server ist wahrscheinlich nicht gestartet");
            } else if (e instanceof UnknownHostException) {
                System.err.println("Unknown host - IP-Adresse ist falsch");
            } else if (e instanceof SocketTimeoutException) {
                System.err.println("Timeout - Server antwortet nicht");
            }

            throw e;
        }
    }

    // Nach Spielende aufrufen!
    public void sendeFertigNachricht(Spieler spieler) {
        if (out != null) {
            out.println("FINISHED|" + spieler.getName() + "|" + spieler.getZeit());
        }
    }

    public String sendMessage(String msg) throws IOException {
        if (out != null) {
            out.println(msg);
            System.out.println("Nachricht an Server gesendet: " + msg);
        }
        if (in != null) {
            String response = in.readLine();
            System.out.println("Antwort vom Server: " + response);
            return response;
        }
        return null;
    }

    public void stopConnection() throws IOException {
        try {
            System.out.println("Schließe Client-Verbindung...");
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            System.out.println("Client-Verbindung geschlossen.");
        } catch (IOException e) {
            System.err.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
        }
    }

    private List<Spieler> stringToSpielerListe(String spielerListeString, String clientName) {
        List<Spieler> spielerListe = new ArrayList<>();

        System.out.println("Konvertiere Spielerliste: '" + spielerListeString + "'");

        if (spielerListeString == null || spielerListeString.trim().isEmpty()) {
            System.out.println("Leere Spielerliste - erstelle Fallback");
            // Fallback: Erstelle Standard-Spielerliste
            spielerListe.add(new Spieler("Host"));
            spielerListe.add(new Spieler(clientName));
            return spielerListe;
        }

        String[] spielerArray = spielerListeString.split(",");
        System.out.println("Gefundene Spieler: " + spielerArray.length);

        for (String spielerName : spielerArray) {
            String trimmedName = spielerName.trim();
            System.out.println("Verarbeite Spieler: '" + trimmedName + "'");

            if (!trimmedName.isEmpty()) {
                if (trimmedName.equals("Client")) {
                    spielerListe.add(new Spieler(clientName));
                    System.out.println("Client-Spieler hinzugefügt: " + clientName);
                } else {
                    spielerListe.add(new Spieler(trimmedName));
                    System.out.println("Spieler hinzugefügt: " + trimmedName);
                }
            }
        }

        System.out.println("Finale Spielerliste:");
        for (Spieler s : spielerListe) {
            System.out.println("- " + s.getName());
        }

        return spielerListe;
    }

    // Getter für die aktuelle Rangliste (für die GUI)
    public List<String> getAktuelleRangliste() {
        return new ArrayList<>(aktuelleRangliste);
    }
    // Getter für die finale Rangliste (nach Spielende)
    public List<String> getFinaleRangliste() {
        return new ArrayList<>(finaleRangliste);
    }
    public boolean isFinaleRanglisteEmpfangen() {
        return finaleRanglisteEmpfangen;
    }
}