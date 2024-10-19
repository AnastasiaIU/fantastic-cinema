package nl.inholland.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Selling implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDateTime dateTime;
    private int ticketsSold;
    private Showing showing;
    private String customer;
    private List<int[]> seats;

    public Selling(int id, LocalDateTime dateTime, int ticketsSold, Showing showing, String customer, List<int[]> seats) {
        this.id = id;
        this.dateTime = dateTime;
        this.ticketsSold = ticketsSold;
        this.showing = showing;
        this.customer = customer;
        this.seats = seats;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public void setShowing(Showing showing) {
        this.showing = showing;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setSeats(List<int[]> seats) {
        this.seats = seats;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public Showing getShowing() {
        return showing;
    }

    public String getCustomer() {
        return customer;
    }

    public List<int[]> getSeats() {
        return seats;
    }
}
