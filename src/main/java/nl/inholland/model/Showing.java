package nl.inholland.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Showing implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDateTime startDateTime;
    private int ticketsSold;
    private LocalTime duration;
    private String title;
    private boolean[][] reservedSeats;

    public Showing(int id, LocalDateTime startDateTime, int ticketsSold, LocalTime duration, String title, boolean[][] reservedSeats) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.ticketsSold = ticketsSold;
        this.duration = duration;
        this.title = title;
        this.reservedSeats = reservedSeats;
    }

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
