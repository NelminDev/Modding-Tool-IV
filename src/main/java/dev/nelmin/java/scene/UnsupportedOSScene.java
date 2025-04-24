package dev.nelmin.java.scene;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class UnsupportedOSScene {
    @Getter
    private final Scene scene;

    public UnsupportedOSScene() {
        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);

        Label errorLabel = new Label("Unsupported Operating System");
        errorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label detailLabel = new Label("This application currently only supports Windows");
        Label supportLabel = new Label("Supported Operating Systems:");
        Label windowsLabel = new Label("• Windows");

        vBox.getChildren().addAll(errorLabel, detailLabel, supportLabel, windowsLabel);
        this.scene = new Scene(vBox, 500, 200);
    }
}
