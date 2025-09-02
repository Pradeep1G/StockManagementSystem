package com.ofss;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class TransactionDAO extends JdbcDaoSupport {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDAO.class);

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void init() {
        setDataSource(dataSource);
    }

    public void saveTransaction(Transaction t) {
        String getIdSQL = "SELECT transaction_seq.NEXTVAL FROM dual";
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if (jdbcTemplate == null) {
            logger.error("JdbcTemplate is not initialized");
            throw new IllegalStateException("JdbcTemplate is not initialized");
        }
        Long transactionId = jdbcTemplate.queryForObject(getIdSQL, Long.class);
        String insertSQL = "INSERT INTO transactions (transaction_id, stock_id, customer_id, transaction_type, transaction_price, volume, transaction_time) VALUES (?,?,?,?,?,?,?)";
        Timestamp txTime = (t.getTransactionTime() != null) ? Timestamp.valueOf(t.getTransactionTime()) : new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(insertSQL, transactionId, t.getStockId(), t.getCustomerId(), t.getTransactionType(), t.getTransactionPrice(), t.getVolume(), txTime);
        logger.debug("Transaction inserted: {} for stock: {}", transactionId, t.getStockId());
    }
}
