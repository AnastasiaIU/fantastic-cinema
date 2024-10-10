package org.example;

import org.example.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<User> users = new ArrayList<>();
    private List<Showing> showings = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();

    public Database() {
        // Add some users
        users.add(new User("admin", "admin", AccessLevel.MANAGEMENT));
        users.add(new User("jane", "jane", AccessLevel.SALES));

        // Add a room
        rooms.add(new Room(72));

        // Add some movies
        movies.add(new Movie("Joker: Folie à Deux", LocalTime.of(2, 30)));
        movies.add(new Movie("The Wild Robot", LocalTime.of(2, 0)));

        // Add some showings
        showings.add(new Showing(LocalDateTime.of(2024, 11, 12, 14, 0), movies.get(0), rooms.getFirst(), 10));
        showings.add(new Showing(LocalDateTime.of(2024, 11, 15, 18, 0), movies.get(1), rooms.getFirst(), 0));
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

    public List<Movie> getMovies() {
        return movies;
    }
}
