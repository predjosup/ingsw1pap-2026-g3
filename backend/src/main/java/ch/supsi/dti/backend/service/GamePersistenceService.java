package ch.supsi.dti.backend.service;

import ch.supsi.dti.backend.model.SavedGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class GamePersistenceService {

    private final Path saveFile;

    public GamePersistenceService(Path saveFile) {
        this.saveFile = saveFile;
    }

    public Optional<SavedGame> load() {
        if (!Files.exists(saveFile)) {
            return Optional.empty();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(saveFile))) {
            Object loaded = in.readObject();
            if (loaded instanceof SavedGame savedGame) {
                return Optional.of(savedGame);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void save(SavedGame game) throws IOException {
        Files.createDirectories(saveFile.toAbsolutePath().getParent());
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(saveFile))) {
            out.writeObject(game);
        }
    }

    public Path saveFile() {
        return saveFile;
    }
}
