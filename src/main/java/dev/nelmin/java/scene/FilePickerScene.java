package dev.nelmin.java.scene;

import com.sun.jna.LastErrorException;
import com.sun.jna.platform.win32.VerRsrc;
import com.sun.jna.platform.win32.VersionUtil;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

@Accessors(fluent = true)
public class FilePickerScene {
    @Getter
    private final Scene scene;
    private final Label statusLabel;
    private final TextField pathField;
    private final Button continueButton;
    private final String TARGET_FILE = "GTAIV.exe";
    private final AtomicBoolean isSearching = new AtomicBoolean(false);
    private final List<String> EXCLUDE_FOLDER_PATTERNS = Arrays.asList(
            "backup", "old", "copy", "archive", "saved", "temp", "tmp", "-bak", ".bak", "_bak"
    );
    @Getter
    private Path selectedPath;
    private String gameVersion = "Unknown";

    public FilePickerScene(BiConsumer<Path, String> onContinue) {
        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);

        statusLabel = new Label("Please select GTAIV.exe file");
        pathField = new TextField();
        pathField.setMinWidth(300);
        pathField.setEditable(true);
        pathField.setPromptText("Path to GTAIV.exe");
        pathField.textProperty().addListener((observable, oldValue, newValue) -> validatePath(newValue));

        HBox filePickerBox = new HBox(10);
        filePickerBox.setAlignment(Pos.CENTER);
        Button selectFileButton = new Button("Select GTAIV.exe");
        Button autoFindButton = new Button("Auto Find GTAIV.exe");
        filePickerBox.getChildren().addAll(selectFileButton, autoFindButton);

        selectFileButton.setOnAction(e -> showFilePicker());
        autoFindButton.setOnAction(e -> autoFindFile());

        continueButton = new Button("Continue");
        continueButton.setVisible(false);
        continueButton.setOnAction(e -> {
            if (selectedPath != null && onContinue != null) {
                onContinue.accept(selectedPath, gameVersion);
            }
        });

        vBox.getChildren().addAll(statusLabel, pathField, filePickerBox, continueButton);
        this.scene = new Scene(vBox, 500, 200);
        autoFindFile();
    }

    private void validatePath(String newValue) {
        if (newValue != null && !newValue.isEmpty()) {
            Path path = Paths.get(newValue);
            if (Files.exists(path) && path.getFileName().toString().equals(TARGET_FILE)) {
                selectedPath = path;
                gameVersion = getGameVersion(path);
                statusLabel.setText("Valid path");
                continueButton.setVisible(true);
            } else {
                statusLabel.setText("Invalid path");
                continueButton.setVisible(false);
            }
        } else {
            continueButton.setVisible(false);
        }
    }

    private void showFilePicker() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select GTAIV.exe");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GTAIV Executable", "GTAIV.exe"));

        if (selectedPath != null && Files.exists(selectedPath.getParent())) {
            fileChooser.setInitialDirectory(selectedPath.getParent().toFile());
        }

        File selectedFile = fileChooser.showOpenDialog(scene.getWindow());
        if (selectedFile != null && selectedFile.getName().equals(TARGET_FILE)) {
            if (isNoBackupLocation(selectedFile.toPath())) {
                selectedPath = selectedFile.toPath();
                gameVersion = getGameVersion(selectedPath);
                pathField.setText(selectedPath.toString());
                statusLabel.setText("Valid path selected");
                continueButton.setVisible(true);
            } else {
                statusLabel.setText("Selected path appears to be a backup location");
                continueButton.setVisible(false);
            }
        }
    }

    private void autoFindFile() {
        if (isSearching.get()) {
            statusLabel.setText("Search already in progress...");
            return;
        }
        isSearching.set(true);
        statusLabel.setText("Searching for GTAIV.exe...");
        continueButton.setVisible(false);

        CompletableFuture.runAsync(() -> {
            try {
                Path[] commonPaths = {
                        Paths.get("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Grand Theft Auto IV"),
                        Paths.get("C:\\Program Files (x86)\\Rockstar Games\\Grand Theft Auto IV"),
                        Paths.get("C:\\Program Files\\Steam\\steamapps\\common\\Grand Theft Auto IV"),
                        Paths.get("C:\\Program Files\\Rockstar Games\\Grand Theft Auto IV")
                };
                for (Path path : commonPaths) {
                    Path exePath = path.resolve(TARGET_FILE);
                    if (Files.exists(exePath) && isNoBackupLocation(exePath)) {
                        updateFoundPath(exePath);
                        return;
                    }
                }
                if (!tryElevatedSearch()) {
                    Platform.runLater(() -> statusLabel.setText("Failed to find GTAIV.exe. Please select manually."));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error during search: " + e.getMessage()));
            } finally {
                isSearching.set(false);
            }
        });
    }

    private boolean tryElevatedSearch() {
        try {
            for (File root : File.listRoots()) {
                try {
                    Files.walk(root.toPath())
                            .filter(p -> p.getFileName().toString().equals(TARGET_FILE))
                            .filter(this::isNoBackupLocation)
                            .findFirst()
                            .ifPresent(p -> updateFoundPath(p));
                } catch (AccessDeniedException e) {
                    ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",
                            "powershell.exe", "-Command",
                            "Start-Process -Verb RunAs -WindowStyle Hidden cmd.exe '/c dir /s /b \"" +
                                    root.getPath() + TARGET_FILE + "\"'");
                    Process process = pb.start();
                    String result = new String(process.getInputStream().readAllBytes());
                    if (result.contains(TARGET_FILE)) {
                        Path foundPath = Paths.get(result.trim());
                        if (isNoBackupLocation(foundPath)) {
                            updateFoundPath(foundPath);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Platform.runLater(() -> statusLabel.setText("Error during elevated search: " + e.getMessage()));
        }
        return false;
    }

    private boolean isNoBackupLocation(Path path) {
        String pathStr = path.toString().toLowerCase();
        return EXCLUDE_FOLDER_PATTERNS.stream().noneMatch(pathStr::contains);
    }

    private void updateFoundPath(Path path) {
        selectedPath = path;
        gameVersion = getGameVersion(path);
        Platform.runLater(() -> {
            pathField.setText(path.toString());
            statusLabel.setText(String.format("""
                    Found valid GTAIV.exe
                    Game Version: %s
                    """.trim(), gameVersion));
            continueButton.setVisible(true);
        });
    }

    private String getGameVersion(Path path) {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            return "N/A (Non-Windows OS)";
        }

        try {
            VerRsrc.VS_FIXEDFILEINFO versionInfo = VersionUtil.getFileVersionInfo(path.toAbsolutePath().toString());

            // Extract all 4 version components
            int major = versionInfo.getFileVersionMajor();
            int minor = versionInfo.getFileVersionMinor();
            int revision = versionInfo.getFileVersionRevision();
            int build = versionInfo.getFileVersionBuild();

            return String.format("%d%d%d%d", major, minor, revision, build);
        } catch (LastErrorException e) {
            return "Unknown (Error: " + e.getMessage() + ")";
        }
    }
}