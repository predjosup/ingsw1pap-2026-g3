package ch.supsi.dti.backend.model;

import java.io.Serializable;

public enum GamePhase implements Serializable {
    WAITING_BET,
    PLAYER_TURN,
    ROUND_ENDED,
    GAME_OVER
}
