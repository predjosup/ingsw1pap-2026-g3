package ch.supsi.dti.backend.service;

import ch.supsi.dti.backend.model.Card;
import ch.supsi.dti.backend.model.Deck;
import ch.supsi.dti.backend.model.GamePhase;
import ch.supsi.dti.backend.model.Hand;
import ch.supsi.dti.backend.model.RoundOutcome;
import ch.supsi.dti.backend.model.RoundRecord;
import ch.supsi.dti.backend.model.SavedGame;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameService {

    private final GamePersistenceService persistenceService;
    private SavedGame state;

    public GameService(GamePersistenceService persistenceService) {
        this.persistenceService = persistenceService;
        this.state = persistenceService.load().orElseGet(SavedGame::new);
        normalizeState();
    }

    public String playerName() {
        return state.playerName();
    }

    public int balance() {
        return state.balance();
    }

    public int minBet() {
        return state.minBet();
    }

    public int currentBet() {
        return state.currentBet();
    }

    public GamePhase phase() {
        return state.phase();
    }

    public String statusKey() {
        return state.lastStatusKey();
    }

    public Integer statusValue() {
        return state.lastStatusValue();
    }

    public boolean isGameOver() {
        return state.phase() == GamePhase.GAME_OVER;
    }

    public boolean canPlaceBet() {
        return state.phase() != GamePhase.PLAYER_TURN && !isGameOver();
    }

    public boolean canPlay() {
        return state.phase() == GamePhase.PLAYER_TURN;
    }

    public String playerCardsText() {
        return state.playerHand().cardsText();
    }

    public int playerScore() {
        return state.playerHand().score();
    }

    public String dealerCardsText(boolean hideHoleCard) {
        List<Card> cards = state.dealerHand().cards();
        if (cards.isEmpty()) {
            return "-";
        }
        if (!hideHoleCard || cards.size() < 2 || state.phase() != GamePhase.PLAYER_TURN) {
            return state.dealerHand().cardsText();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            if (i == 1) {
                sb.append("??");
            } else {
                sb.append(cards.get(i));
            }
        }
        return sb.toString();
    }

    public String dealerScoreText(boolean hideHoleCard) {
        if (hideHoleCard && state.phase() == GamePhase.PLAYER_TURN && state.dealerHand().cards().size() >= 2) {
            return "?";
        }
        return Integer.toString(state.dealerHand().score());
    }

    public boolean hasHistory() {
        return state.history() != null && !state.history().isEmpty();
    }

    public List<RoundRecord> historyRecords() {
        if (!hasHistory()) {
            return List.of();
        }
        return Collections.unmodifiableList(state.history());
    }

    public RoundRecord latestHistoryRecord() {
        if (!hasHistory()) {
            return null;
        }
        return state.history().get(state.history().size() - 1);
    }

    public void setPlayerName(String newName) {
        if (newName == null || newName.isBlank()) {
            setStatus("status.invalidName");
            return;
        }
        state.setPlayerName(newName.trim());
        setStatus("status.profileUpdated");
        saveQuietly();
    }

    public void addBet(int amount) {
        if (!canPlaceBet()) {
            setStatus("status.betUnavailable");
            return;
        }
        if (amount <= 0) {
            setStatus("status.invalidAmount");
            return;
        }
        if (state.currentBet() + amount > state.balance()) {
            setStatus("status.insufficientBalance");
            return;
        }

        state.setCurrentBet(state.currentBet() + amount);
        setStatus("status.tableBet", state.currentBet());
        saveQuietly();
    }

    public void clearBet() {
        if (!canPlaceBet()) {
            setStatus("status.cannotClearNow");
            return;
        }
        state.setCurrentBet(0);
        setStatus("status.betCleared");
        saveQuietly();
    }

    public void startRound() {
        if (!canPlaceBet()) {
            setStatus("status.roundNotEnded");
            return;
        }
        if (state.currentBet() < state.minBet()) {
            setStatus("status.minBet", state.minBet());
            return;
        }
        if (state.currentBet() > state.balance()) {
            setStatus("status.insufficientBalance");
            return;
        }

        state.setPlayerHand(new Hand());
        state.setDealerHand(new Hand());
        state.setLastOutcome(null);

        dealInitialCards();
        state.setPhase(GamePhase.PLAYER_TURN);

        if (state.playerHand().isBlackjack() && state.dealerHand().isBlackjack()) {
            settleRound(RoundOutcome.PUSH, "status.bothBlackjack");
            return;
        }
        if (state.playerHand().isBlackjack()) {
            settleRound(RoundOutcome.PLAYER_BLACKJACK, "status.playerBlackjack");
            return;
        }
        if (state.dealerHand().isBlackjack()) {
            settleRound(RoundOutcome.DEALER_WIN, "status.dealerBlackjack");
            return;
        }

        setStatus("status.playerTurn");
        saveQuietly();
    }

    public void hit() {
        if (!canPlay()) {
            setStatus("status.actionUnavailable");
            return;
        }

        state.playerHand().addCard(state.deck().draw());
        if (state.playerHand().isBust()) {
            settleRound(RoundOutcome.DEALER_WIN, "status.playerBust");
            return;
        }

        setStatus("status.cardDrawn");
        saveQuietly();
    }

    public void stand() {
        if (!canPlay()) {
            setStatus("status.actionUnavailable");
            return;
        }

        while (state.dealerHand().score() < 17) {
            state.dealerHand().addCard(state.deck().draw());
        }

        if (state.dealerHand().isBust()) {
            settleRound(RoundOutcome.PLAYER_WIN, "status.dealerBust");
            return;
        }

        int player = state.playerHand().score();
        int dealer = state.dealerHand().score();

        if (player > dealer) {
            settleRound(RoundOutcome.PLAYER_WIN, "status.playerWin");
        } else if (player < dealer) {
            settleRound(RoundOutcome.DEALER_WIN, "status.dealerWin");
        } else {
            settleRound(RoundOutcome.PUSH, "status.push");
        }
    }

    public void startNewGame() {
        String playerName = state.playerName();
        state = new SavedGame();
        if (playerName != null && !playerName.isBlank()) {
            state.setPlayerName(playerName);
        }
        setStatus("status.newGameStarted");
        saveQuietly();
    }

    private void normalizeState() {
        if (state.playerName() == null) {
            state.setPlayerName("");
        }
        if ("Giocatore".equals(state.playerName()) || "Player".equals(state.playerName())) {
            state.setPlayerName("");
        }
        if (state.minBet() <= 0) {
            state.setMinBet(10);
        }
        if (state.blackjackPayout() <= 0) {
            state.setBlackjackPayout(1.5);
        }
        if (state.deckCount() < 1) {
            state.setDeckCount(1);
        }
        if (state.deck() == null) {
            state.setDeck(new Deck(state.deckCount()));
        }
        if (state.playerHand() == null) {
            state.setPlayerHand(new Hand());
        }
        if (state.dealerHand() == null) {
            state.setDealerHand(new Hand());
        }
        if (state.history() == null) {
            state.setHistory(new ArrayList<>());
        }
        if (state.phase() == null) {
            state.setPhase(GamePhase.WAITING_BET);
        }
        if (state.currentBet() < 0) {
            state.setCurrentBet(0);
        }
        if (state.lastStatusKey() == null || state.lastStatusKey().isBlank()) {
            setStatus("status.promptBet");
        }
        if (state.balance() <= 0) {
            state.setBalance(0);
            state.setPhase(GamePhase.GAME_OVER);
            setStatus("status.gameOverEmpty");
        } else if (state.balance() < state.minBet()) {
            state.setPhase(GamePhase.GAME_OVER);
            setStatus("status.gameOverMinBet");
        }
    }

    private void dealInitialCards() {
        state.playerHand().addCard(state.deck().draw());
        state.dealerHand().addCard(state.deck().draw());
        state.playerHand().addCard(state.deck().draw());
        state.dealerHand().addCard(state.deck().draw());
    }

    private void settleRound(RoundOutcome outcome, String messageKey) {
        int bet = state.currentBet();
        int newBalance = state.balance();

        if (outcome == RoundOutcome.PLAYER_BLACKJACK) {
            int winAmount = (int) Math.round(bet * state.blackjackPayout());
            newBalance += winAmount;
        } else if (outcome == RoundOutcome.PLAYER_WIN) {
            newBalance += bet;
        } else if (outcome == RoundOutcome.DEALER_WIN) {
            newBalance -= bet;
        }

        if (newBalance < 0) {
            newBalance = 0;
        }

        state.setBalance(newBalance);
        state.setLastOutcome(outcome);
        state.history().add(new RoundRecord(
                LocalDateTime.now(),
                bet,
                outcome,
                state.playerHand().score(),
                state.dealerHand().score(),
                newBalance
        ));

        state.setCurrentBet(0);
        if (newBalance == 0) {
            state.setPhase(GamePhase.GAME_OVER);
            setStatus("status.gameOverEmpty");
        } else if (newBalance < state.minBet()) {
            state.setPhase(GamePhase.GAME_OVER);
            setStatus("status.gameOverMinBet");
        } else {
            state.setPhase(GamePhase.ROUND_ENDED);
            setStatus(messageKey, newBalance);
        }

        saveQuietly();
    }

    private void saveQuietly() {
        try {
            persistenceService.save(state);
        } catch (IOException ignored) {
        }
    }

    public List<Card> playerCards() {
        return Collections.unmodifiableList(state.playerHand().cards());
    }

    public List<Card> dealerCards() {
        return Collections.unmodifiableList(state.dealerHand().cards());
    }

    private void setStatus(String key) {
        state.setLastResultMessage(null);
        state.setLastStatusKey(key);
        state.setLastStatusValue(null);
    }

    private void setStatus(String key, int value) {
        state.setLastResultMessage(null);
        state.setLastStatusKey(key);
        state.setLastStatusValue(value);
    }
}
