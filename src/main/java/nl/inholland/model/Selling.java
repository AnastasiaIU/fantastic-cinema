package nl.inholland.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The Selling class represents a transaction where tickets for a showing are sold to a customer.
 * This class implements {@link Serializable} to allow instances to be serialized for storage or transmission.
 */
public class Selling implements Serializable {
    // This constant is used to keep track of the version
    @Serial
    private static final long serialVersionUID = 1L;

    // Unique identifier for the selling transaction
    private int id;
    // The date and time when the transaction occurred
    private LocalDateTime dateTime;
    // The number of tickets sold in this transaction
    private int ticketsSold;
    // The showing for which tickets are being sold
    private Showing showing;
    // The name of the customer who bought the tickets
    private String customer;
    // A list of seat coordinates that were reserved in this transaction
    private List<int[]> seats;

    /**
     * Constructs a new Selling instance with the specified details.
     *
     * @param id The unique identifier for the selling transaction.
     * @param dateTime The date and time when the transaction occurred.
     * @param ticketsSold The number of tickets sold.
     * @param showing The showing for which the tickets were sold.
     * @param customer The name of the customer who bought the tickets.
     * @param seats A list of seats reserved in the transaction.
     */
    public Selling(int id, LocalDateTime dateTime, int ticketsSold, Showing showing, String customer, List<int[]> seats) {
        this.id = id;
        this.dateTime = dateTime;
        this.ticketsSold = ticketsSold;
        this.showing = showing;
        this.customer = customer;
        this.seats = seats;
    }

    // Getter and setter methods for the class fields
    public void setId(int id) {
        this.id = id;
    }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setTicketsSold(int ticketsSold) { this.ticketsSold = ticketsSold; }
    public void setShowing(Showing showing) {
        this.showing = showing;
    }
    public void setCustomer(String customer) { this.customer = customer; }
    public void setSeats(List<int[]> seats) { this.seats = seats; }
    public int getId() {
        return id;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public int getTicketsSold() { return ticketsSold; }
    public Showing getShowing() {
        return showing;
    }
    public String getCustomer() { return customer; }
    public List<int[]> getSeats() { return seats; }
}
