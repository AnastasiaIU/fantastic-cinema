package org.example.dal;

import org.example.AppContext;
import org.example.Database;
import org.example.model.Showing;

import java.util.List;

public class ShowingDao {
    private Database database;

    public ShowingDao() {
        database = AppContext.getInstance().getDatabase();
    }

    public List<Showing> getAllShowings() {
        return database.getAllShowings();
    }

    public void deleteShowing(Showing selectedShowing) {
        database.deleteShowing(selectedShowing);
    }

    public void addUpdateShowing(Showing showing) {
        database.addUpdateShowing(showing);
    }

    public List<Showing> getAllUpcomingShowings() {
        return database.getAllUpcomingShowings();
    }

    public void sellTicket(int showingId, int[] seat) {
        database.sellTicket(showingId, seat);
    }
}
