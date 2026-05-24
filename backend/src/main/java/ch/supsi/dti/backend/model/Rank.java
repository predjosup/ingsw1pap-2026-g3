package ch.supsi.dti.backend.model;

import java.io.Serializable;

public enum Rank implements Serializable {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 10),
    QUEEN("Q", 10),
    KING("K", 10),
    ACE("A", 11);

    private final String shortName;
    private final int value;

    Rank(String shortName, int value) {
        this.shortName = shortName;
        this.value = value;
    }

    public String shortName() {
        return shortName;
    }

    public int value() {
        return value;
    }
}
