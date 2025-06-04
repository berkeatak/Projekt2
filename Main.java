package Projekt_Quiz;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static int letzterModus = -1; // 0=Einzel, 1=Mehr
    private static int letzterSchwierigkeitsgrad = -1;
    private static String letzterName = null;
    private static String letzterServerAddress = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showMainMenu);
    }

    public static void showMainMenu() {
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
            letzterModus = 0;
            einzelspielerModus();
        } else if (modusAuswahl == 1) {
            letzterModus = 1;
            mehrspielerModus();
        } else {
            System.exit(0);
        }
    }

    public static void restartLastMode() {
        if (letzterModus == 0) {
            einzelspielerModus(true);
        } else if (letzterModus == 1) {
            mehrspielerModus(true);
        } else {
            showMainMenu();
        }
    }

    private static void einzelspielerModus() {
        einzelspielerModus(false);
    }
    private static void einzelspielerModus(boolean wiederholung) {
        if (!wiederholung) {
            String einfuehrungstext = "Willkommen zum Escape Room Quiz im Einzelspielermodus!\n\n"
                    + "In diesem Spiel musst du verschiedene Rätsel lösen, um aus den Räumen zu entkommen.\n"
                    + "Du hast drei Leben. Bei jeder falschen Antwort verlierst du ein Leben.\n"
                    + "Wenn du zweimal hintereinander falsch liegst oder die Zeit überschreitest, verlierst du zwei Leben.\n"
                    + "Viel Glück!";
            JOptionPane.showMessageDialog(null, einfuehrungstext, "Einführung", JOptionPane.INFORMATION_MESSAGE);

            letzterSchwierigkeitsgrad = waehleSchwierigkeitsgrad();
            if (letzterSchwierigkeitsgrad == -1) { showMainMenu(); return; }

            letzterName = JOptionPane.showInputDialog(null, "Wie heißt du?");
            if (letzterName == null || letzterName.isEmpty()) letzterName = "Spieler";
        }
        Spieler spieler = new Spieler(letzterName);
        List<Spieler> spielerListe = new ArrayList<>();
        spielerListe.add(spieler);
        new QuizSpielGUI(spieler, letzterSchwierigkeitsgrad, spielerListe, null);
    }

    private static void mehrspielerModus() { mehrspielerModus(false); }
    private static void mehrspielerModus(boolean wiederholung) {
        if (!wiederholung) {
            String[] mehrspielerOptionen = {"Raum erstellen", "Raum beitreten", "Zurück"};
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
            if (mehrspielerAuswahl == 2 || mehrspielerAuswahl == JOptionPane.CLOSED_OPTION) { showMainMenu(); return; }

            letzterSchwierigkeitsgrad = waehleSchwierigkeitsgrad();
            if (letzterSchwierigkeitsgrad == -1) { showMainMenu(); return; }

            if (mehrspielerAuswahl == 0) {
                letzterName = JOptionPane.showInputDialog(null, "Wie heißt du?");
                if (letzterName == null || letzterName.isEmpty()) letzterName = "Host";
                final String finalHostName = letzterName;

                String einfuehrungstext = "Willkommen zum Escape Room Quiz im Mehrspielermodus!\n\n" +
                        "Du hast einen Raum erstellt. Warte, bis ein anderer Spieler beitritt.\n" +
                        "Du hast drei Leben. Bei jeder falschen Antwort verlierst du ein Leben.\n" +
                        "Wenn du zweimal hintereinander falsch liegst oder die Zeit überschreitest, verlierst du zwei Leben.\n" +
                        "Viel Glück!";

                JOptionPane.showMessageDialog(null, einfuehrungstext, "Einführung", JOptionPane.INFORMATION_MESSAGE);
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
                        server.start(6666, letzterSchwierigkeitsgrad, finalHostName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null,
                                        "Fehler beim Starten des Servers: " + e.getMessage())
                        );
                    }
                }).start();
            } else if (mehrspielerAuswahl == 1) {
                letzterServerAddress = JOptionPane.showInputDialog(null,
                        "Gib die Server IP-Adresse ein:\n(z.B. 192.168.1.100)\nNICHT 'localhost' verwenden bei verschiedenen PCs!");
                if (letzterServerAddress == null || letzterServerAddress.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Keine Server-Adresse eingegeben!");
                    showMainMenu();
                    return;
                }
                letzterName = JOptionPane.showInputDialog(null, "Wie heißt du?");
                if (letzterName == null || letzterName.isEmpty()) letzterName = "Client";
                final String finalServerAddress = letzterServerAddress.trim();
                final String finalClientName = letzterName;
                String einfuehrungstext = "Willkommen zum Escape Room Quiz im Mehrspielermodus!\n\n" +
                        "Du trittst einem Raum bei. Das Spiel startet, sobald beide Spieler verbunden sind.\n" +
                        "Du hast drei Leben. Bei jeder falschen Antwort verlierst du ein Leben.\n" +
                        "Wenn du zweimal hintereinander falsch liegst oder die Zeit überschreitest, verlierst du zwei Leben.\n" +
                        "Viel Glück!";
                JOptionPane.showMessageDialog(null, einfuehrungstext, "Einführung", JOptionPane.INFORMATION_MESSAGE);
                JOptionPane.showMessageDialog(null, "Verbinde zum Server: " + finalServerAddress + ":6666\n\nBitte warten...");
                new Thread(() -> {
                    try {
                        QuizClient client = new QuizClient(finalServerAddress, 6666);
                        client.startConnection(letzterSchwierigkeitsgrad, finalClientName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null,
                                        "Fehler beim Verbinden zum Server!\n\n" +
                                                "Technischer Fehler: " + e.getMessage())
                        );
                    }
                }).start();
            }
        } else {
            if (letzterServerAddress == null || letzterName == null || letzterSchwierigkeitsgrad == -1) {
                showMainMenu();
                return;
            }
            if (letzterModus == 1 && letzterServerAddress != null) {
                String finalServerAddress = letzterServerAddress;
                String finalClientName = letzterName;
                new Thread(() -> {
                    try {
                        QuizClient client = new QuizClient(finalServerAddress, 6666);
                        client.startConnection(letzterSchwierigkeitsgrad, finalClientName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null,
                                        "Fehler beim Verbinden zum Server!\n\n" +
                                                "Technischer Fehler: " + e.getMessage())
                        );
                    }
                }).start();
            }
        }
    }

    public static int waehleSchwierigkeitsgrad() {
        while (true) {
            String[] schwierigkeitsgrade = {"Leicht", "Mittel", "Schwer"};
            int auswahl = JOptionPane.showOptionDialog(
                    null,
                    "Wähle den Schwierigkeitsgrad:",
                    "Schwierigkeitsgrad",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    schwierigkeitsgrade,
                    schwierigkeitsgrade[0]
            );
            if (auswahl == JOptionPane.CLOSED_OPTION || auswahl == -1) return -1;
            int bestaetigung = JOptionPane.showConfirmDialog(
                    null, "Wirklich '" + schwierigkeitsgrade[auswahl] + "' wählen?", "Bestätigen", JOptionPane.YES_NO_OPTION);
            if (bestaetigung == JOptionPane.YES_OPTION) return auswahl;
        }
    }

    private static String getLocalIPAddress() {
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (Exception e) {
            return "IP nicht ermittelbar - verwende ipconfig in cmd";
        }
    }
}