package ch.supsi.dti.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Deck implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Card> cards = new ArrayList<>();
    private final Random random = new Random();
    private final int deckCount;

    public Deck(int deckCount) {
        if (deckCount < 1) {
            throw new IllegalArgumentException("deckCount must be >= 1");
        }
        this.deckCount = deckCount;
        refill();
    }

    public void refill() {
        cards.clear();
        for (int i = 0; i < deckCount; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(suit, rank));
                }
            }
        }
        Collections.shuffle(cards, random);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            refill();
        }
        return cards.remove(cards.size() - 1);
    }

    public int remaining() {
        return cards.size();
    }
}
