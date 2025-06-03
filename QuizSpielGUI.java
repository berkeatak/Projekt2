package Projekt_Quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class QuizSpielGUI {

    private JFrame frame;
    private JLabel raumLabel;
    private JLabel frageLabel;
    private JButton[] antwortButtons;
    private JLabel lebenLabel;
    private JLabel timerLabel;

    private Spieler spieler;
    public List<Spieler> spielerListe;
    private List<Frage> fragen;
    private List<String> raumTexte;
    private int aktuelleFrageIndex = 0;
    private Timer countdownTimer;
    private int zeitProFrage = 30;
    private int verbleibendeZeit;
    private int falscheAntwortenAufFrage = 0;
    private long startZeit;
    private boolean beideVerbunden = false;
    private boolean istMehrspielerModus;
    private boolean spielBeendet = false;

    // Für Live-Rangliste im Wartefenster
    private QuizClient client;
    private JFrame warteFrame;
    private JPanel rangPanel;
    private boolean warteAufEnde = false;

    public QuizSpielGUI(Spieler spieler, int schwierigkeitsgrad, List<Spieler> spielerListe, QuizClient client) {
        this.spieler = spieler;
        this.fragen = frageListeErstellen(schwierigkeitsgrad);
        this.raumTexte = raumTexteErstellen();
        this.spielerListe = new ArrayList<>(spielerListe);
        this.startZeit = System.currentTimeMillis();
        this.istMehrspielerModus = spielerListe.size() > 1;
        this.client = client;

        erzeugeGUI();

        if (spielerListe.size() >= 2) {
            beideVerbunden = true;
            zeigeNaechsteFrage();
        } else {
            frageLabel.setText("Warte auf zweiten Spieler...");
        }
    }

    public void spielerVerbunden() {
        if (spielerListe.size() >= 2) {
            beideVerbunden = true;
            SwingUtilities.invokeLater(() -> {
                frageLabel.setText("Zweiter Spieler verbunden! Das Spiel beginnt.");
                zeigeNaechsteFrage();
            });
        }
    }

    private List<Frage> frageListeErstellen(int schwierigkeitsgrad) {
        List<Frage> fragen = new ArrayList<>();
        switch (schwierigkeitsgrad) {
            case 0:
                fragen.add(new Frage("Was ist die Farbe des Himmels an einem klaren Tag?",
                        Arrays.asList("Grün", "Blau", "Rot"), 1));
                fragen.add(new Frage("Welches Tier ist bekannt als 'der König des Dschungels'?",
                        Arrays.asList("Elefant", "Löwe", "Tiger"), 1));
                fragen.add(new Frage("Wie viele Beine hat eine Spinne?",
                        Arrays.asList("6", "8", "10"), 1));
                fragen.add(new Frage("Welche Farbe entsteht, wenn man Rot und Gelb mischt?",
                        Arrays.asList("Orange", "Lila", "Grün"), 0));
                break;
            case 1:
                fragen.add(new Frage("Welches ist das größte Säugetier der Welt?",
                        Arrays.asList("Elefant", "Blauwal", "Giraffe"), 1));
                fragen.add(new Frage("Welcher Planet ist als 'Roter Planet' bekannt?",
                        Arrays.asList("Venus", "Mars", "Jupiter"), 1));
                fragen.add(new Frage("In welchem Jahr fiel die Berliner Mauer?",
                        Arrays.asList("1987", "1989", "1991"), 1));
                fragen.add(new Frage("Welches Land hat die meisten Zeitzonen?",
                        Arrays.asList("USA", "Russland", "China"), 1));
                break;
            case 2:
                fragen.add(new Frage("Welches Element hat das chemische Symbol 'Au'?",
                        Arrays.asList("Silber", "Gold", "Kupfer"), 1));
                fragen.add(new Frage("Wer schrieb 'Faust'?",
                        Arrays.asList("Friedrich Schiller", "Johann Wolfgang von Goethe", "Gotthold Ephraim Lessing"), 1));
                fragen.add(new Frage("Was ist die Quadratwurzel von 144?",
                        Arrays.asList("11", "12", "13"), 1));
                fragen.add(new Frage("Welcher Philosoph schrieb 'Sein und Zeit'?",
                        Arrays.asList("Kant", "Heidegger", "Nietzsche"), 1));
                break;
        }
        return fragen;
    }

    private List<String> raumTexteErstellen() {
        return Arrays.asList(
                "🧪 Raum 1: Labor – Du wachst auf und musst entkommen.",
                "🔐 Raum 2: Sicherheitsraum – Nur mit Logik kommst du weiter.",
                "📻 Raum 3: Kommunikationszentrale – Ein Funkspruch?",
                "🎹 Raum 4: Musikzimmer – Ein Piano scheint wichtig zu sein..."
        );
    }

    private void erzeugeGUI() {
        frame = new JFrame("Escape Room Quiz - " + spieler.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        Font labelFont = new Font("Arial", Font.PLAIN, 18);

        lebenLabel = new JLabel("Leben: " + spieler.getLeben());
        lebenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lebenLabel.setFont(labelFont);
        frame.add(lebenLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        raumLabel = new JLabel("", SwingConstants.CENTER);
        raumLabel.setFont(labelFont);
        frageLabel = new JLabel("Warte auf zweiten Spieler...", SwingConstants.CENTER);
        frageLabel.setFont(labelFont);
        timerLabel = new JLabel("Zeit: 30", SwingConstants.CENTER);
        timerLabel.setFont(labelFont);

        centerPanel.add(raumLabel);
        centerPanel.add(frageLabel);
        centerPanel.add(timerLabel);
        frame.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        antwortButtons = new JButton[3];
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);

        for (int i = 0; i < antwortButtons.length; i++) {
            antwortButtons[i] = new JButton();
            antwortButtons[i].setFont(buttonFont);
            antwortButtons[i].setPreferredSize(new Dimension(200, 50));
            buttonPanel.add(antwortButtons[i]);
            antwortButtons[i].setVisible(false);
        }

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void zeigeNaechsteFrage() {
        if (!beideVerbunden || spielBeendet) {
            return;
        }

        if (aktuelleFrageIndex >= fragen.size() || !spieler.istNochImSpiel()) {
            beendeSpiel();
            return;
        }

        falscheAntwortenAufFrage = 0;

        Frage frage = fragen.get(aktuelleFrageIndex);
        frageLabel.setText("<html><center>" + frage.getFrageText() + "</center></html>");

        if (aktuelleFrageIndex < raumTexte.size()) {
            raumLabel.setText(raumTexte.get(aktuelleFrageIndex));
        }

        for (JButton button : antwortButtons) {
            ActionListener[] listeners = button.getActionListeners();
            for (ActionListener listener : listeners) {
                button.removeActionListener(listener);
            }
        }

        List<String> antworten = frage.getAntwortMoeglichkeiten();
        for (int i = 0; i < antwortButtons.length; i++) {
            if (i < antworten.size()) {
                antwortButtons[i].setText(antworten.get(i));
                antwortButtons[i].setEnabled(true);
                antwortButtons[i].setVisible(true);
                final int buttonIndex = i;
                antwortButtons[i].addActionListener(e -> verarbeiteAntwort(buttonIndex));
            } else {
                antwortButtons[i].setVisible(false);
            }
        }

        starteCountdown();
    }

    private void starteCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        verbleibendeZeit = zeitProFrage;
        timerLabel.setText("Zeit: " + verbleibendeZeit);

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                verbleibendeZeit--;
                SwingUtilities.invokeLater(() -> timerLabel.setText("Zeit: " + verbleibendeZeit));

                if (verbleibendeZeit <= 0) {
                    countdownTimer.cancel();
                    SwingUtilities.invokeLater(() -> verarbeiteFalscheAntwort(true));
                }
            }
        }, 0, 1000);
    }

    private void verarbeiteAntwort(int auswahlIndex) {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        for (JButton button : antwortButtons) {
            button.setEnabled(false);
        }

        Frage frage = fragen.get(aktuelleFrageIndex);

        if (frage.istAntwortRichtig(auswahlIndex)) {
            JOptionPane.showMessageDialog(frame, "Richtig! Die Tür öffnet sich...");
            aktuelleFrageIndex++;
            zeigeNaechsteFrage();
        } else {
            verarbeiteFalscheAntwort(false);
        }
    }

    private void verarbeiteFalscheAntwort(boolean zeitUeberschritten) {
        falscheAntwortenAufFrage++;
        int lebenVerlust = 1;
        String nachricht = "Falsch! Du verlierst 1 Leben. Versuch es nochmal.";

        if (falscheAntwortenAufFrage >= 2 || zeitUeberschritten) {
            lebenVerlust = 2;
            nachricht = zeitUeberschritten ?
                    "Zeit abgelaufen! Du verlierst 2 Leben." :
                    "Zweiter Fehler! Du verlierst 2 Leben.";
            falscheAntwortenAufFrage = 0;
        }

        spieler.verliereLeben(lebenVerlust);
        JOptionPane.showMessageDialog(frame, nachricht);
        lebenLabel.setText("Leben: " + spieler.getLeben());

        if (!spieler.istNochImSpiel()) {
            beendeSpiel();
        } else if (!zeitUeberschritten && falscheAntwortenAufFrage < 2) {
            zeigeAktuelleFrage();
        } else {
            aktuelleFrageIndex++;
            zeigeNaechsteFrage();
        }
    }

    private void zeigeAktuelleFrage() {
        zeigeNaechsteFrage();
    }

    private void beendeSpiel() {
        if (spielBeendet) return;
        spielBeendet = true;

        if (countdownTimer != null) countdownTimer.cancel();
        for (JButton button : antwortButtons) button.setEnabled(false);

        long endZeit = System.currentTimeMillis();
        spieler.setZeit(endZeit - startZeit);

        // Zeit an den Server schicken
        if (client != null) {
            client.sendeFertigNachricht(spieler);
        }

        if (istMehrspielerModus) {
            öffneWartefensterMitRangliste();
        } else {
            zeigeEndergebnisse();
        }
    }

    private void öffneWartefensterMitRangliste() {
        warteFrame = new JFrame("Warte auf andere Spieler...");
        warteFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        warteFrame.setSize(450, 400);
        warteFrame.setLocationRelativeTo(frame);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel info = new JLabel("<html><center>Du hast das Spiel beendet!<br>Warte auf andere Spieler...</center></html>", SwingConstants.CENTER);
        content.add(info, BorderLayout.NORTH);

        JLabel zeitLabel = new JLabel("⏱️ Deine Zeit: " + (spieler.getZeit() / 1000) + " Sekunden", SwingConstants.CENTER);
        zeitLabel.setFont(new Font("Arial", Font.BOLD, 16));
        content.add(zeitLabel, BorderLayout.CENTER);

        rangPanel = new JPanel();
        rangPanel.setLayout(new BoxLayout(rangPanel, BoxLayout.Y_AXIS));
        rangPanel.setBorder(BorderFactory.createTitledBorder("Bisherige Platzierung"));

        JScrollPane scrollPane = new JScrollPane(rangPanel);
        scrollPane.setPreferredSize(new Dimension(380, 180));
        content.add(scrollPane, BorderLayout.SOUTH);

        warteFrame.add(content);
        warteFrame.setVisible(true);
        warteAufEnde = true;

        // Timer, der prüft, ob das Spiel vorbei ist
        Timer warteTimer = new Timer();
        warteTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                if (client != null && client.isFinaleRanglisteEmpfangen()) {
                    warteAufEnde = false;
                    this.cancel();
                    warteFrame.dispose();
                    SwingUtilities.invokeLater(() -> zeigeEndergebnisse());
                } else {
                    List<String> rangListe = client != null ? client.getAktuelleRangliste() : Collections.emptyList();
                    aktualisiereRangPanel(rangListe);
                }
            }
        }, 0, 1000);
    }

    public void aktualisiereWartefensterRangliste() {
        if (warteFrame != null && rangPanel != null && warteAufEnde) {
            List<String> rangListe = client != null ? client.getAktuelleRangliste() : Collections.emptyList();
            aktualisiereRangPanel(rangListe);
        }
    }

    // Wird von Client aufgerufen, wenn finale Rangliste eintrifft
    public void aktualisiereFinaleRangliste() {
        if (warteFrame != null && rangPanel != null && warteAufEnde) {
            List<String> rangListe = client != null ? client.getFinaleRangliste() : Collections.emptyList();
            aktualisiereRangPanel(rangListe);
        }
    }

    private void aktualisiereRangPanel(List<String> rangListe) {
        SwingUtilities.invokeLater(() -> {
            rangPanel.removeAll();
            int meinRang = -1;
            for (int i = 0; i < rangListe.size(); i++) {
                String text = rangListe.get(i);
                JLabel l = new JLabel(text, SwingConstants.LEFT);
                if (text.contains(spieler.getName())) {
                    l.setFont(new Font("Arial", Font.BOLD, 14));
                    l.setForeground(Color.BLUE);
                    meinRang = i + 1;
                } else {
                    l.setFont(new Font("Arial", Font.PLAIN, 14));
                }
                rangPanel.add(l);
            }
            if (meinRang > 0) {
                JLabel platz = new JLabel("Dein aktueller Platz: " + meinRang, SwingConstants.CENTER);
                platz.setFont(new Font("Arial", Font.BOLD, 15));
                rangPanel.add(platz);
            }
            rangPanel.revalidate();
            rangPanel.repaint();
        });
    }

    private void zeigeEndergebnisse() {
        StringBuilder ergebnis = new StringBuilder();
        ergebnis.append("<html><center>");
        if (spielerListe.size() == 1) {
            if (spieler.istNochImSpiel()) {
                ergebnis.append("🎉 Du hast das Spiel erfolgreich beendet, ").append(spieler.getName()).append("!<br>");
                ergebnis.append("Deine Zeit: ").append(spieler.getZeit() / 1000).append(" Sekunden");
            } else {
                ergebnis.append("💀 Leider bist du im Escape Room gescheitert, ").append(spieler.getName()).append(".");
            }
        } else {
            // Finale Rangliste anzeigen (nach Spielende)
            List<String> rangListe = client != null ? client.getFinaleRangliste() : Collections.emptyList();
            ergebnis.append("<b>Rangliste:</b><br><br>");
            int meinRang = -1;
            for (int i = 0; i < rangListe.size(); i++) {
                ergebnis.append(rangListe.get(i)).append("<br>");
                if (rangListe.get(i).contains(spieler.getName())) {
                    meinRang = i + 1;
                }
            }
            if (meinRang > 0) {
                ergebnis.append("<br><b>Dein Platz: ").append(meinRang).append("</b><br>");
            }
            if (spieler.istNochImSpiel()) {
                ergebnis.append("<br>🎉 Du hast das Spiel erfolgreich beendet, ").append(spieler.getName()).append("!<br>");
                ergebnis.append("Deine Zeit: ").append(spieler.getZeit() / 1000).append(" Sekunden");
            } else {
                ergebnis.append("<br>💀 Leider bist du im Escape Room gescheitert, ").append(spieler.getName()).append(".");
            }
        }
        ergebnis.append("</center></html>");
        JOptionPane.showMessageDialog(frame, ergebnis.toString(), "Spiel beendet", JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
    }
}