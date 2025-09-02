package com.ofss;

public class ApiResponse {
    private String status;
    private String message;
    private Object data; // can be used for returning payload if needed

    // getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}