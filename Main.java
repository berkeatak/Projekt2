package Projekt_Quiz;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] modi = {"Einzelspieler", "Mehrspieler"};
            int modusAuswahl = JOptionPane.showOptionDialog(
                    null,
                    "Wähle den Spielmodus:",
                    "Spielmodus",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    modi,
                    modi[0]
            );

            if (modusAuswahl == 0) {
                einzelspielerModus();
            } else if (modusAuswahl == 1) {
                mehrspielerModus();
            }
        });
    }

    private static void einzelspielerModus() {
        String einfuehrungstext = "Willkommen zum Escape Room Quiz im Einzelspielermodus!\n\n" +
                "In diesem Spiel musst du verschiedene Rätsel lösen, um aus den Räumen zu entkommen.\n" +
                "Du hast drei Leben. Bei jeder falschen Antwort verlierst du ein Leben.\n" +
                "Wenn du zweimal hintereinander falsch liegst oder die Zeit überschreitest, verlierst du zwei Leben.\n" +
                "Viel Glück!";

        JOptionPane.showMessageDialog(null, einfuehrungstext, "Einführung", JOptionPane.INFORMATION_MESSAGE);

        String[] schwierigkeitsgrade = {"Leicht", "Mittel", "Schwer"};
        int schwierigkeitsgradAuswahl = JOptionPane.showOptionDialog(
                null,
                "Wähle den Schwierigkeitsgrad:",
                "Schwierigkeitsgrad",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                schwierigkeitsgrade,
                schwierigkeitsgrade[0]
        );

        String name = JOptionPane.showInputDialog(null, "Wie heißt du?");
        if (name == null || name.isEmpty()) {
            name = "Spieler";
        }

        Spieler spieler = new Spieler(name);
        List<Spieler> spielerListe = new ArrayList<>();
        spielerListe.add(spieler);

        new QuizSpielGUI(spieler, schwierigkeitsgradAuswahl, spielerListe, null);
    }

    private static void mehrspielerModus() {
        String[] mehrspielerOptionen = {"Raum erstellen", "Raum beitreten"};
        int mehrspielerAuswahl = JOptionPane.showOptionDialog(
                null,
                "Möchtest du einen Raum erstellen oder einem Raum beitreten?",
                "Mehrspieler-Modus",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                mehrspielerOptionen,
                mehrspielerOptionen[0]
        );

        String[] schwierigkeitsgrade = {"Leicht", "Mittel", "Schwer"};
        int schwierigkeitsgradAuswahl = JOptionPane.showOptionDialog(
                null,
                "Wähle den Schwierigkeitsgrad:",
                "Schwierigkeitsgrad",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                schwierigkeitsgrade,
                schwierigkeitsgrade[0]
        );

        if (mehrspielerAuswahl == 0) {
            // Server-Modus
            String hostName = JOptionPane.showInputDialog(null, "Wie heißt du?");
            if (hostName == null || hostName.isEmpty()) {
                hostName = "Host";
            }

            final String finalHostName = hostName;

            String einfuehrungstext = "Willkommen zum Escape Room Quiz im Mehrspielermodus!\n\n" +
                    "Du hast einen Raum erstellt. Warte, bis ein anderer Spieler beitritt.\n" +
                    "Du hast drei Leben. Bei jeder falschen Antwort verlierst du ein Leben.\n" +
                    "Wenn du zweimal hintereinander falsch liegst oder die Zeit überschreitest, verlierst du zwei Leben.\n" +
                    "Viel Glück!";

            JOptionPane.showMessageDialog(null, einfuehrungstext, "Einführung", JOptionPane.INFORMATION_MESSAGE);

            // IP-Adresse anzeigen für andere Spieler
            String ipAddress = getLocalIPAddress();
            JOptionPane.showMessageDialog(null,
                    "Server wird gestartet...\n" +
                            "Port: 6666\n" +
                            "Deine IP-Adresse: " + ipAddress + "\n\n" +
                            "Teile diese IP-Adresse mit dem anderen Spieler!\n" +
                            "Warte auf zweiten Spieler...",
                    "Server Info", JOptionPane.INFORMATION_MESSAGE);

            QuizServer server = new QuizServer();
            new Thread(() -> {
                try {
                    System.out.println("=== SERVER STARTET ===");
                    System.out.println("Server IP: " + ipAddress);
                    server.start(6666, schwierigkeitsgradAuswahl, finalHostName);
                } catch (IOException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(null,
                                    "Fehler beim Starten des Servers: " + e.getMessage() +
                                            "\n\nMögliche Ursachen:\n" +
                                            "- Port 6666 bereits in Verwendung\n" +
                                            "- Firewall blockiert den Port\n" +
                                            "- Netzwerk-Berechtigungen fehlen")
                    );
                }
            }).start();

        } else if (mehrspielerAuswahl == 1) {
            // Client-Modus
            String serverAddress = JOptionPane.showInputDialog(null,
                    "Gib die Server IP-Adresse ein:\n" +
                            "(z.B. 192.168.1.100)\n" +
                            "NICHT 'localhost' verwenden bei verschiedenen PCs!");

            if (serverAddress == null || serverAddress.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Keine Server-Adresse eingegeben!");
                return;
            }

            String clientName = JOptionPane.showInputDialog(null, "Wie heißt du?");
            if (clientName == null || clientName.isEmpty()) {
                clientName = "Client";
            }

            final String finalServerAddress = serverAddress.trim();
            final String finalClientName = clientName;

            String einfuehrungstext = "Willkommen zum Escape Room Quiz im Mehrspielermodus!\n\n" +
                    "Du trittst einem Raum bei. Das Spiel startet, sobald beide Spieler verbunden sind.\n" +
                    "Du hast drei Leben. Bei jeder falschen Antwort verlierst du ein Leben.\n" +
                    "Wenn du zweimal hintereinander falsch liegst oder die Zeit überschreitest, verlierst du zwei Leben.\n" +
                    "Viel Glück!";

            JOptionPane.showMessageDialog(null, einfuehrungstext, "Einführung", JOptionPane.INFORMATION_MESSAGE);

            JOptionPane.showMessageDialog(null, "Verbinde zum Server: " + finalServerAddress + ":6666\n\nBitte warten...");

            new Thread(() -> {
                try {
                    System.out.println("=== CLIENT STARTET ===");
                    System.out.println("Versuche Verbindung zu: " + finalServerAddress + ":6666");
                    QuizClient client = new QuizClient(finalServerAddress, 6666);
                    client.startConnection(schwierigkeitsgradAuswahl, finalClientName);
                } catch (IOException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(null,
                                    "Fehler beim Verbinden zum Server!\n\n" +
                                            "Server: " + finalServerAddress + ":6666\n\n" +
                                            "Mögliche Ursachen:\n" +
                                            "- Server ist nicht gestartet\n" +
                                            "- Falsche IP-Adresse eingegeben\n" +
                                            "- Firewall blockiert Verbindung\n" +
                                            "- Port 6666 nicht erreichbar\n" +
                                            "- Netzwerk-Problem zwischen PCs\n\n" +
                                            "Technischer Fehler: " + e.getMessage())
                    );
                }
            }).start();
        }
    }

    // Hilfsmethode um lokale IP-Adresse zu ermitteln
    private static String getLocalIPAddress() {
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (Exception e) {
            return "IP nicht ermittelbar - verwende ipconfig in cmd";
        }
    }
}