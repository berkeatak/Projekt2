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
    private JLabel fortschrittLabel;
    private JLabel lebenTextLabel;
    private JButton[] antwortButtons;
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

    // Farben für Escape Room Feeling
    public static final Color PRIMARY_BG = new Color(33, 37, 43);
    public static final Color PANEL_BG = new Color(44, 47, 51);
    public static final Color PRIMARY_FG = new Color(255, 255, 255);
    public static final Color ACCENT = new Color(0x5CDB95);
    public static final Color BUTTON_BG = new Color(44, 62, 80);
    public static final Color BUTTON_FG = new Color(236, 240, 241);
    public static final Color BUTTON_CORRECT = new Color(76, 175, 80);
    public static final Color BUTTON_WRONG = new Color(224, 60, 60);

    public QuizSpielGUI(Spieler spieler, int schwierigkeitsgrad, List<Spieler> spielerListe, QuizClient client) {
        this.spieler = spieler;
        this.fragen = frageListeErstellen(schwierigkeitsgrad);
        Collections.shuffle(this.fragen);
        this.raumTexte = raumTexteErstellen();
        this.spielerListe = new ArrayList<>(spielerListe);
        this.startZeit = System.currentTimeMillis();
        this.istMehrspielerModus = (spielerListe != null && spielerListe.size() > 1);
        this.client = client;

        erzeugeGUI();

        if (!istMehrspielerModus) {
            beideVerbunden = true;
            zeigeNaechsteFrage();
        } else {
            if (spielerListe.size() >= 2) {
                beideVerbunden = true;
                zeigeNaechsteFrage();
            } else {
                frageLabel.setText("Warte auf zweiten Spieler...");
            }
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
        frame.setSize(650, 600);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBackground(PRIMARY_BG);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1, 0, 6));
        topPanel.setBackground(PANEL_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 10, 0));

        lebenTextLabel = new JLabel("Leben: " + spieler.getLeben(), SwingConstants.CENTER);
        lebenTextLabel.setFont(new Font("Arial", Font.BOLD, 22));
        lebenTextLabel.setForeground(ACCENT);
        lebenTextLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        topPanel.add(lebenTextLabel);

        fortschrittLabel = new JLabel("Fortschritt", SwingConstants.CENTER);
        fortschrittLabel.setFont(new Font("Arial", Font.BOLD, 18));
        fortschrittLabel.setForeground(PRIMARY_FG);
        topPanel.add(fortschrittLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(PRIMARY_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        raumLabel = new JLabel("", SwingConstants.CENTER);
        raumLabel.setFont(new Font("Arial", Font.ITALIC, 17));
        raumLabel.setForeground(ACCENT);
        raumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(raumLabel);

        frageLabel = new JLabel("", SwingConstants.CENTER);
        frageLabel.setFont(new Font("Arial", Font.BOLD, 21));
        frageLabel.setForeground(Color.WHITE);
        frageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frageLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        centerPanel.add(frageLabel);

        timerLabel = new JLabel("Zeit: 30", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        timerLabel.setForeground(new Color(255, 122, 89));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        centerPanel.add(timerLabel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(PRIMARY_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 70, 20, 70));

        antwortButtons = new JButton[3];
        for (int i = 0; i < antwortButtons.length; i++) {
            antwortButtons[i] = new JButton();
            antwortButtons[i].setFont(new Font("Arial", Font.BOLD, 19));
            antwortButtons[i].setBackground(BUTTON_BG);
            antwortButtons[i].setForeground(BUTTON_FG);
            antwortButtons[i].setFocusPainted(false);
            antwortButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 2),
                    BorderFactory.createEmptyBorder(10, 8, 10, 8)
            ));
            antwortButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            antwortButtons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            buttonPanel.add(antwortButtons[i]);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 13)));
            antwortButtons[i].setVisible(false);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private void updateLebenAnzeige() {
        lebenTextLabel.setText("Leben: " + spieler.getLeben());
    }

    private void updateZeitAnzeige() {
        timerLabel.setText("Zeit: " + verbleibendeZeit);
    }

    private void zeigeNaechsteFrage() {
        if (!beideVerbunden || spielBeendet) {
            return;
        }

        verbleibendeZeit = zeitProFrage;
        updateLebenAnzeige();
        updateZeitAnzeige();

        if (aktuelleFrageIndex >= fragen.size() || !spieler.istNochImSpiel()) {
            beendeSpiel();
            return;
        }

        fortschrittLabel.setText("Frage " + (aktuelleFrageIndex + 1) + " von " + fragen.size());

        falscheAntwortenAufFrage = 0;

        Frage frage = fragen.get(aktuelleFrageIndex);
        frageLabel.setText("<html><div style='text-align:center;'>" + frage.getFrageText() + "</div></html>");

        if (aktuelleFrageIndex < raumTexte.size()) {
            raumLabel.setText(raumTexte.get(aktuelleFrageIndex));
        } else {
            raumLabel.setText("");
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
                antwortButtons[i].setBackground(BUTTON_BG);
                antwortButtons[i].setForeground(BUTTON_FG);
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
        updateZeitAnzeige();

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                verbleibendeZeit--;
                SwingUtilities.invokeLater(() -> updateZeitAnzeige());
                if (verbleibendeZeit <= 0) {
                    countdownTimer.cancel();
                    SwingUtilities.invokeLater(() -> verarbeiteFalscheAntwort(true));
                }
            }
        }, 1000, 1000);
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
            antwortButtons[auswahlIndex].setBackground(BUTTON_CORRECT);
        } else {
            antwortButtons[auswahlIndex].setBackground(BUTTON_WRONG);
            int richtige = frage.getRichtigeAntwortIndex();
            if (frage.getAntwortMoeglichkeiten().size() > richtige && richtige >= 0) {
                antwortButtons[richtige].setBackground(BUTTON_CORRECT);
            }
        }

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    for (JButton button : antwortButtons) {
                        button.setBackground(BUTTON_BG);
                        button.setForeground(BUTTON_FG);
                    }

                    if (frage.istAntwortRichtig(auswahlIndex)) {
                        aktuelleFrageIndex++;
                        zeigeNaechsteFrage();
                    } else {
                        verarbeiteFalscheAntwort(false);
                    }
                });
            }
        }, 700);
    }

    private void verarbeiteFalscheAntwort(boolean zeitUeberschritten) {
        if (zeitUeberschritten) {
            spieler.verliereLeben(2);
            updateLebenAnzeige();
            JOptionPane.showMessageDialog(frame, "Zeit abgelaufen! Du verlierst 2 Leben.");
            aktuelleFrageIndex++;
            zeigeNaechsteFrage();
            return;
        }

        falscheAntwortenAufFrage++;
        if (falscheAntwortenAufFrage == 1) {
            spieler.verliereLeben(1);
            updateLebenAnzeige();
            JOptionPane.showMessageDialog(frame, "Falsch! Du verlierst 1 Leben. Versuch es nochmal.");
            zeigeAktuelleFrage();
        } else {
            spieler.verliereLeben(2);
            updateLebenAnzeige();
            JOptionPane.showMessageDialog(frame, "Zweiter Fehler! Du verlierst 2 Leben.");
            falscheAntwortenAufFrage = 0;
            aktuelleFrageIndex++;
            zeigeNaechsteFrage();
        }
    }

    private void zeigeAktuelleFrage() {
        verbleibendeZeit = zeitProFrage;
        updateZeitAnzeige();
        Frage frage = fragen.get(aktuelleFrageIndex);
        frageLabel.setText("<html><div style='text-align:center;'>" + frage.getFrageText() + "</div></html>");
        for (int i = 0; i < antwortButtons.length; i++) {
            antwortButtons[i].setEnabled(true);
            antwortButtons[i].setBackground(BUTTON_BG);
            antwortButtons[i].setForeground(BUTTON_FG);
        }
        starteCountdown();
    }

    private void beendeSpiel() {
        if (spielBeendet) return;
        spielBeendet = true;

        if (countdownTimer != null) countdownTimer.cancel();
        for (JButton button : antwortButtons) button.setEnabled(false);

        long endZeit = System.currentTimeMillis();
        spieler.setZeit(endZeit - startZeit);

        if (client != null) {
            client.sendeFertigNachricht(spieler);
        }

        boolean istEinzelspielerErfolg = !istMehrspielerModus && spieler.istNochImSpiel();
        if (istEinzelspielerErfolg) {
            Highscore.speichere(spieler.getName(), spieler.getZeit());
        }

        if (istMehrspielerModus) {
            öffneWartefensterMitRangliste();
        } else {
            if (istEinzelspielerErfolg) {
                SwingUtilities.invokeLater(() -> zeigeHighscoreFenster(spieler.getName(), spieler.getZeit()));
            }
            zeigeEndergebnisse();
        }
    }

    private void öffneWartefensterMitRangliste() {
        warteFrame = new JFrame("Warte auf andere Spieler...");
        warteFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        warteFrame.setSize(450, 400);
        warteFrame.setLocationRelativeTo(frame);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(PANEL_BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel info = new JLabel("<html><center>Du hast das Spiel beendet!<br>Warte auf andere Spieler...</center></html>", SwingConstants.CENTER);
        info.setForeground(PRIMARY_FG);
        content.add(info, BorderLayout.NORTH);

        JLabel zeitLabel = new JLabel("⏱️ Deine Zeit: " + (spieler.getZeit() / 1000) + " Sekunden", SwingConstants.CENTER);
        zeitLabel.setFont(new Font("Arial", Font.BOLD, 16));
        zeitLabel.setForeground(ACCENT);
        content.add(zeitLabel, BorderLayout.CENTER);

        rangPanel = new JPanel();
        rangPanel.setBackground(PANEL_BG);
        rangPanel.setLayout(new BoxLayout(rangPanel, BoxLayout.Y_AXIS));
        rangPanel.setBorder(BorderFactory.createTitledBorder("Bisherige Platzierung"));

        JScrollPane scrollPane = new JScrollPane(rangPanel);
        scrollPane.setPreferredSize(new Dimension(380, 180));
        content.add(scrollPane, BorderLayout.SOUTH);

        warteFrame.setContentPane(content);
        warteFrame.setVisible(true);
        warteAufEnde = true;

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
                l.setForeground(PRIMARY_FG);
                if (text.contains(spieler.getName())) {
                    l.setFont(new Font("Arial", Font.BOLD, 14));
                    l.setForeground(ACCENT);
                    meinRang = i + 1;
                } else {
                    l.setFont(new Font("Arial", Font.PLAIN, 14));
                }
                rangPanel.add(l);
            }
            if (meinRang > 0) {
                JLabel platz = new JLabel("Dein aktueller Platz: " + meinRang, SwingConstants.CENTER);
                platz.setFont(new Font("Arial", Font.BOLD, 15));
                platz.setForeground(ACCENT);
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

        // EscapeRoom-Style-EndeDialog (3 Buttons)
        EndeDialog dialog = new EndeDialog(null, ergebnis.toString());
        dialog.setVisible(true);
        frame.dispose();
        if (dialog.auswahl == 0) {
            Main.restartLastMode();
        } else if (dialog.auswahl == 1) {
            Main.showMainMenu();
        } else {
            System.exit(0);
        }
    }

    // Highscore-Fenster: Top 5 + eigener Platz
    private void zeigeHighscoreFenster(String myName, long myZeit) {
        List<Highscore.HighscoreEntry> highs = Highscore.ladeTopN(5);
        int meinPlatz = Highscore.getPlatzVon(myName, myZeit);

        JFrame highscoreFrame = new JFrame("Highscore");
        highscoreFrame.setSize(340, 280);
        highscoreFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        highscoreFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("🏆 Top 5 Highscores 🏆");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        boolean selbstInTop5 = false;
        for (int i = 0; i < highs.size(); i++) {
            Highscore.HighscoreEntry e = highs.get(i);
            JLabel l = new JLabel((i+1) + ". " + e.name + ": " + (e.zeitMillis/1000) + " Sekunden");
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            l.setForeground(PRIMARY_FG);
            if (e.name.equals(myName) && e.zeitMillis == myZeit) {
                l.setFont(new Font("Arial", Font.BOLD, 16));
                l.setForeground(ACCENT);
                selbstInTop5 = true;
            } else {
                l.setFont(new Font("Arial", Font.PLAIN, 16));
            }
            panel.add(l);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        if (!selbstInTop5 && meinPlatz > 0) {
            JLabel platz = new JLabel("Dein Platz: " + meinPlatz);
            platz.setAlignmentX(Component.CENTER_ALIGNMENT);
            platz.setFont(new Font("Arial", Font.BOLD, 15));
            platz.setForeground(ACCENT);
            panel.add(platz);
        }

        highscoreFrame.setContentPane(panel);
        highscoreFrame.setVisible(true);
    }

    // --- Utility für andere Fenster im selben Style ---
    public static void styleDialog(JFrame dialogFrame, JPanel content) {
        dialogFrame.setContentPane(content);
        dialogFrame.getContentPane().setBackground(PANEL_BG);
        dialogFrame.setLocationRelativeTo(null);
        dialogFrame.setVisible(true);
    }
}
