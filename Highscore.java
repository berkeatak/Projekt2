package Projekt_Quiz;

import java.io.*;
import java.util.*;

public class Highscore {
    private static final String HIGHSCORE_FILE = "highscores.txt";

    public static void speichere(String name, long zeitMillis) {
        try (PrintWriter out = new PrintWriter(new FileWriter(HIGHSCORE_FILE, true))) {
            out.println(name + ";" + zeitMillis);
        } catch (IOException ignored) {}
    }

    public static List<HighscoreEntry> ladeAlle() {
        List<HighscoreEntry> entries = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    try {
                        entries.add(new HighscoreEntry(parts[0], Long.parseLong(parts[1])));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException ignored) {}
        entries.sort(Comparator.comparingLong(e -> e.zeitMillis));
        return entries;
    }

    public static int getPlatzVon(String name, long zeitMillis) {
        List<HighscoreEntry> alle = ladeAlle();
        for (int i = 0; i < alle.size(); i++) {
            HighscoreEntry e = alle.get(i);
            if (e.name.equals(name) && e.zeitMillis == zeitMillis) {
                return i + 1; // 1-basiert
            }
        }
        return -1;
    }

    public static List<HighscoreEntry> ladeTopN(int n) {
        List<HighscoreEntry> alle = ladeAlle();
        return alle.subList(0, Math.min(n, alle.size()));
    }

    // Hilfsklasse für Einträge
    public static class HighscoreEntry {
        public final String name;
        public final long zeitMillis;

        public HighscoreEntry(String name, long zeitMillis) {
            this.name = name;
            this.zeitMillis = zeitMillis;
        }
    }
}