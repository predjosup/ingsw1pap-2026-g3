package ch.supsi.dti.backend.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public final class RoundRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDateTime timestamp;
    private final int bet;
    private final RoundOutcome outcome;
    private final int playerScore;
    private final int dealerScore;
    private final int balanceAfter;

    public RoundRecord(LocalDateTime timestamp,
                       int bet,
                       RoundOutcome outcome,
                       int playerScore,
                       int dealerScore,
                       int balanceAfter) {
        this.timestamp = timestamp;
        this.bet = bet;
        this.outcome = outcome;
        this.playerScore = playerScore;
        this.dealerScore = dealerScore;
        this.balanceAfter = balanceAfter;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public int bet() {
        return bet;
    }

    public RoundOutcome outcome() {
        return outcome;
    }

    public int playerScore() {
        return playerScore;
    }

    public int dealerScore() {
        return dealerScore;
    }

    public int balanceAfter() {
        return balanceAfter;
    }
}
