package dev.nelmin.java.scene.mods;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class ModBrowserScene {
    @Getter
    private final Scene scene;

    public ModBrowserScene() {
        VBox vBox = new VBox(20);

        this.scene = new Scene(vBox, 500, 200);
    }
}
