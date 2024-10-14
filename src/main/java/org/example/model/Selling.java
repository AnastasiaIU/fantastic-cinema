package org.example.model;

import java.time.LocalDateTime;

public class Selling {
    private int id;
    private LocalDateTime dateTime;
    private int ticketsSold;
    private String showing;
    private String customer;

    public Selling(int id, LocalDateTime dateTime, int ticketsSold, String showing, String customer) {
        this.id = id;
        this.dateTime = dateTime;
        this.ticketsSold = ticketsSold;
        this.showing = showing;
        this.customer = customer;
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

    public void setShowing(String showing) {
        this.showing = showing;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
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

    public String getShowing() {
        return showing;
    }

    public String getCustomer() {
        return customer;
    }
}
