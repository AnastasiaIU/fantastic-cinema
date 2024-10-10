package org.example;

import org.example.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<User> users = new ArrayList<>();
    private List<Showing> showings = new ArrayList<>();

    public Database() {
        // Add some users
        users.add(new User("admin", "admin", AccessLevel.MANAGEMENT));
        users.add(new User("jane", "jane", AccessLevel.SALES));

        // Add some showings
        showings.add(new Showing(LocalDateTime.of(2024, 11, 12, 14, 0), 10, LocalTime.of(2, 30), "Joker: Folie à Deux", 72));
        showings.add(new Showing(LocalDateTime.of(2024, 11, 15, 18, 0), 0, LocalTime.of(2, 0), "The Wild Robot", 72));
    }

    /**
     * Returns the list of users.
     *
     * @return List of users.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Returns the list of showings.
     *
     * @return List of showings.
     */
    public List<Showing> getShowings() {
        return showings;
    }

    public void deleteShowing(Showing selectedShowing) {
        showings.remove(selectedShowing);
    }
}
