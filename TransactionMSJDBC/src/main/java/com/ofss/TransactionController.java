package com.ofss;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofss.exception.StockNotFoundException;
import com.ofss.exception.TransactionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse> performTransaction(@RequestBody Transaction t) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            if (t == null) {
                logger.warn("Received null transaction object");
                apiResponse.setStatus("error");
                apiResponse.setMessage("Invalid transaction data received.");
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }

            t.setTransactionPrice(t.getVolume() * getTransactionPrice(t.getStockId()));
            t.setTransactionTime(LocalDateTime.now());

            transactionService.performTransaction(t); // throws exception if fails
            apiResponse.setStatus("success");
            apiResponse.setMessage("Transaction successful!");
            return ResponseEntity.ok(apiResponse);
        } catch (StockNotFoundException ex) {
            logger.error("Stock not found: {}", t != null ? t.getStockId() : "unknown", ex);
            apiResponse.setStatus("error");
            apiResponse.setMessage("Stock not found: " + (t != null ? t.getStockId() : "unknown"));
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (TransactionException ex) {
            logger.error("Transaction failed for customer: {}", t != null ? t.getCustomerId() : "unknown", ex);
            apiResponse.setStatus("error");
            apiResponse.setMessage("Transaction failed: " + ex.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("Unknown error performing transaction", ex);
            apiResponse.setStatus("error");
            apiResponse.setMessage("Unknown error: " + ex.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public double getTransactionPrice(long stockId) throws StockNotFoundException {
        ApiResponse response = restTemplate.getForObject(
            "http://STOCKSMSJDBC/stock/{stockId}", ApiResponse.class, stockId);
        if (response != null && "success".equalsIgnoreCase(response.getStatus()) && response.getData() != null) {
            // Use Jackson (or similar) to map “data” to Stocks
            ObjectMapper mapper = new ObjectMapper();
            Stocks stock = mapper.convertValue(response.getData(), Stocks.class);
            if (stock.getStockPrice() > 0) {
                return stock.getStockPrice();
            }
        }
        throw new StockNotFoundException("Stock not found for ID: " + stockId);
    }
}
