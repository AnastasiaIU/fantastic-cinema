package nl.inholland;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.inholland.controller.LoginController;

import java.io.*;
import java.util.Objects;

public class MainApplication extends Application {
    // Path to the serialized file
    private static final String DATABASE_FILE = "database.ser";

    private Database database;

    @Override
    public void start(Stage stage) throws IOException {
        // Deserialize the database at startup
        database = loadDatabase();

        // Load the FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/login-view.fxml"));
        fxmlLoader.setController(new LoginController(database));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/nl/inholland/view/css/login-view.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Serialize the database before the application closes
        saveDatabase(database);
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }

    // Method to serialize the database to a file
    private void saveDatabase(Database database) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATABASE_FILE))) {
            out.writeObject(database);
        } catch (IOException e) {
            System.err.println("Error saving the database: " + e.getMessage());
        }
    }

    // Method to deserialize the database from a file
    private Database loadDatabase() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATABASE_FILE))) {
            return (Database) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Return a new database if the file does not exist or deserialization fails
            return new Database();
        }
    }
}