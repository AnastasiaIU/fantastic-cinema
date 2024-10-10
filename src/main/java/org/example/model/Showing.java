package org.example.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Showing {
    private LocalDateTime startTime;
    private int ticketsSold;
    private LocalTime duration;
    private String title;
    private int seats;

    public Showing(LocalDateTime startTime, int ticketsSold, LocalTime duration, String title, int seats) {
        this.startTime = startTime;
        this.ticketsSold = ticketsSold;
        this.duration = duration;
        this.title = title;
        this.seats = seats;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public LocalDateTime getStartTime() {
        return startTime;
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

    public int getSeats() {
        return seats;
    }

    public boolean sellTickets(int tickets) {
        if (ticketsSold + tickets <= seats) {
            ticketsSold += tickets;
            return true;
        }
        return false;
    }

    public boolean isTicketsSold() {
        return ticketsSold > 0;
    }
}
