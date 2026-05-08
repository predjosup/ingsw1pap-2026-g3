package ch.supsi.dti.backend.service;

import ch.supsi.dti.backend.model.GamePhase;

public final class GameService {

    private static final int INITIAL_BALANCE = 100;
    private static final int MIN_BET = 10;

    private String playerName = "";
    private int balance = INITIAL_BALANCE;
    private GamePhase phase = GamePhase.WAITING_BET;

    public String playerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return;
        }
        this.playerName = playerName.trim();
    }

    public int balance() {
        return balance;
    }

    public int minBet() {
        return MIN_BET;
    }

    public GamePhase phase() {
        return phase;
    }

    public boolean isGameOver() {
        return phase == GamePhase.GAME_OVER;
    }

    public void startNewGame() {
        balance = INITIAL_BALANCE;
        phase = GamePhase.WAITING_BET;
    }

    public void markGameOver() {
        balance = 0;
        phase = GamePhase.GAME_OVER;
    }
}

