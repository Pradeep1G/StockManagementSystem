package com.ofss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse> addNewCustomer(@RequestBody Customer c) {
        ApiResponse response = new ApiResponse();
        try {
            customerService.addNewCustomer(c);
            response.setStatus("success");
            response.setMessage("Customer Added Successfully");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Failed to add customer", ex);
            response.setStatus("error");
            response.setMessage("Failed to add customer: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse> getCustomerDetails(@PathVariable("email") String email) {
        ApiResponse response = new ApiResponse();
        try {
            Customer c = customerService.getCustomerDetailsByEmail(email);
            if (c != null) {
                response.setStatus("success");
                response.setMessage("Customer found");
                response.setData(c);
                return ResponseEntity.ok(response);
            } else {
                response.setStatus("error");
                response.setMessage("Customer not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error("Fetch customer error", ex);
            response.setStatus("error");
            response.setMessage("Error fetching customer: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginCustomer(@RequestBody Customer c) {
        ApiResponse response = new ApiResponse();
        try {
            boolean valid = customerService.validateLogin(c.getEmailId(), c.getPassword());
            if (valid) {
                response.setStatus("success");
                response.setMessage("Login successful!");
                return ResponseEntity.ok(response);
            } else {
                response.setStatus("error");
                response.setMessage("Invalid email or password.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            logger.error("Login error", ex);
            response.setStatus("error");
            response.setMessage("Error logging in: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stocksDetails/{customerId}")
    public ResponseEntity<ApiResponse> getCustomerStockDetails(@PathVariable("customerId") int customerId) {
        ApiResponse response = new ApiResponse();
        try {
            List<CustomerStockDetail> details = customerService.getCustomerStockDetails(customerId);
            response.setStatus("success");
            response.setMessage("Fetched customer stock details");
            response.setData(details);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Fetch customer stock details error", ex);
            response.setStatus("error");
            response.setMessage("Error fetching stock details: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
