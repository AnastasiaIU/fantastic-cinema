package nl.inholland.service;

import nl.inholland.dal.ShowingDao;
import nl.inholland.model.Showing;

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

    public void addUpdateShowing(Showing showing) {
        showingDao.addUpdateShowing(showing);
    }

    public List<Showing> getAllUpcomingShowings() {
        return showingDao.getAllUpcomingShowings();
    }

    public void sellTicket(int showingId, int[] seat) {
        showingDao.sellTicket(showingId, seat);
    }
}
