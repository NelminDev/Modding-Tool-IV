package dev.nelmin.java;

import dev.nelmin.java.application.NDApp;
import dev.nelmin.java.scene.FilePickerScene;
import dev.nelmin.java.scene.UnsupportedOSScene;
import javafx.scene.Scene;

import java.io.IOException;

public class MTIV extends NDApp {

    @Override
    public void start() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("windows")) {
            stage(
                    new UnsupportedOSScene().getScene(),
                    "Unsupported Operating System"
            );
            return;
        }


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

    public static void main(String[] args) {
        launch();
    }

    public void stage(Scene scene, String title) {
        setTitle(title);
        stage().setScene(scene);
        stage().show();
    }
}
