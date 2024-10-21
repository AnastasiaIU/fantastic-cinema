package nl.inholland.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * The Showing class represents a movie showing in the application.
 * This class stores information about the movie showing, including the start time,
 * duration, title, and reserved seats. It implements {@link Serializable} to allow
 * instances to be serialized for storage or transmission.
 */
public class Showing implements Serializable {
    // This constant is used to keep track of the version
    @Serial
    private static final long serialVersionUID = 1L;

    // Unique identifier for the showing
    private int id;
    // The date and time when the showing start
    private LocalDateTime startDateTime;
    // The number of tickets sold for this showing
    private int ticketsSold;
    // The duration of the movie
    private LocalTime duration;
    // The title of the movie being shown
    private String title;
    // A 2D boolean array representing reserved seats in the theater
    private final boolean[][] reservedSeats;
    // A boolean flag to indicate if the movie showing is age restricted
    private boolean isAgeChecked;

    /**
     * Constructs a new Showing instance with the specified details.
     *
     * @param id The unique identifier for the showing.
     * @param startDateTime The start date and time of the showing.
     * @param ticketsSold The number of tickets sold.
     * @param duration The duration of the showing.
     * @param title The title of the movie being shown.
     * @param reservedSeats A 2D boolean array representing the reserved seats.
     */
    public Showing(int id, LocalDateTime startDateTime, int ticketsSold, LocalTime duration, String title, boolean[][] reservedSeats, boolean isAgeChecked) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.ticketsSold = ticketsSold;
        this.duration = duration;
        this.title = title;
        this.reservedSeats = reservedSeats;
        this.isAgeChecked = isAgeChecked;
    }

    // Getter and setter methods for the class fields
    public  void setId(int id) {
        this.id = id;
    }
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setIsAgeChecked(boolean isAgeChecked) { this.isAgeChecked = isAgeChecked; }
    public int getId() { return id; }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public int getTicketsSold() {
        return ticketsSold;
    }
    public LocalTime getDuration() {
        return duration;
    }
    public String getTitle() {
        return title;
    }
    public boolean[][] getReservedSeats() {
        return reservedSeats;
    }
    public boolean getIsAgeChecked() { return isAgeChecked; }

    public void sellTicket(int[] seat) {
        ticketsSold++;
        reservedSeats[seat[0]][seat[1]] = true;
    }
    public boolean isTicketsSold() {
        return ticketsSold > 0;
    }
    public int getNumberOfSeats() {
        return reservedSeats.length * reservedSeats[0].length;
    }
}
