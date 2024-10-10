package org.example.DAL;

import org.example.AppContext;
import org.example.Database;
import org.example.Model.Movie;
import org.example.Model.Showing;

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
