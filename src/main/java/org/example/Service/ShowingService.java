package org.example.Service;

import org.example.DAL.ShowingDao;
import org.example.Model.Movie;
import org.example.Model.Showing;

import java.util.List;

public class ShowingService {
    private ShowingDao showingDao;

    public ShowingService() {
        showingDao = new ShowingDao();
    }

    public List<Showing> getAllShowings() {
        return showingDao.getAllShowings();
    }

    public void deleteShowing(Showing selectedShowing) {
        showingDao.deleteShowing(selectedShowing);
    }

    public List<Movie> getAllMovies() {
        return showingDao.getAllMovies();
    }
}
