package ch.supsi.dti.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Hand implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("card must not be null");
        }
        cards.add(card);
    }

    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    public int score() {
        int total = 0;
        int aces = 0;

        for (Card card : cards) {
            total += card.value();
            if (card.rank() == Rank.ACE) {
                aces++;
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public boolean isBust() {
        return score() > 21;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && score() == 21;
    }

    public String cardsText() {
        if (cards.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(cards.get(i));
        }
        return sb.toString();
    }
}
