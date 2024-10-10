package org.example.dal;

import org.example.AppContext;
import org.example.Database;
import org.example.model.User;

public class UserDao {
    private Database database;

    public UserDao() {
        database = AppContext.getInstance().getDatabase();
    }

    /**
     * Find a user by username.
     * @param username The username to search for.
     * @return The User object if found, otherwise null.
     */
    public User findUserByUsername(String username) {
        for (User user : database.getUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
