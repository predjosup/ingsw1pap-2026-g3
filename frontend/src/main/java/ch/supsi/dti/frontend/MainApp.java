package ch.supsi.dti.frontend;

import ch.supsi.dti.backend.service.GamePersistenceService;
import ch.supsi.dti.backend.service.GameService;
import ch.supsi.dti.backend.service.LicenseService;
import ch.supsi.dti.backend.service.LicenseValidationResult;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

public class MainApp extends Application {

    private enum ViewMode {
        LICENSE,
        START_CHOICE,
        GAME
    }

    public enum StartMode {
        RESUME,
        NEW_GAME
    }

    private static final Path SAVE_PATH = Path.of("saved", "game-state.dat");

    private final LicenseService licenseService = new LicenseService();
    private final GameService gameService = new GameService(new GamePersistenceService(SAVE_PATH));

    private Stage stage;
    private Locale currentLocale = Locale.ITALIAN;
    private ViewMode currentView = ViewMode.LICENSE;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle(i18n().text("app.title"));
        stage.setMinWidth(720);
        stage.setMinHeight(520);
        showLicenseScene();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private I18n i18n() {
        return new I18n(currentLocale);
    }

    private void switchLanguage(Locale locale) {
        currentLocale = locale;
        stage.setTitle(i18n().text("app.title"));
        switch (currentView) {
            case LICENSE -> showLicenseScene();
            case START_CHOICE -> showStartChoiceScene();
            case GAME -> showGameScene();
        }
    }

    private void showLicenseScene() {
        currentView = ViewMode.LICENSE;
        stage.setMinWidth(720);
        stage.setMinHeight(520);
        stage.setScene(createLicenseScene());
    }

    private void showStartChoiceScene() {
        currentView = ViewMode.START_CHOICE;
        stage.setMinWidth(720);
        stage.setMinHeight(520);
        stage.setScene(createStartChoiceScene());
    }

    private void showGameScene() {
        currentView = ViewMode.GAME;
        try {
            stage.setScene(createGameScene());
            stage.setMinWidth(980);
            stage.setMinHeight(620);
            stage.setMaximized(true);
        } catch (IOException e) {
            showLicenseScene();
        }
    }

    private Scene createLicenseScene() {
        I18n i18n = i18n();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.getStylesheets().add(getClass().getResource("/ui/main.css").toExternalForm());
        root.setTop(createTitleBar(i18n.text("app.licenseTitle")));

        VBox licenseCard = new VBox(12);
        licenseCard.getStyleClass().add("license-card");
        licenseCard.setMaxWidth(640);

        Label cardTitle = new Label(i18n.text("license.cardTitle"));
        cardTitle.getStyleClass().add("license-title");

        Label helpText = new Label(i18n.text("license.help"));
        helpText.setWrapText(true);
        helpText.getStyleClass().add("license-help");

        TextArea licenseInput = new TextArea();
        licenseInput.setPromptText(i18n.text("license.prompt"));
        licenseInput.setWrapText(true);
        licenseInput.setPrefRowCount(3);
        licenseInput.getStyleClass().add("license-input");

        Label sourceLabel = new Label(i18n.text("license.source.none"));
        sourceLabel.getStyleClass().add("stat-label");

        Label statusLabel = new Label(i18n.text("license.status.initial"));
        statusLabel.setWrapText(true);
        statusLabel.getStyleClass().addAll("status-label", "license-status-info");

        Button loadFileButton = new Button(i18n.text("license.button.load"));
        loadFileButton.setOnAction(event -> loadLicenseFromFile(licenseInput, sourceLabel, statusLabel));

        Button validateButton = new Button(i18n.text("license.button.validate"));
        validateButton.setOnAction(event -> validateAndStart(licenseInput, statusLabel));

        HBox actions = new HBox(8, loadFileButton, validateButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        licenseCard.getChildren().addAll(cardTitle, helpText, licenseInput, sourceLabel, actions, statusLabel);

        StackPane center = new StackPane(licenseCard);
        center.setPadding(new Insets(24));
        root.setCenter(center);

        prefillLicenseCode(licenseInput, sourceLabel, statusLabel);
        return new Scene(root, 760, 520);
    }

    private Scene createStartChoiceScene() {
        I18n i18n = i18n();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.getStylesheets().add(getClass().getResource("/ui/main.css").toExternalForm());
        root.setTop(createTitleBar(i18n.text("app.startTitle")));

        boolean savedGamePresent = Files.exists(SAVE_PATH);

        VBox choiceCard = new VBox(12);
        choiceCard.getStyleClass().add("license-card");
        choiceCard.setMaxWidth(640);

        Label cardTitle = new Label(i18n.text("start.cardTitle"));
        cardTitle.getStyleClass().add("license-title");

        Label helpText = new Label(savedGamePresent
                ? i18n.text("start.help.resume")
                : i18n.text("start.help.new"));
        helpText.setWrapText(true);
        helpText.getStyleClass().add("license-help");

        Label saveInfo = new Label(savedGamePresent
                ? i18n.text("start.save.found", SAVE_PATH.toAbsolutePath())
                : i18n.text("start.save.missing", SAVE_PATH.toAbsolutePath()));
        saveInfo.setWrapText(true);
        saveInfo.getStyleClass().add("stat-label");

        Button resumeButton = new Button(i18n.text("button.resume"));
        resumeButton.setDisable(!savedGamePresent);
        resumeButton.setOnAction(event -> openGame(StartMode.RESUME));

        Button newGameButton = new Button(i18n.text("button.newGame"));
        newGameButton.setOnAction(event -> openGame(StartMode.NEW_GAME));

        Button closeButton = new Button(i18n.text("button.close"));
        closeButton.setOnAction(event -> stage.close());

        HBox actions = new HBox(8, resumeButton, newGameButton, closeButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        choiceCard.getChildren().addAll(cardTitle, helpText, saveInfo, actions);

        StackPane center = new StackPane(choiceCard);
        center.setPadding(new Insets(24));
        root.setCenter(center);

        return new Scene(root, 820, 520);
    }

    private Scene createGameScene() throws IOException {
        I18n i18n = i18n();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"), i18n.bundle());
        loader.setControllerFactory(type -> {
            if (type == Controller.class) {
                return new Controller(gameService, i18n, this::switchLanguage);
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        return new Scene(loader.load(), 1280, 720);
    }

    private HBox createTitleBar(String titleText) {
        I18n i18n = i18n();

        HBox titleBar = new HBox(8);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.getStyleClass().add("title-bar");
        titleBar.setPadding(new Insets(8, 12, 8, 12));

        Label title = new Label(titleText);
        title.getStyleClass().add("title-text");

        Region spacer = growRegion();

        Button italianButton = new Button(i18n.text("language.it"));
        italianButton.setDisable(i18n.isItalian());
        italianButton.setOnAction(event -> switchLanguage(Locale.ITALIAN));

        Button englishButton = new Button(i18n.text("language.en"));
        englishButton.setDisable(i18n.isEnglish());
        englishButton.setOnAction(event -> switchLanguage(Locale.ENGLISH));

        titleBar.getChildren().addAll(title, spacer, italianButton, englishButton);
        return titleBar;
    }

    private Path resolveValidatorPath() {
        String custom = System.getProperty("blackjack.validator.path");
        if (custom != null && !custom.isBlank()) {
            return Path.of(custom);
        }

        String executableName = isWindows() ? "license_validator.exe" : "license_validator";
        List<Path> candidates = List.of(
                Path.of("license-validator", "bin", executableName),
                Path.of("..", "license-validator", "bin", executableName),
                Path.of("license-validator", executableName),
                Path.of("..", "license-validator", executableName)
        );

        return firstExisting(candidates).orElse(candidates.get(0));
    }

    private Path resolveLicenseFilePath() {
        String custom = System.getProperty("blackjack.license.path");
        if (custom != null && !custom.isBlank()) {
            return Path.of(custom);
        }

        List<Path> candidates = List.of(
                Path.of("license-validator", "license.key"),
                Path.of("..", "license-validator", "license.key")
        );

        return firstExisting(candidates).orElse(candidates.get(0));
    }

    private java.util.Optional<Path> firstExisting(List<Path> candidates) {
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return java.util.Optional.of(candidate);
            }
        }
        return java.util.Optional.empty();
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win");
    }

    private void prefillLicenseCode(TextArea licenseInput, Label sourceLabel, Label statusLabel) {
        I18n i18n = i18n();
        Path defaultLicense = resolveLicenseFilePath();
        if (!Files.exists(defaultLicense)) {
            return;
        }

        try {
            licenseInput.setText(licenseService.readLicenseCode(defaultLicense));
            sourceLabel.setText(i18n.text("license.source.default", defaultLicense.toAbsolutePath()));
            setStatus(statusLabel, i18n.text("license.prefilled"), "license-status-info");
        } catch (IOException e) {
            setStatus(statusLabel, i18n.text("license.status.fileReadError", defaultLicense.toAbsolutePath()), "license-status-error");
        }
    }

    private void loadLicenseFromFile(TextArea licenseInput, Label sourceLabel, Label statusLabel) {
        I18n i18n = i18n();

        FileChooser chooser = new FileChooser();
        chooser.setTitle(i18n.text("license.fileDialog.title"));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(i18n.text("license.fileDialog.filter.license"), "*.key", "*.txt"),
                new FileChooser.ExtensionFilter(i18n.text("license.fileDialog.filter.all"), "*.*")
        );

        Path defaultLicense = resolveLicenseFilePath().toAbsolutePath().getParent();
        if (defaultLicense != null && Files.exists(defaultLicense)) {
            chooser.setInitialDirectory(defaultLicense.toFile());
        }

        java.io.File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return;
        }

        try {
            String licenseCode = licenseService.readLicenseCode(selectedFile.toPath());
            licenseInput.setText(licenseCode);
            sourceLabel.setText(i18n.text("license.source.loaded", selectedFile.getAbsolutePath()));
            setStatus(statusLabel, i18n.text("license.status.fileLoaded"), "license-status-info");
        } catch (IOException e) {
            setStatus(statusLabel, i18n.text("license.status.fileReadError", selectedFile.toPath().toAbsolutePath()), "license-status-error");
        }
    }

    private void validateAndStart(TextArea licenseInput, Label statusLabel) {
        LicenseValidationResult result = licenseService.validateCode(
                resolveValidatorPath(),
                licenseInput.getText(),
                Duration.ofSeconds(5)
        );

        String message = result.detail() == null
                ? i18n().text(result.messageKey())
                : i18n().text(result.messageKey(), result.detail());

        if (!result.valid()) {
            setStatus(statusLabel, message, "license-status-error");
            return;
        }

        setStatus(statusLabel, message, "license-status-success");
        showStartChoiceScene();
    }

    private void openGame(StartMode startMode) {
        if (startMode == StartMode.NEW_GAME) {
            gameService.startNewGame();
        }
        showGameScene();
    }

    private void setStatus(Label statusLabel, String message, String stateClass) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll(
                "license-status-info",
                "license-status-error",
                "license-status-success"
        );
        statusLabel.getStyleClass().add(stateClass);
    }

    private Region growRegion() {
        Region region = new Region();
        HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);
        return region;
    }
}
