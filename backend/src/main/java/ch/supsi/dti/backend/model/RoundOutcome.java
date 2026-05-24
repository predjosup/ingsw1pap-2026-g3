package ch.supsi.dti.backend.model;

import java.io.Serializable;

public enum RoundOutcome implements Serializable {
    PLAYER_BLACKJACK,
    PLAYER_WIN,
    DEALER_WIN,
    PUSH
}
