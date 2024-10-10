package org.example;

public class AppContext {
    private static AppContext instance;
    private Database database;

    private AppContext() {
        database = new Database(); // Initialize the database once
    }

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public Database getDatabase() {
        return database;
    }
}
