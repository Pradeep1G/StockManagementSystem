package com.ofss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ofss.exception.TransactionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionDAO transactionDAO;

    public void performTransaction(Transaction t) throws TransactionException {
        try {
            transactionDAO.saveTransaction(t);
            logger.info("Transaction saved for customer: {}", t.getCustomerId());
        } catch (Exception e) {
            logger.error("Error performing transaction for customer: {}", t.getCustomerId(), e);
            throw new TransactionException("Transaction persistence failed", e);
        }
    }
}
