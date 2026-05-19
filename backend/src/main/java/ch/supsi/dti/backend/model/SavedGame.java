package ch.supsi.dti.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class SavedGame implements Serializable {

    private static final long serialVersionUID = 1L;

    private String playerName;
    private int balance;
    private int minBet;
    private double blackjackPayout;
    private int deckCount;
    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;
    private int currentBet;
    private GamePhase phase;
    private RoundOutcome lastOutcome;
    private String lastResultMessage;
    private String lastStatusKey;
    private Integer lastStatusValue;
    private List<RoundRecord> history;

    public SavedGame() {
        this.playerName = "";
        this.balance = 100;
        this.minBet = 10;
        this.blackjackPayout = 1.5;
        this.deckCount = 1;
        this.deck = new Deck(deckCount);
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.currentBet = 0;
        this.phase = GamePhase.WAITING_BET;
        this.lastOutcome = null;
        this.lastResultMessage = null;
        this.lastStatusKey = "status.promptBet";
        this.lastStatusValue = null;
        this.history = new ArrayList<>();
    }

    public String playerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int balance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int minBet() {
        return minBet;
    }

    public void setMinBet(int minBet) {
        this.minBet = minBet;
    }

    public double blackjackPayout() {
        return blackjackPayout;
    }

    public void setBlackjackPayout(double blackjackPayout) {
        this.blackjackPayout = blackjackPayout;
    }

    public int deckCount() {
        return deckCount;
    }

    public void setDeckCount(int deckCount) {
        this.deckCount = deckCount;
    }

    public Deck deck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Hand playerHand() {
        return playerHand;
    }

    public void setPlayerHand(Hand playerHand) {
        this.playerHand = playerHand;
    }

    public Hand dealerHand() {
        return dealerHand;
    }

    public void setDealerHand(Hand dealerHand) {
        this.dealerHand = dealerHand;
    }

    public int currentBet() {
        return currentBet;
    }

    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

    public GamePhase phase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public RoundOutcome lastOutcome() {
        return lastOutcome;
    }

    public void setLastOutcome(RoundOutcome lastOutcome) {
        this.lastOutcome = lastOutcome;
    }

    public String lastResultMessage() {
        return lastResultMessage;
    }

    public void setLastResultMessage(String lastResultMessage) {
        this.lastResultMessage = lastResultMessage;
    }

    public String lastStatusKey() {
        return lastStatusKey;
    }

    public void setLastStatusKey(String lastStatusKey) {
        this.lastStatusKey = lastStatusKey;
    }

    public Integer lastStatusValue() {
        return lastStatusValue;
    }

    public void setLastStatusValue(Integer lastStatusValue) {
        this.lastStatusValue = lastStatusValue;
    }

    public List<RoundRecord> history() {
        return history;
    }

    public void setHistory(List<RoundRecord> history) {
        this.history = history;
    }
}
