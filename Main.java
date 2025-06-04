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
        ModusAuswahlDialog dialog = new ModusAuswahlDialog(null);
        dialog.setVisible(true);
        int modusAuswahl = dialog.gewaehlterModus;
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
            letzterSchwierigkeitsgrad = waehleSchwierigkeitsgrad();
            if (letzterSchwierigkeitsgrad == -1) { showMainMenu(); return; }

            letzterName = waehleName();
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
            MehrspielerAuswahlDialog mehrDialog = new MehrspielerAuswahlDialog(null);
            mehrDialog.setVisible(true);
            int mehrspielerAuswahl = mehrDialog.auswahl;
            if (mehrspielerAuswahl == 2 || mehrspielerAuswahl == -1) { showMainMenu(); return; }

            letzterSchwierigkeitsgrad = waehleSchwierigkeitsgrad();
            if (letzterSchwierigkeitsgrad == -1) { showMainMenu(); return; }

            if (mehrspielerAuswahl == 0) { // Raum erstellen
                letzterName = waehleName();
                if (letzterName == null || letzterName.isEmpty()) letzterName = "Host";
                final String finalHostName = letzterName;

                String ipAddress = getLocalIPAddress();
                String serverInfo = "Server wird gestartet...\n" +
                        "Port: 6666\n" +
                        "Deine IP-Adresse: " + ipAddress + "\n\n" +
                        "Teile diese IP-Adresse mit dem anderen Spieler!\n" +
                        "Warte auf zweiten Spieler...";
                JOptionPane.showMessageDialog(null, serverInfo, "Server Info", JOptionPane.INFORMATION_MESSAGE);

                QuizServer server = new QuizServer();
                new Thread(() -> {
                    try {
                        server.start(6666, letzterSchwierigkeitsgrad, finalHostName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null, "Fehler beim Starten des Servers: " + e.getMessage())
                        );
                    }
                }).start();
            } else if (mehrspielerAuswahl == 1) { // Raum beitreten
                ServerAdresseDialog serverDialog = new ServerAdresseDialog(null);
                serverDialog.setVisible(true);
                letzterServerAddress = serverDialog.serverAdresse;
                if (letzterServerAddress == null || letzterServerAddress.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Keine Server-Adresse eingegeben!");
                    showMainMenu();
                    return;
                }
                letzterName = waehleName();
                if (letzterName == null || letzterName.isEmpty()) letzterName = "Client";
                final String finalServerAddress = letzterServerAddress.trim();
                final String finalClientName = letzterName;
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
        SchwierigkeitsgradDialog schwierigDialog = new SchwierigkeitsgradDialog(null);
        schwierigDialog.setVisible(true);
        return schwierigDialog.schwierigkeitsgrad;
    }

    public static String waehleName() {
        NameEingabeDialog nameDialog = new NameEingabeDialog(null);
        nameDialog.setVisible(true);
        return nameDialog.spielerName;
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