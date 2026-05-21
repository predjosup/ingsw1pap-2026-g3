package ch.supsi.dti.frontend;

import ch.supsi.dti.backend.model.Card;
import ch.supsi.dti.backend.model.RoundOutcome;
import ch.supsi.dti.backend.model.RoundRecord;
import ch.supsi.dti.backend.service.GameService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class Controller {

    private static final DateTimeFormatter HISTORY_FORMAT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML private Label playerNameLabel;
    @FXML private Label balanceLabel;
    @FXML private Label minBetLabel;
    @FXML private Label tableBetLabel;
    @FXML private Label dealerScoreLabel;
    @FXML private Label playerScoreLabel;
    @FXML private Label statusLabel;
    @FXML private Label lastRoundLabel;
    @FXML private Label profileBalanceLabel;

    @FXML private HBox dealerCardsBox;
    @FXML private HBox playerCardsBox;

    @FXML private Button bet10Button;
    @FXML private Button bet25Button;
    @FXML private Button bet50Button;
    @FXML private Button clearBetButton;
    @FXML private Button confirmBetButton;
    @FXML private Button hitButton;
    @FXML private Button standButton;
    @FXML private Button newGameButton;
    @FXML private Button italianButton;
    @FXML private Button englishButton;

    @FXML private TextField profileNameField;
    @FXML private ListView<String> historyList;

    private final GameService gameService;
    private final I18n i18n;
    private final Consumer<Locale> languageChanger;

    public Controller(GameService gameService, I18n i18n, Consumer<Locale> languageChanger) {
        this.gameService = gameService;
        this.i18n = i18n;
        this.languageChanger = languageChanger;
    }

    @FXML
    private void initialize() {
        profileNameField.setText(gameService.playerName());
        refreshUi();
    }

    @FXML
    private void onBet10() {
        gameService.addBet(10);
        refreshUi();
    }

    @FXML
    private void onBet25() {
        gameService.addBet(25);
        refreshUi();
    }

    @FXML
    private void onBet50() {
        gameService.addBet(50);
        refreshUi();
    }

    @FXML
    private void onClearBet() {
        gameService.clearBet();
        refreshUi();
    }

    @FXML
    private void onConfirmBet() {
        gameService.startRound();
        refreshUi();
    }

    @FXML
    private void onHit() {
        gameService.hit();
        refreshUi();
    }

    @FXML
    private void onStand() {
        gameService.stand();
        refreshUi();
    }

    @FXML
    private void onNewGame() {
        gameService.startNewGame();
        profileNameField.setText(gameService.playerName());
        refreshUi();
    }

    @FXML
    private void onUpdateProfile() {
        gameService.setPlayerName(profileNameField.getText());
        refreshUi();
    }

    @FXML
    private void onItalian() {
        languageChanger.accept(Locale.ITALIAN);
    }

    @FXML
    private void onEnglish() {
        languageChanger.accept(Locale.ENGLISH);
    }

    private void refreshUi() {
        boolean hideDealerHole = gameService.canPlay();

        playerNameLabel.setText(displayPlayerName());
        balanceLabel.setText(Integer.toString(gameService.balance()));
        minBetLabel.setText(Integer.toString(gameService.minBet()));
        tableBetLabel.setText(Integer.toString(gameService.currentBet()));
        dealerScoreLabel.setText(gameService.dealerScoreText(hideDealerHole));
        playerScoreLabel.setText(Integer.toString(gameService.playerScore()));
        statusLabel.setText(formatStatus());
        lastRoundLabel.setText(i18n.text("label.lastHand", formatLatestHistory()));
        lastRoundLabel.setManaged(gameService.hasHistory());
        lastRoundLabel.setVisible(gameService.hasHistory());
        profileBalanceLabel.setText(Integer.toString(gameService.balance()));

        renderCards(dealerCardsBox, gameService.dealerCards(), hideDealerHole);
        renderCards(playerCardsBox, gameService.playerCards(), false);

        historyList.setItems(FXCollections.observableArrayList(formatHistoryLines()));

        boolean canBet = gameService.canPlaceBet();
        boolean canPlay = gameService.canPlay();

        bet10Button.setDisable(!canBet);
        bet25Button.setDisable(!canBet);
        bet50Button.setDisable(!canBet);
        clearBetButton.setDisable(!canBet || gameService.currentBet() == 0);
        confirmBetButton.setDisable(!canBet || gameService.currentBet() < gameService.minBet());
        hitButton.setDisable(!canPlay);
        standButton.setDisable(!canPlay);
        newGameButton.setDisable(canPlay);
        italianButton.setDisable(i18n.isItalian());
        englishButton.setDisable(i18n.isEnglish());
    }

    private String displayPlayerName() {
        String name = gameService.playerName();
        return (name == null || name.isBlank()) ? i18n.text("label.player") : name;
    }

    private String formatStatus() {
        String key = gameService.statusKey();
        if (key == null || key.isBlank()) {
            return "";
        }
        Integer value = gameService.statusValue();
        return value == null ? i18n.text(key) : i18n.text(key, value);
    }

    private String formatLatestHistory() {
        RoundRecord record = gameService.latestHistoryRecord();
        if (record == null) {
            return i18n.text("history.none");
        }
        return formatRecord(record);
    }

    private List<String> formatHistoryLines() {
        if (!gameService.hasHistory()) {
            return List.of(i18n.text("history.none"));
        }

        List<String> lines = new ArrayList<>();
        List<RoundRecord> records = gameService.historyRecords();
        for (int i = records.size() - 1; i >= 0; i--) {
            lines.add(formatRecord(records.get(i)));
        }
        return lines;
    }

    private String formatRecord(RoundRecord record) {
        return i18n.text(
                "history.entry",
                HISTORY_FORMAT.format(record.timestamp()),
                outcomeLabel(record.outcome()),
                record.bet(),
                record.playerScore(),
                record.dealerScore(),
                record.balanceAfter()
        );
    }

    private String outcomeLabel(RoundOutcome outcome) {
        if (outcome == null) {
            return "-";
        }
        return switch (outcome) {
            case PLAYER_BLACKJACK -> i18n.text("outcome.blackjack");
            case PLAYER_WIN -> i18n.text("outcome.win");
            case DEALER_WIN -> i18n.text("outcome.lose");
            case PUSH -> i18n.text("outcome.push");
        };
    }

    private void renderCards(HBox box, List<Card> cards, boolean hideSecondCard) {
        box.getChildren().clear();
        if (cards.isEmpty()) {
            box.getChildren().add(emptyCardNode());
            return;
        }

        for (int i = 0; i < cards.size(); i++) {
            if (hideSecondCard && i == 1) {
                box.getChildren().add(hiddenCardNode());
            } else {
                box.getChildren().add(faceUpCardNode(cards.get(i)));
            }
        }
    }

    private BorderPane faceUpCardNode(Card card) {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("playing-card-tile");
        pane.setMinWidth(76);
        pane.setMinHeight(108);
        pane.setPrefWidth(76);
        pane.setPrefHeight(108);

        Label topLabel = new Label(card.rank().shortName() + "\n" + suitSymbol(card));
        topLabel.getStyleClass().addAll("card-corner-label", cardTextStyleClass(card));
        BorderPane.setAlignment(topLabel, Pos.TOP_LEFT);
        pane.setTop(topLabel);

        Label centerLabel = new Label(suitSymbol(card));
        centerLabel.getStyleClass().addAll("card-center-label", cardTextStyleClass(card));
        pane.setCenter(centerLabel);

        Label bottomLabel = new Label(card.rank().shortName() + "\n" + suitSymbol(card));
        bottomLabel.getStyleClass().addAll("card-corner-label", cardTextStyleClass(card));
        bottomLabel.setRotate(180);
        BorderPane.setAlignment(bottomLabel, Pos.BOTTOM_RIGHT);
        pane.setBottom(bottomLabel);

        return pane;
    }

    private BorderPane hiddenCardNode() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("hidden-card-tile");
        pane.setMinWidth(76);
        pane.setMinHeight(108);
        pane.setPrefWidth(76);
        pane.setPrefHeight(108);

        Label label = new Label(i18n.text("card.hidden"));
        label.getStyleClass().add("hidden-card-label");
        pane.setCenter(label);
        return pane;
    }

    private BorderPane emptyCardNode() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("empty-card-tile");
        pane.setMinWidth(76);
        pane.setMinHeight(108);
        pane.setPrefWidth(76);
        pane.setPrefHeight(108);

        Label label = new Label("--");
        label.getStyleClass().add("empty-card-label");
        pane.setCenter(label);
        return pane;
    }

    private String suitSymbol(Card card) {
        return switch (card.suit()) {
            case HEARTS -> "\u2665";
            case DIAMONDS -> "\u2666";
            case CLUBS -> "\u2663";
            case SPADES -> "\u2660";
        };
    }

    private String cardTextStyleClass(Card card) {
        return switch (card.suit()) {
            case HEARTS, DIAMONDS -> "card-text-red";
            case CLUBS, SPADES -> "card-text-black";
        };
    }
}
