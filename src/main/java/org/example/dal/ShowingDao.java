package org.example.dal;

import org.example.AppContext;
import org.example.Database;
import org.example.model.Movie;
import org.example.model.Showing;

import java.util.List;

public class ShowingDao {
    private Database database;

    public ShowingDao() {
        database = AppContext.getInstance().getDatabase();
    }

    public List<Showing> getAllShowings() {
        return database.getShowings();
    }

    public void deleteShowing(Showing selectedShowing) {
        database.deleteShowing(selectedShowing);
    }

    public List<Movie> getAllMovies() {
        return database.getMovies();
    }
}
