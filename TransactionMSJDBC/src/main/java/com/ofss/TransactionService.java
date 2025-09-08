package com.ofss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ofss.exception.TransactionException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionDAO transactionDAO;

    public void performTransaction(Transaction t) throws TransactionException {
    	logger.info("Transaction is of type selling {}",t.getTransactionType());
    	try {
    		if("SELL".equalsIgnoreCase(t.getTransactionType())) {
    			logger.info("Transaction is of type selling ");
            	transactionDAO.increaseVolume(t.getVolume(), t.getStockId());
            	transactionDAO.updateCustomerProfit(t.getCustomerId(), calculateNetProfit(t));
            } else {
            	transactionDAO.decreaseVolume(t.getVolume(), t.getStockId());
            }
        } catch (Exception e) {
            logger.error("Error performing transaction for customer: {}", t.getCustomerId(), e);
            throw new TransactionException("Transaction persistence failed", e);
        }
        try {
            transactionDAO.saveTransaction(t);
            logger.info("Transaction saved for customer: {}", t.getCustomerId());
        } catch (Exception e) {
            logger.error("Error performing transaction for customer: {}", t.getCustomerId(), e);
            throw new TransactionException("Transaction persistence failed", e);
        }
    }

	private Double calculateNetProfit(Transaction t) {
		// TODO Auto-generated method stub
		List<Transaction> txns = transactionDAO.getTransactions(t.getCustomerId(), t.getStockId());
		logger.info("Transaction is of type selling ");
		return ProfitCalculator.calculateRealizedProfit(txns, t);
	}
}
