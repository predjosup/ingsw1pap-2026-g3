package ch.supsi.dti.backend.model;

import java.io.Serializable;

public final class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        if (suit == null || rank == null) {
            throw new IllegalArgumentException("suit/rank must not be null");
        }
        this.suit = suit;
        this.rank = rank;
    }

    public Suit suit() {
        return suit;
    }

    public Rank rank() {
        return rank;
    }

    public int value() {
        return rank.value();
    }

    @Override
    public String toString() {
        return rank.shortName() + suit.shortName();
    }
}
