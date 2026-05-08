package ch.supsi.dti.frontend;

import ch.supsi.dti.backend.service.LicenseService;
import ch.supsi.dti.backend.service.LicenseValidationResult;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class MainApp extends Application {

    private final LicenseService licenseService = new LicenseService();
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Blackjack - License");
        stage.setMinWidth(720);
        stage.setMinHeight(520);
        stage.setScene(createLicenseScene());
        stage.show();
    }

    private Scene createLicenseScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.getStylesheets().add(getClass().getResource("/ui/main.css").toExternalForm());

        Label title = new Label("Blackjack - License");
        title.getStyleClass().add("title-text");
        HBox titleBar = new HBox(title);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(8, 12, 8, 12));
        titleBar.getStyleClass().add("title-bar");
        root.setTop(titleBar);

        VBox licenseCard = new VBox(12);
        licenseCard.getStyleClass().add("license-card");
        licenseCard.setMaxWidth(640);

        Label cardTitle = new Label("Enter or load the license");
        cardTitle.getStyleClass().add("license-title");

        Label helpText = new Label("You can type the code manually or load a local file.");
        helpText.setWrapText(true);
        helpText.getStyleClass().add("license-help");

        TextArea licenseInput = new TextArea();
        licenseInput.setPromptText("BLACKJACK-2026-VALID");
        licenseInput.setWrapText(true);
        licenseInput.setPrefRowCount(3);

        Label sourceLabel = new Label("No file loaded");
        sourceLabel.getStyleClass().add("stat-label");

        Label statusLabel = new Label("Enter a valid license to continue.");
        statusLabel.setWrapText(true);
        statusLabel.getStyleClass().addAll("status-label", "license-status-info");

        Button loadFileButton = new Button("Load file");
        loadFileButton.setOnAction(event -> loadLicenseFromFile(licenseInput, sourceLabel, statusLabel));

        Button validateButton = new Button("Validate");
        validateButton.setOnAction(event -> validateLicense(licenseInput, statusLabel));

        HBox actions = new HBox(8, loadFileButton, validateButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        licenseCard.getChildren().addAll(cardTitle, helpText, licenseInput, sourceLabel, actions, statusLabel);
        StackPane center = new StackPane(licenseCard);
        center.setPadding(new Insets(24));
        root.setCenter(center);

        prefillLicenseCode(licenseInput, sourceLabel, statusLabel);
        return new Scene(root, 760, 520);
    }

    private void prefillLicenseCode(TextArea licenseInput, Label sourceLabel, Label statusLabel) {
        Path defaultLicense = resolveLicenseFilePath();
        if (!Files.exists(defaultLicense)) {
            return;
        }

        try {
            licenseInput.setText(licenseService.readLicenseCode(defaultLicense));
            sourceLabel.setText("Default file: " + defaultLicense.toAbsolutePath());
            setStatus(statusLabel, "Default license loaded.", "license-status-info");
        } catch (IOException e) {
            setStatus(statusLabel, "Unable to read the license file.", "license-status-error");
        }
    }

    private void loadLicenseFromFile(TextArea licenseInput, Label sourceLabel, Label statusLabel) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select license file");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("License files", "*.key", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        java.io.File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile == null) {
            return;
        }

        try {
            licenseInput.setText(licenseService.readLicenseCode(selectedFile.toPath()));
            sourceLabel.setText("Loaded file: " + selectedFile.getAbsolutePath());
            setStatus(statusLabel, "License code loaded from file.", "license-status-info");
        } catch (IOException e) {
            setStatus(statusLabel, "Unable to read the license file.", "license-status-error");
        }
    }

    private void validateLicense(TextArea licenseInput, Label statusLabel) {
        LicenseValidationResult result = licenseService.validateCode(
                resolveValidatorPath(),
                licenseInput.getText(),
                Duration.ofSeconds(5)
        );
        setStatus(statusLabel, licenseMessage(result), result.valid() ? "license-status-success" : "license-status-error");
    }

    private String licenseMessage(LicenseValidationResult result) {
        String detail = result.detail() == null ? "" : ": " + result.detail();
        return switch (result.messageKey()) {
            case "license.status.valid" -> "Valid license";
            case "license.status.invalid" -> "Invalid license";
            case "license.status.validatorMissing" -> "License validator not found" + detail;
            case "license.status.codeRequired" -> "Enter or load a license code";
            case "license.status.timeout" -> "License validation timeout";
            default -> "License validation error";
        };
    }

    private Path resolveValidatorPath() {
        String custom = System.getProperty("blackjack.validator.path");
        if (custom != null && !custom.isBlank()) {
            return Path.of(custom);
        }
        String executableName = isWindows() ? "license_validator.exe" : "license_validator";
        List<Path> candidates = List.of(
                Path.of("license-validator", "bin", executableName),
                Path.of("..", "license-validator", "bin", executableName)
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
        return System.getProperty("os.name", "").toLowerCase().contains("win");
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

    public static void main(String[] args) {
        launch(args);
    }
}

