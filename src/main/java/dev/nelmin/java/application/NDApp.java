package dev.nelmin.java;

import dev.nelmin.java.configuration.JSONConfiguration;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public abstract class NDApplication extends Application {
    @Getter private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        saveAndLoadConfig();
        start();
    }

    public abstract void start() throws IOException;

    public void setTitle(String title) {
        stage.setTitle(String.format("ND Modding Tool IV - %s", title));
    }

    // RESOURCE MANAGEMENT (src/main/resources) - INSPIRED BY THE BUKKIT API
    @Getter
    private JSONConfiguration config;

    /**
     * Saves a specific configuration resource file to the current working directory
     * and subsequently loads it into the application configuration.
     * <p>
     * The method first saves the resource file named "config.json" from the classpath
     * to the current working directory. Then, it uses the path of the saved file to
     * load the configuration into the application's {@code config} field.
     *
     * @throws IOException if an I/O error occurs while saving or loading the configuration
     */
    public void saveAndLoadConfig() throws IOException {
        saveResource("config.json");
        String filePath = System.getProperty("user.dir") + File.separator + "config.json";
        this.config = JSONConfiguration.loadConfiguration(Path.of(filePath).toFile());
    }

    /**
     * Saves a resource file from the classpath to a specified destination on the file system.
     *
     * @param resourceName the name of the resource file to be saved, located in the classpath
     * @param destination  the file path where the resource should be saved
     * @throws IllegalArgumentException if the resource cannot be found in the classpath
     * @throws RuntimeException         if an error occurs during the process of saving the resource
     */
    public void saveResource(String resourceName, String destination) {
        try (var inputStream = this.getClass().getClassLoader().getResourceAsStream(resourceName);
             var outputStream = new java.io.FileOutputStream(destination)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }

            inputStream.transferTo(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save resource " + resourceName + " to " + destination, e);
        }
    }

    /**
     * Saves a resource file from the classpath to the current working directory.
     * The resource file is saved with the same name as provided in the classpath.
     *
     * @param resourceName the name of the resource file to be saved, located in the classpath
     * @throws IllegalArgumentException if the resource cannot be found in the classpath
     * @throws RuntimeException         if an error occurs during the process of saving the resource
     */
    public void saveResource(String resourceName) {
        String currentDir = System.getProperty("user.dir");
        saveResource(resourceName, currentDir + File.separator + resourceName);
    }
}
