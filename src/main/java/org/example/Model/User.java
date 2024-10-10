package org.example.Model;

public class User {
    private String username;
    private String password;
    private AccessLevel accessLevel;

    public User(String username, String password, AccessLevel accessLevel) {
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Getter for accessLevel
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
}
