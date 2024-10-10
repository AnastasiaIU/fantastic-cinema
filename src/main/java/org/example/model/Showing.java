package org.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Showing {
    private LocalDateTime startTime;
    private Room room;
    private int ticketsSold;
    private Movie movie;

    // fields for the TableView
    private String start;
    private String end;
    private String title;
    private String seats;

    // constructor for the TableView
    public Showing(String start, String end, String title, String seats) {
        this.start = start;
        this.end = end;
        this.title = title;
        this.seats = seats;
    }

    public Showing(LocalDateTime startTime, Movie movie, Room room, int ticketsSold) {
        this.startTime = startTime;
        this.movie = movie;
        this.room = room;
        this.ticketsSold = ticketsSold;
    }

    public void setStart(String start) {
        this.start = start;
    }
    public void setEnd(String end) {
        this.end = end;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getStart() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return startTime.format(formatter);
    }

    public String getEnd() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return (startTime.plusSeconds(movie.getDuration().toSecondOfDay())).format(formatter);
    }

    public String getTitle() { return movie.getTitle(); }
    public String getSeats() {
        return ticketsSold + "/" + room.getSeats();
    }

    public boolean sellTickets(int tickets) {
        if (ticketsSold + tickets <= room.getSeats()) {
            ticketsSold += tickets;
            return true;
        }
        return false;
    }

    public boolean isTicketsSold() {
        return ticketsSold > 0;
    }
}
