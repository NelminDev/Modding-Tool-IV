package dev.nelmin.java;

import dev.nelmin.java.application.NDApp;
import dev.nelmin.java.configuration.NDString;
import dev.nelmin.java.scene.FilePickerScene;
import dev.nelmin.java.scene.UnsupportedOSScene;
import dev.nelmin.java.scene.mods.Dashboard;
import javafx.scene.Scene;

import java.io.IOException;

public class MTIV extends NDApp {

    @Override
    public void start() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("windows")) {
            stage(
                    new UnsupportedOSScene().scene(),
                    "Unsupported Operating System"
            );
            return;
        }

        NDString defaultGameVersion = config().getNDString("default");
        NDString gameVersion = config().getNDString("game." + defaultGameVersion.value());
        if (defaultGameVersion.isBlankOrNull() || isGameVersionInvalid(defaultGameVersion.value()) || gameVersion.isBlankOrNull())
            openFilePicker();
        else {
            stage(
                    new Dashboard().scene(),
                    "Dashboard"
            );
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public void stage(Scene scene, String title) {
        setTitle(title);
        stage().setScene(scene);
        stage().show();
    }

    private boolean isGameVersionInvalid(String gameVersion) {
        return switch (gameVersion) {
            case "1040", "1070", "1080", "12059" -> false;
            default -> true;
        };
    }

    private void openFilePicker() {
        stage(
                new FilePickerScene(
                        (path, version) -> {
                            config().set("game.default", version);
                            config().set(String.format("game.%s.directory", version.replace(".", "\\.")), path.toString());
                            try {
                                config().save();
                            } catch (IOException e) {
                                System.err.println("Failed to save configuration: " + e.getMessage());
                            }
                        }
                ).scene(),
                "Select your GTAIV.exe"
        );
    }
}
