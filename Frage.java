package Projekt_Quiz;

import java.util.List;

public class Frage {
    private String frageText;
    private List<String> antwortMoeglichkeiten;
    private int richtigeAntwortIndex;

    public Frage(String frageText, List<String> antwortMoeglichkeiten, int richtigeAntwortIndex) {
        this.frageText = frageText;
        this.antwortMoeglichkeiten = antwortMoeglichkeiten;
        this.richtigeAntwortIndex = richtigeAntwortIndex;
    }

    public String getFrageText() {
        return frageText;
    }

    public List<String> getAntwortMoeglichkeiten() {
        return antwortMoeglichkeiten;
    }

    public boolean istAntwortRichtig(int index) {
        return index == richtigeAntwortIndex;
    }

    public int getRichtigeAntwortIndex() {
        return richtigeAntwortIndex;
    }
}