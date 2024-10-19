package nl.inholland;

import nl.inholland.model.AccessLevel;
import nl.inholland.model.Selling;
import nl.inholland.model.Showing;
import nl.inholland.model.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Database class serves as a mock database for managing users, showings, and ticket sales in the system.
 * It provides methods for retrieving and manipulating data related to users, showings, and sales.
 * This class is designed for use in a simulation environment where it manages the state in memory.
 * Implements {@link Serializable} to allow its instances to be serialized for storage.
 */
public class Database implements Serializable {
    // List of users in the system
    private final List<User> users = new ArrayList<>();
    // List of showings available in the system
    private final List<Showing> showings = new ArrayList<>();
    // List of sells made in the system
    private final List<Selling> sells = new ArrayList<>();

    /**
     * Constructs a new Database instance, initializing predefined users, showings, and sells.
     */
    public Database() {
        // Add some users
        users.add(new User("admin", "admin", AccessLevel.MANAGEMENT));
        users.add(new User("sell", "sell", AccessLevel.SALES));

        // Create reserved seats
        int[][] reservedSeats10 = new int[][]{
                {0, 1}, {0, 4}, {1, 2}, {1, 5}, {2, 3},
                {3, 6}, {3, 8}, {4, 1}, {5, 5}, {5, 9}
        };
        int[][] reservedSeats52 = new int[][]{
                {0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}, {0, 8}, {0, 9}, {0, 10}, {0, 11},
                {1, 0}, {1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, {1, 9}, {1, 10}, {1, 11},
                {2, 0}, {2, 1}, {2, 2}, {2, 3}, {2, 4}, {2, 5}, {2, 6}, {2, 7}, {2, 8}, {2, 9}, {2, 10}, {2, 11},
                {3, 0}, {3, 1}, {3, 2}, {3, 3}, {3, 4}, {3, 5}, {3, 6}, {3, 7}, {3, 8}, {3, 9},
                {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {5, 0}
        };

        //  Create reservations
        boolean[][] seats0 = new boolean[6][12];
        boolean[][] seats10 = new boolean[6][12];
        boolean[][] seats52 = new boolean[6][12];

        // Initialize reserved seats for showings
        initializeSeats(reservedSeats10, seats10);
        initializeSeats(reservedSeats52, seats52);

        // Add some showings
        showings.add(new Showing(0, LocalDateTime.of(2024, 11, 15, 14, 0), 10, LocalTime.of(2, 30), "Joker: Folie à Deux", seats10));
        showings.add(new Showing(1, LocalDateTime.of(2024, 11, 12, 18, 0), 0, LocalTime.of(2, 0), "The Wild Robot", seats0));
        showings.add(new Showing(2, LocalDateTime.of(2024, 10, 10, 16, 30), 52, LocalTime.of(3, 10), "Beetlejuice Beetlejuice", seats52));

        // Add selling data
        addSells();
    }

    /**
     * Helper method to initialize reserved seats based on provided coordinates.
     *
     * @param reservedSeats The coordinates of reserved seats.
     * @param seats         The boolean matrix representing seat availability.
     */
    private void initializeSeats(int[][] reservedSeats, boolean[][] seats) {
        for (int[] seat : reservedSeats) {
            seats[seat[0]][seat[1]] = true;
        }
    }

    /**
     * @return List of all users.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * @return List of all showings.
     */
    public List<Showing> getShowings() {
        return showings;
    }

    /**
     * @return List of all sales.
     */
    public List<Selling> getSells() {
        return sells;
    }

    /**
     * Deletes a showing from the system.
     *
     * @param selectedShowing The showing to delete.
     */
    public void deleteShowing(Showing selectedShowing) {
        showings.remove(selectedShowing);
    }

    /**
     * Adds or updates a showing in the system.
     * If the showing is new (id is -1), it is added as a new showing.
     *
     * @param showing The showing to add or update.
     */
    public void addUpdateShowing(Showing showing) {
        if (showing.getId() == -1) {
            showing.setId(showings.size());
            showings.add(showing);
        } else {
            showings.set(showing.getId(), showing);
        }
    }

    /**
     * @return List of upcoming showings based on the current date and time.
     */
    public List<Showing> getUpcomingShowings() {
        List<Showing> upcomingShowings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Showing showing : showings) {
            if (showing.getStartDateTime().isAfter(now)) {
                upcomingShowings.add(showing);
            }
        }

        return upcomingShowings;
    }

    /**
     * Adds a selling record to the system.
     *
     * @param selling The selling record to add.
     */
    public void addSelling(Selling selling) {
        selling.setId(sells.size());
        sells.add(selling);
    }

    /**
     * Marks a seat as sold for a specific showing.
     *
     * @param showingId The ID of the showing.
     * @param seat      The coordinates of the seat to mark as sold.
     */
    public void sellTicket(int showingId, int[] seat) {
        Showing showing = showings.get(showingId);
        showing.sellTicket(seat);
    }

    /**
     * Initializes and adds predefined sales data to the system.
     */
    private void addSells() {
        sells.add(new Selling(0, LocalDateTime.of(2024, 10, 15, 14, 0), 6, showings.getFirst(), "John Doe", Arrays.asList(
                new int[]{4, 3},
                new int[]{4, 4},
                new int[]{4, 5},
                new int[]{4, 6},
                new int[]{4, 7},
                new int[]{4, 8}
        )));
        sells.add(new Selling(1, LocalDateTime.of(2024, 10, 20, 16, 0), 4, showings.getFirst(), "Jane Doe", Arrays.asList(
                new int[]{5, 3},
                new int[]{5, 4},
                new int[]{5, 5},
                new int[]{5, 6}
        )));
        sells.add(new Selling(3, LocalDateTime.of(2024, 9, 16, 16, 30), 6, showings.get(2), "Jane Smith", Arrays.asList(
                new int[]{1, 3},
                new int[]{1, 4},
                new int[]{1, 5},
                new int[]{1, 6},
                new int[]{1, 7},
                new int[]{1, 8}
        )));
        sells.add(new Selling(4, LocalDateTime.of(2024, 9, 17, 18, 0), 8, showings.get(1), "Alex Johnson", Arrays.asList(
                new int[]{2, 1},
                new int[]{2, 2},
                new int[]{2, 3},
                new int[]{2, 4},
                new int[]{2, 5},
                new int[]{2, 6},
                new int[]{2, 7},
                new int[]{2, 8}
        )));
        sells.add(new Selling(5, LocalDateTime.of(2024, 9, 18, 20, 0), 5, showings.getFirst(), "Emily Brown", Arrays.asList(
                new int[]{3, 1},
                new int[]{3, 2},
                new int[]{3, 3},
                new int[]{3, 4},
                new int[]{3, 5}
        )));
        sells.add(new Selling(6, LocalDateTime.of(2024, 9, 19, 14, 30), 7, showings.get(1), "Michael Lee", Arrays.asList(
                new int[]{4, 2},
                new int[]{4, 3},
                new int[]{4, 4},
                new int[]{4, 5},
                new int[]{4, 6},
                new int[]{4, 7},
                new int[]{4, 8}
        )));
        sells.add(new Selling(7, LocalDateTime.of(2024, 9, 20, 15, 0), 10, showings.getFirst(), "Olivia Davis", Arrays.asList(
                new int[]{5, 1},
                new int[]{5, 2},
                new int[]{5, 3},
                new int[]{5, 4},
                new int[]{5, 5},
                new int[]{5, 6},
                new int[]{5, 7},
                new int[]{5, 8},
                new int[]{5, 9},
                new int[]{5, 10}
        )));
        sells.add(new Selling(8, LocalDateTime.of(2024, 9, 21, 19, 0), 8, showings.get(2), "Sophia Martinez", Arrays.asList(
                new int[]{0, 2},
                new int[]{0, 3},
                new int[]{0, 4},
                new int[]{0, 5},
                new int[]{0, 6},
                new int[]{0, 7},
                new int[]{0, 8},
                new int[]{0, 9}
        )));
        sells.add(new Selling(9, LocalDateTime.of(2024, 9, 22, 12, 0), 8, showings.getFirst(), "Liam Wilson", Arrays.asList(
                new int[]{1, 9},
                new int[]{1, 10},
                new int[]{1, 11},
                new int[]{2, 9},
                new int[]{2, 10},
                new int[]{2, 11},
                new int[]{3, 9},
                new int[]{3, 10}
        )));
    }
}
