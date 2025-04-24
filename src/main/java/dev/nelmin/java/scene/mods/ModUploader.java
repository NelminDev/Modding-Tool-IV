package dev.nelmin.java.scene.mods;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.experimental.Accessors;

/*
 NOT YET ACCOUNTED FOR AS THE INDEX IS NOT BEHIND A REST API

 Give the option to upload mods (must follow guidelines)
 */
@Accessors(fluent = true)
public class ModUploader {
    @Getter
    private final Scene scene;

    public ModUploader() {
        VBox vBox = new VBox(20);

        this.scene = new Scene(vBox, 500, 200);
    }
}
