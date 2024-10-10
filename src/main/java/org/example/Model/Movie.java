package org.example.Model;

import java.time.LocalTime;

public class Movie {
    private String title;
    private LocalTime duration;

    public Movie(String title, LocalTime duration) {
        this.title = title;
        this.duration = duration;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
