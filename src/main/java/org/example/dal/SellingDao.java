package org.example.dal;

import org.example.AppContext;
import org.example.Database;
import org.example.model.Selling;

import java.util.List;

public class SellingDao {
    private Database database;

    public SellingDao() {
        database = AppContext.getInstance().getDatabase();
    }

    public List<Selling> getSellings() { return database.getSellings(); }

    public void addSelling(Selling selling) { database.addSelling(selling); }
}
