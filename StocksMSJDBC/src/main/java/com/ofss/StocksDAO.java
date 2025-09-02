package com.ofss;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class StocksDAO extends JdbcDaoSupport {

    private static final Logger logger = LoggerFactory.getLogger(StocksDAO.class);

    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void init() {
        setDataSource(dataSource);
    }

    public void addAStock(Stocks s) {
        String sql = "INSERT INTO stock (STOCK_ID, STOCK_NAME, STOCK_PRICE, STOCK_VOLUME, LISTED_PRICE, LISTED_DATE, LISTED_EXCHANGE) VALUES (?,?,?,?,?,?,?)";
        getJdbcTemplate().update(sql, s.getStockId(), s.getStockName(), s.getStockPrice(), s.getStockVolume(),
                s.getListedPrice(), s.getListedDate(), s.getListedExchange());
        logger.debug("Stock inserted: {}", s.getStockId());
    }

    public List<Stocks> getAllStocks() {
        String sql = "SELECT * FROM stock";
        List<Stocks> stocks = getJdbcTemplate().query(sql, stockRowMapper);
        logger.debug("Fetched {} stocks", stocks.size());
        return stocks;
    }

    public Stocks getStockById(long id) {
        String sql = "SELECT * FROM stock WHERE STOCK_ID = ?";
        List<Stocks> result = getJdbcTemplate().query(sql, stockRowMapper, id);
        if (result.isEmpty()) {
            logger.info("Stock with ID {} not found", id);
            return null;
        }
        return result.get(0);
    }

    public boolean deleteAStockById(long sid) {
        int rowsAffected = getJdbcTemplate().update("DELETE FROM stock WHERE STOCK_ID = ?", sid);
        logger.debug("Delete stock {} rows affected: {}", sid, rowsAffected);
        return rowsAffected > 0;
    }

    public boolean updateStock(long sid, Stocks s) {
        String sql = "UPDATE stock SET STOCK_NAME = ?, STOCK_PRICE = ?, STOCK_VOLUME = ?, LISTED_PRICE = ?, LISTED_DATE = ?, LISTED_EXCHANGE = ? WHERE STOCK_ID = ?";
        int rowsAffected = getJdbcTemplate().update(sql, s.getStockName(), s.getStockPrice(), s.getStockVolume(),
                s.getListedPrice(), s.getListedDate(), s.getListedExchange(), sid);
        logger.debug("Update stock {} rows affected: {}", sid, rowsAffected);
        return rowsAffected > 0;
    }

    public boolean patchStock(long sid, Stocks s) {
        StringBuilder sql = new StringBuilder("UPDATE stock SET ");
        List<Object> params = new ArrayList<>();
        if (s.getStockName() != null) {
            sql.append("STOCK_NAME = ?, ");
            params.add(s.getStockName());
        }
        if (s.getStockPrice() != 0) {
            sql.append("STOCK_PRICE = ?, ");
            params.add(s.getStockPrice());
        }
        if (s.getStockVolume() != 0) {
            sql.append("STOCK_VOLUME = ?, ");
            params.add(s.getStockVolume());
        }
        if (s.getListedPrice() != 0) {
            sql.append("LISTED_PRICE = ?, ");
            params.add(s.getListedPrice());
        }
        if (s.getListedDate() != null) {
            sql.append("LISTED_DATE = ?, ");
            params.add(s.getListedDate());
        }
        if (s.getListedExchange() != null) {
            sql.append("LISTED_EXCHANGE = ?, ");
            params.add(s.getListedExchange());
        }
        if (params.isEmpty()) {
            logger.warn("No data provided for patching stock {}", sid);
            return false;
        }
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE STOCK_ID = ?");
        params.add(sid);
        int rowsAffected = getJdbcTemplate().update(sql.toString(), params.toArray());
        logger.debug("Patch stock {} rows affected: {}", sid, rowsAffected);
        return rowsAffected > 0;
    }

    // RowMapper for Stocks
    private RowMapper<Stocks> stockRowMapper = (rs, rowNum) -> {
        Stocks stock = new Stocks();
        stock.setStockId(rs.getInt("STOCK_ID"));
        stock.setStockName(rs.getString("STOCK_NAME"));
        stock.setStockPrice(rs.getDouble("STOCK_PRICE"));
        stock.setStockVolume(rs.getInt("STOCK_VOLUME"));
        stock.setListedPrice(rs.getDouble("LISTED_PRICE"));
        stock.setListedDate(rs.getDate("LISTED_DATE"));
        stock.setListedExchange(rs.getString("LISTED_EXCHANGE"));
        return stock;
    };
}
