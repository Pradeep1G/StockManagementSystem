package com.ofss;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class CustomerDAO extends JdbcDaoSupport {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDAO.class);

    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void init() {
        setDataSource(dataSource);
    }

    public void addNewCustomer(Customer c) {
        String getIdSQL = "SELECT customer_seq.NEXTVAL FROM dual";
        Long customerId = getJdbcTemplate().queryForObject(getIdSQL, Long.class);

        String insertCustomer = "INSERT INTO customers (customer_id, first_name, last_name, phone_number, email) VALUES (?,?,?,?,?)";
        getJdbcTemplate().update(insertCustomer, customerId, c.getFirstName(), c.getLastName(), c.getPhoneNumber(), c.getEmailId());

        String insertCredentials = "INSERT INTO credentials (customer_id, email, password) VALUES (?,?,?)";
        getJdbcTemplate().update(insertCredentials, customerId, c.getEmailId(), c.getPassword());

        logger.info("Added customer {}", customerId);
    }

    public Customer getCustomerDetailsByEmail(String email) {
        String sql = "SELECT customer_id, first_name, last_name, phone_number, email FROM customers WHERE email = ?";
        List<Customer> results = getJdbcTemplate().query(sql, (rs, rowNum) -> {
            Customer c = new Customer();
            c.setCustomerId(rs.getInt("customer_id"));
            c.setFirstName(rs.getString("first_name"));
            c.setLastName(rs.getString("last_name"));
            c.setPhoneNumber(rs.getLong("phone_number"));
            c.setEmailId(rs.getString("email"));
            return c;
        }, email);

        return results.isEmpty() ? null : results.get(0);
    }

    public boolean validateLogin(String email, String password) {
        String sql = "SELECT COUNT(*) FROM credentials WHERE email = ? AND password = ?";
        Integer count = getJdbcTemplate().queryForObject(sql, Integer.class, email, password);
        logger.debug("Login for {}: {}", email, count);
        return count != null && count > 0;
    }

    public List<Transaction> getAllTransactionsForCustomer(int customerId) {
        String sql = "SELECT stock_id, transaction_type, transaction_price, volume, transaction_time " +
            "FROM transactions WHERE customer_id = ? ORDER BY stock_id, transaction_time";
        return getJdbcTemplate().query(sql, (rs, rowNum) -> {
            Transaction t = new Transaction();
            t.setStockId(rs.getLong("stock_id"));
            t.setTransactionType(rs.getString("transaction_type"));
            t.setTransactionPrice(rs.getDouble("transaction_price"));
            t.setVolume(rs.getInt("volume"));
            t.setTransactionTime(rs.getTimestamp("transaction_time").toLocalDateTime());
            return t;
        }, customerId);
    }
}
