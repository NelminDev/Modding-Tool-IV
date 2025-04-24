package dev.nelmin.java.scene.mods;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.Accessors;

/*
 List all installed mods
 Give the option to update index
 Give the option to change instance
 Mod Manager (Uninstall/Update mod)
 Give the option to load a specific Backup
 Give the option to create a new Backup
*/
@Accessors(fluent = true)
public class Dashboard {
    @Getter
    private final Scene scene;

    public Dashboard() {
        VBox vBox = new VBox(20);

        this.scene = new Scene(vBox, 500, 200);
    }
}
