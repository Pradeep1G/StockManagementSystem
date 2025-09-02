package com.ofss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private StocksService stocksService;

    public void addNewCustomer(Customer c) throws Exception {
        try {
            customerDAO.addNewCustomer(c);
            logger.info("New customer added: {}", c.getEmailId());
        } catch (Exception e) {
            logger.error("Error adding customer {}", c.getEmailId(), e);
            throw e;
        }
    }

    public Customer getCustomerDetailsByEmail(String email) throws Exception {
        logger.debug("Fetch customer details: {}", email);
        return customerDAO.getCustomerDetailsByEmail(email);
    }

    public boolean validateLogin(String email, String password) throws Exception {
        logger.debug("Validate login: {}", email);
        return customerDAO.validateLogin(email, password);
    }

    public List<CustomerStockDetail> getCustomerStockDetails(int customerId) throws Exception {
        logger.info("Fetch stock details for customerId: {}", customerId);
        List<Transaction> allTransactions = customerDAO.getAllTransactionsForCustomer(customerId);

        // Build CustomerStockDetail list from transactions + stock service
        Map<Long, List<Transaction>> grouped =
            allTransactions.stream().collect(Collectors.groupingBy(Transaction::getStockId));
        List<CustomerStockDetail> details = new ArrayList<>();
        for (Map.Entry<Long, List<Transaction>> entry : grouped.entrySet()) {
            List<Transaction> txns = entry.getValue();
            int currentVolume = LofoCostBasisCalculator.getCurrentVolume(txns);
            if (currentVolume <= 0) continue;
            double netInvested = LofoCostBasisCalculator.calculateNetInvested(txns);
            Stocks stock = stocksService.getAStock(entry.getKey());
            if (stock == null) continue;
            CustomerStockDetail detail = new CustomerStockDetail();
            detail.setStockId(stock.getStockId());
            detail.setStockName(stock.getStockName());
            detail.setCurrentVolume(currentVolume);
            detail.setNetInvested(netInvested);
            detail.setCurrentPrice(stock.getStockPrice());
            detail.setCurrentValue(currentVolume * stock.getStockPrice());
            details.add(detail);
        }
        return details;
    }
}
