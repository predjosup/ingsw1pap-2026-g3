package ch.supsi.dti.backend.service;

import ch.supsi.dti.backend.model.Card;
import ch.supsi.dti.backend.model.Deck;
import ch.supsi.dti.backend.model.GamePhase;
import ch.supsi.dti.backend.model.Hand;

public final class GameService {

    private static final int INITIAL_BALANCE = 100;
    private static final int MIN_BET = 10;

    private String playerName = "";
    private int balance = INITIAL_BALANCE;
    private GamePhase phase = GamePhase.WAITING_BET;
    private Deck deck = new Deck(1);
    private Hand playerHand = new Hand();
    private Hand dealerHand = new Hand();

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

    public int remainingCards() {
        return deck.remaining();
    }

    public Card drawCard() {
        return deck.draw();
    }

    public Hand playerHand() {
        return playerHand;
    }

    public Hand dealerHand() {
        return dealerHand;
    }

    public int playerScore() {
        return playerHand.score();
    }

    public int dealerScore() {
        return dealerHand.score();
    }

    public void addCardToPlayer(Card card) {
        playerHand.addCard(card);
    }

    public void addCardToDealer(Card card) {
        dealerHand.addCard(card);
    }

    public boolean canPlay() {
        return phase == GamePhase.PLAYER_TURN;
    }

    public void startRound() {
        playerHand = new Hand();
        dealerHand = new Hand();

        playerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());
        playerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());

        phase = GamePhase.PLAYER_TURN;
        if (playerHand.isBust()) {
            phase = GamePhase.ROUND_ENDED;
        }
    }

    public void hit() {
        if (!canPlay()) {
            return;
        }
        playerHand.addCard(deck.draw());
        if (playerHand.isBust()) {
            phase = GamePhase.ROUND_ENDED;
        }
    }

    public void stand() {
        if (!canPlay()) {
            return;
        }
        phase = GamePhase.ROUND_ENDED;
    }

    public void resetDeck(int deckCount) {
        deck = new Deck(deckCount);
    }

    public boolean isGameOver() {
        return phase == GamePhase.GAME_OVER;
    }

    public void startNewGame() {
        balance = INITIAL_BALANCE;
        phase = GamePhase.WAITING_BET;
        playerHand = new Hand();
        dealerHand = new Hand();
    }

    public void markGameOver() {
        balance = 0;
        phase = GamePhase.GAME_OVER;
    }
}
