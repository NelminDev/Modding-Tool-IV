package dev.nelmin.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.nelmin.java.configuration.JSONConfiguration;
import dev.nelmin.java.scene.FilePickerScene;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloApplication extends NDApplication {

    @Override
    public void start(Stage stage) throws IOException {
        saveAndLoadConfig();

        Scene scene = new FilePickerScene().getScene();

        stage.setTitle("JSON Configuration Demo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
