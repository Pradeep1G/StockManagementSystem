package com.ofss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class StocksService {

    private static final Logger logger = LoggerFactory.getLogger(StocksService.class);

    @Autowired
    private StocksDAO stockDAO;

    public void addStock(Stocks s) throws Exception {
        logger.info("Adding stock: {}", s.getStockId());
        stockDAO.addAStock(s);
    }

    public List<Stocks> getStocks() throws Exception {
        logger.debug("Fetching all stocks");
        return stockDAO.getAllStocks();
    }

    public Stocks getAStock(long sid) throws Exception {
        logger.debug("Fetching stock: {}", sid);
        Stocks stock = stockDAO.getStockById(sid);
        // If not found, DAO should throw or handle appropriately
        return stock;
    }

    public boolean deleteAStockById(long sid) throws Exception {
        logger.info("Deleting stock: {}", sid);
        return stockDAO.deleteAStockById(sid);
    }

    public boolean updateStockById(long sid, Stocks s) throws Exception {
        logger.info("Updating stock: {}", sid);
        return stockDAO.patchStock(sid, s);
    }

    public boolean patchStockById(long sid, Stocks s) throws Exception {
        logger.info("Patching stock: {}", sid);
        return stockDAO.patchStock(sid, s);
    }
    
    public List<String> getMostTransactedStocks() {
        return stockDAO.getMostTransactedStockNames();
    }
    public List<String> getLeastTransactedStocks() {
        return stockDAO.getLeastTransactedStockNames();
    }
}
