package com.ofss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List; // Prefer interface over implementation

@RestController
@RequestMapping("/stock")
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StocksService stocksService;

    @PostMapping
    public ResponseEntity<ApiResponse> addStock(@RequestBody Stocks stock) {
        ApiResponse response = new ApiResponse();
        try {
            stocksService.addStock(stock);
            logger.info("Added stock: {}", stock.getStockId());
            response.setStatus("success");
            response.setMessage("Stock Added Successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to add stock", e);
            response.setStatus("error");
            response.setMessage("Failed to add stock: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getStocks() {
        ApiResponse response = new ApiResponse();
        try {
            logger.debug("Fetching all stocks...");
            List<Stocks> allStocks = stocksService.getStocks();
            response.setStatus("success");
            response.setMessage("Fetched all stocks");
            response.setData(allStocks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching stocks", e);
            response.setStatus("error");
            response.setMessage("Could not fetch stocks");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<ApiResponse> getAStock(@PathVariable("stockId") long stockId) {
        ApiResponse response = new ApiResponse();
        try {
            Stocks stock = stocksService.getAStock(stockId);
            if (stock == null) {
                response.setStatus("error");
                response.setMessage("Stock not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.setStatus("success");
            response.setMessage("Stock fetched successfully");
            response.setData(stock);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching stock {}", stockId, e);
            response.setStatus("error");
            response.setMessage("Could not fetch stock: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<ApiResponse> deleteAStockById(@PathVariable("stockId") long stockId) {
        ApiResponse response = new ApiResponse();
        try {
            boolean deleted = stocksService.deleteAStockById(stockId);
            if (deleted) {
                logger.info("Deleted stock {}", stockId);
                response.setStatus("success");
                response.setMessage("Stock deleted successfully");
            } else {
                response.setStatus("error");
                response.setMessage("Stock not found to delete");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting stock {}", stockId, e);
            response.setStatus("error");
            response.setMessage("Could not delete stock: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{stockId}")
    public ResponseEntity<ApiResponse> updateAStockById(@PathVariable("stockId") long stockId, @RequestBody Stocks stock) {
        ApiResponse response = new ApiResponse();
        try {
            boolean updated = stocksService.updateStockById(stockId, stock);
            if (updated) {
                logger.info("Updated stock {}", stockId);
                response.setStatus("success");
                response.setMessage("Stock updated successfully");
            } else {
                response.setStatus("error");
                response.setMessage("Stock not found to update");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating stock {}", stockId, e);
            response.setStatus("error");
            response.setMessage("Could not update stock: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{stockId}")
    public ResponseEntity<ApiResponse> patchAStockById(@PathVariable("stockId") long stockId, @RequestBody Stocks stock) {
        ApiResponse response = new ApiResponse();
        try {
            boolean patched = stocksService.patchStockById(stockId, stock);
            if (patched) {
                logger.info("Patched stock {}", stockId);
                response.setStatus("success");
                response.setMessage("Stock patched successfully");
            } else {
                response.setStatus("error");
                response.setMessage("Stock not found to patch");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error patching stock {}", stockId, e);
            response.setStatus("error");
            response.setMessage("Could not patch stock: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/mostTransacted")
    public ResponseEntity<ApiResponse> getMostTransactedStocks() {
        ApiResponse response = new ApiResponse();
        List<String> names = stocksService.getMostTransactedStocks();
        response.setStatus("success");
        response.setData(names);
        response.setMessage("Most transacted stocks found");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leastTransacted")
    public ResponseEntity<ApiResponse> getLeastTransactedStocks() {
        ApiResponse response = new ApiResponse();
        List<String> names = stocksService.getLeastTransactedStocks();
        response.setStatus("success");
        response.setData(names);
        response.setMessage("Least transacted stocks found");
        return ResponseEntity.ok(response);
    }
}
