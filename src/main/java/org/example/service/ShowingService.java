package org.example.service;

import org.example.dal.ShowingDao;
import org.example.model.Showing;

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
}
