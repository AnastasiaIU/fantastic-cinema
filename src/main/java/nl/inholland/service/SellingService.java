package nl.inholland.service;

import nl.inholland.dal.SellingDao;
import nl.inholland.model.Selling;

import java.util.List;

public class SellingService {
    private SellingDao sellingDao;

    public SellingService() {
        sellingDao = new SellingDao();
    }

    public List<Selling> getSellings() { return sellingDao.getSellings(); }

    public void addSelling(Selling selling) { sellingDao.addSelling(selling); }
}
