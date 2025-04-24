package dev.nelmin.java.scene.mods;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.Accessors;

/*
 List all mods compatible with current game version
 Add option to install
 If supporting Fusion Overloader natively, use this without giving an option
 If not, ask if a Fusion Overloader "patch" should be created and then used, or if it should modify the normal files.
 */
@Accessors(fluent = true)
public class ModBrowser {
    @Getter
    private final Scene scene;

    public ModBrowser() {
        VBox vBox = new VBox(20);

        this.scene = new Scene(vBox, 500, 200);
    }
}
