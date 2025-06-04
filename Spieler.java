package Projekt_Quiz;

public class Spieler {
    private String name;
    private int leben;
    private long zeit; // Zeit in Millisekunden
    private boolean nochImSpiel = true;

    public Spieler(String name) {
        this.name = name;
        this.leben = 3;
    }

    public String getName() {
        return name;
    }

    public int getLeben() {
        return leben;
    }

    public void verliereLeben(int anzahl) {
        leben -= anzahl;
        if (leben <= 0) {
            leben = 0;
            nochImSpiel = false;
        }
    }

    public boolean istNochImSpiel() {
        return nochImSpiel && leben > 0;
    }

    public long getZeit() {
        return zeit;
    }

    public void setZeit(long zeit) {
        this.zeit = zeit;
    }
}