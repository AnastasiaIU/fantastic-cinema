package org.example.Service;

import org.example.DAL.UserDao;
import org.example.Model.User;

public class LoginService {
    private UserDao userDao;

    public LoginService() {
        userDao = new UserDao();
    }

    /**
     * Validates user credentials.
     * @param username The username provided by the user.
     * @param password The password provided by the user.
     * @return The User object if credentials are valid, otherwise null.
     */
    public User login(String username, String password) {
        User user = userDao.findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
