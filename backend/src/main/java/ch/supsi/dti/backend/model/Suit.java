package ch.supsi.dti.backend.model;

import java.io.Serializable;

public enum Suit implements Serializable {
    HEARTS("H"),
    DIAMONDS("D"),
    CLUBS("C"),
    SPADES("S");

    private final String shortName;

    Suit(String shortName) {
        this.shortName = shortName;
    }

    public String shortName() {
        return shortName;
    }
}
