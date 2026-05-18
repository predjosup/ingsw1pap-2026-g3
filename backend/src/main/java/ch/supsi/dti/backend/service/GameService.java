package ch.supsi.dti.backend.service;

import ch.supsi.dti.backend.model.Card;
import ch.supsi.dti.backend.model.Deck;
import ch.supsi.dti.backend.model.GamePhase;
import ch.supsi.dti.backend.model.Hand;
import ch.supsi.dti.backend.model.RoundOutcome;

public final class GameService {

    private static final int INITIAL_BALANCE = 100;
    private static final int MIN_BET = 10;
    private static final double BLACKJACK_PAYOUT = 1.5;

    private String playerName = "";
    private int balance = INITIAL_BALANCE;
    private int currentBet;
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

    public int currentBet() {
        return currentBet;
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

    public boolean canPlaceBet() {
        return phase != GamePhase.PLAYER_TURN && !isGameOver();
    }

    public void addBet(int amount) {
        if (!canPlaceBet()) {
            status = "Bet is not available now.";
            return;
        }
        if (amount <= 0) {
            status = "Invalid amount.";
            return;
        }
        if (currentBet + amount > balance) {
            status = "Insufficient balance.";
            return;
        }
        currentBet += amount;
        status = "Table bet: " + currentBet;
    }

    public void clearBet() {
        if (!canPlaceBet()) {
            status = "Bet cannot be cleared now.";
            return;
        }
        currentBet = 0;
        status = "Bet cleared.";
    }

    public void startRound() {
        if (!canPlaceBet()) {
            status = "Round already active.";
            return;
        }
        if (currentBet < MIN_BET) {
            status = "Minimum bet: " + MIN_BET;
            return;
        }
        if (currentBet > balance) {
            status = "Insufficient balance.";
            return;
        }

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
        if (outcome == RoundOutcome.PLAYER_BLACKJACK) {
            balance += (int) Math.round(currentBet * BLACKJACK_PAYOUT);
        } else if (outcome == RoundOutcome.PLAYER_WIN) {
            balance += currentBet;
        } else if (outcome == RoundOutcome.DEALER_WIN) {
            balance -= currentBet;
        }

        if (balance < 0) {
            balance = 0;
        }

        this.lastOutcome = outcome;
        this.status = status + " Balance: " + balance;
        currentBet = 0;

        if (balance == 0) {
            this.phase = GamePhase.GAME_OVER;
            this.status = "Game over: balance exhausted.";
        } else if (balance < MIN_BET) {
            this.phase = GamePhase.GAME_OVER;
            this.status = "Game over: balance below minimum bet.";
        } else {
            this.phase = GamePhase.ROUND_ENDED;
        }
    }

    public void resetDeck(int deckCount) {
        deck = new Deck(deckCount);
    }

    public boolean isGameOver() {
        return phase == GamePhase.GAME_OVER;
    }

    public void startNewGame() {
        balance = INITIAL_BALANCE;
        currentBet = 0;
        phase = GamePhase.WAITING_BET;
        playerHand = new Hand();
        dealerHand = new Hand();
        lastOutcome = null;
        status = "New game started.";
    }

    public void markGameOver() {
        balance = 0;
        currentBet = 0;
        phase = GamePhase.GAME_OVER;
    }
}
