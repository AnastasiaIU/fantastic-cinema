package nl.inholland.dal;

import nl.inholland.Database;
import nl.inholland.model.Selling;

import java.util.List;

public class SellingDao {
    private Database database;

    public SellingDao() {
        //database = AppContext.getInstance().getDatabase();
    }

    public List<Selling> getSellings() { return database.getSells(); }

    public void addSelling(Selling selling) { database.addSelling(selling); }
}
