package nl.inholland;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.inholland.controller.LoginController;

import java.io.*;
import java.util.Objects;

/**
 * The MainApplication class is the entry point for the JavaFX application.
 * It handles loading the initial scene, managing the application lifecycle,
 * and serializing/deserializing the application state (database).
 */
public class MainApplication extends Application {
    // Path to the serialized file where the database state is stored
    private static final String DATABASE_FILE = "database.ser";

    // Instance of the Database class used for storing application data
    private Database database;

    /**
     * The main entry point for all JavaFX applications. This method is called to start the application.
     *
     * @param stage The primary stage for the JavaFX application, onto which scenes are set.
     * @throws IOException If an I/O error occurs while loading the FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Deserialize the database at startup
        database = loadDatabase();

        // Load the FXML for the login screen
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/login-view.fxml"));
        fxmlLoader.setController(new LoginController(database));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/nl/inholland/view/css/login-view.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false); // Prevent resizing of the window
    }

    /**
     * Called when the application is stopped.
     * This method serializes the database to save its state before the application closes.
     *
     * @throws Exception If any error occurs during the stop operation.
     */
    @Override
    public void stop() throws Exception {
        // Serialize the database before the application closes
        saveDatabase();
        super.stop();
    }

    /**
     * The main method, which serves as the entry point for the application when launched as a standalone Java application.
     *
     * @param args The command-line arguments passed during application launch.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Serializes the current state of the database and saves it to a file.
     * This ensures that any changes made during the application's runtime are persisted.
     *
     * @throws RuntimeException If an I/O error occurs during serialization.
     */
    private void saveDatabase() {
        try (FileOutputStream fos = new FileOutputStream(DATABASE_FILE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(database);
        } catch (IOException e) {
            throw new RuntimeException("Error saving the database: " + e.getMessage(), e);
        }
    }

    /**
     * Deserializes the database from a file to restore the application's state at startup.
     * If the file does not exist or deserialization fails, a new Database instance is returned.
     *
     * @return The deserialized Database instance or a new instance if an error occurs.
     */
    private Database loadDatabase() {
        try (FileInputStream fis = new FileInputStream(DATABASE_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Database) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Return a new database if the file does not exist or deserialization fails
            return new Database();
        }
    }
}