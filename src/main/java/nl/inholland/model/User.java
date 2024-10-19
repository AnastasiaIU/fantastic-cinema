package nl.inholland.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * The User record represents a user in the system.
 * It stores the user's credentials and access level, and it implements {@link Serializable}
 * to allow instances to be serialized for storage or transmission.
 * This record is an immutable data structure, meaning its fields cannot be modified after initialization.
 */
public record User(String username, String password, AccessLevel accessLevel) implements Serializable {
    // This constant is used to keep track of the version
    @Serial
    private static final long serialVersionUID = 1L;

    // Getter methods for the class fields
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public AccessLevel getAccessLevel() { return accessLevel; }
}
