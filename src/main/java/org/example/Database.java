package org.example;

import org.example.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Database {
    private List<User> users = new ArrayList<>();
    private List<Showing> showings = new ArrayList<>();
    private List<Selling> sellings = new ArrayList<>();

    public Database() {
        Random random = new Random();

        // Add some users
        users.add(new User("admin", "admin", AccessLevel.MANAGEMENT));
        users.add(new User("sell", "sell", AccessLevel.SALES));

        // Add reservations
        boolean[][] reservations1 = new boolean[6][12];
        boolean[][] reservations2 = new boolean[6][12];
        for (int i = 0; i < 10; i++) {
            int row = random.nextInt(0, 6);
            int col = random.nextInt(0, 12);

            if (reservations1[row][col]) {
                i--;
            } else {
                reservations1[row][col] = true;
            }
        }
        for (int i = 0; i < 52; i++) {
            int row = random.nextInt(0, 6);
            int col = random.nextInt(0, 12);

            if (reservations2[row][col]) {
                i--;
            } else {
                reservations2[row][col] = true;
            }
        }

        // Add some showings
        showings.add(new Showing(0, LocalDateTime.of(2024, 11, 15, 14, 0), 10, LocalTime.of(2, 30), "Joker: Folie à Deux", reservations1));
        showings.add(new Showing(1, LocalDateTime.of(2024, 11, 12, 18, 0), 0, LocalTime.of(2, 0), "The Wild Robot", new boolean[6][12]));
        showings.add(new Showing(2, LocalDateTime.of(2024, 10, 10, 16, 30), 52, LocalTime.of(3, 10), "Beetlejuice Beetlejuice", reservations2));
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
    public List<Showing> getAllShowings() {
        return showings;
    }

    public List<Selling> getSellings() {
        return sellings;
    }

    public void deleteShowing(Showing selectedShowing) {
        showings.remove(selectedShowing);
    }

    public void addUpdateShowing(Showing showing) {
        if (showing.getId() == -1) {
            showing.setId(showings.size());
            showings.add(showing);
        } else {
            showings.set(showing.getId(), showing);
        }
    }

    public List<Showing> getAllUpcomingShowings() {
        List<Showing> upcomingShowings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Showing showing : showings) {
            if (showing.getStartDateTime().isAfter(now)) {
                upcomingShowings.add(showing);
            }
        }

        return upcomingShowings;
    }

    public void addSelling(Selling selling) {
        sellings.add(selling);
    }

    public void sellTicket(int showingId, int[] seat) {
        Showing showing = showings.get(showingId);
        showing.sellTicket(seat);
    }
}
