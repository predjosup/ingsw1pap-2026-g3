package ch.supsi.dti.backend.service;

import ch.supsi.dti.backend.model.Card;
import ch.supsi.dti.backend.model.Deck;
import ch.supsi.dti.backend.model.GamePhase;
import ch.supsi.dti.backend.model.Hand;
import ch.supsi.dti.backend.model.RoundOutcome;

public final class GameService {

    private static final int INITIAL_BALANCE = 100;
    private static final int MIN_BET = 10;

    private String playerName = "";
    private int balance = INITIAL_BALANCE;
    private GamePhase phase = GamePhase.WAITING_BET;
    private Deck deck = new Deck(1);
    private Hand playerHand = new Hand();
    private Hand dealerHand = new Hand();
    private RoundOutcome lastOutcome;
    private String status = "Set up a round.";

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

    public RoundOutcome lastOutcome() {
        return lastOutcome;
    }

    public String status() {
        return status;
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
        lastOutcome = null;

        playerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());
        playerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());

        phase = GamePhase.PLAYER_TURN;
        if (playerHand.isBlackjack() && dealerHand.isBlackjack()) {
            settleRound(RoundOutcome.PUSH, "Blackjack for both. Push.");
        } else if (playerHand.isBlackjack()) {
            settleRound(RoundOutcome.PLAYER_BLACKJACK, "Player has natural blackjack.");
        } else if (dealerHand.isBlackjack()) {
            settleRound(RoundOutcome.DEALER_WIN, "Dealer has blackjack.");
        } else {
            status = "Player turn.";
        }
    }

    public void hit() {
        if (!canPlay()) {
            return;
        }
        playerHand.addCard(deck.draw());
        if (playerHand.isBust()) {
            settleRound(RoundOutcome.DEALER_WIN, "Player busts.");
        } else {
            status = "Card drawn.";
        }
    }

    public void stand() {
        if (!canPlay()) {
            return;
        }
        playDealerTurn();
        if (dealerHand.isBust()) {
            settleRound(RoundOutcome.PLAYER_WIN, "Dealer busts.");
            return;
        }

        int player = playerHand.score();
        int dealer = dealerHand.score();
        if (player > dealer) {
            settleRound(RoundOutcome.PLAYER_WIN, "Player wins.");
        } else if (player < dealer) {
            settleRound(RoundOutcome.DEALER_WIN, "Dealer wins.");
        } else {
            settleRound(RoundOutcome.PUSH, "Push.");
        }
    }

    public void playDealerTurn() {
        while (dealerHand.score() < 17) {
            dealerHand.addCard(deck.draw());
        }
    }

    public boolean dealerBust() {
        return dealerHand.isBust();
    }

    private void settleRound(RoundOutcome outcome, String status) {
        this.lastOutcome = outcome;
        this.status = status;
        this.phase = GamePhase.ROUND_ENDED;
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
        lastOutcome = null;
        status = "New game started.";
    }

    public void markGameOver() {
        balance = 0;
        phase = GamePhase.GAME_OVER;
    }
}
