package com.splitbite.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
    private List<String> errors;

    public ApiErrorResponse() {}

    public ApiErrorResponse(int status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
