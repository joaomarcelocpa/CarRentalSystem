package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponseDTO {
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private String path;
    private List<ValidationError> validationErrors;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(String message, String code) {
        this();
        this.message = message;
        this.code = code;
    }

    public ErrorResponseDTO(String message, String code, String path) {
        this(message, code);
        this.path = path;
    }

    public ErrorResponseDTO(String message, String code, String path, List<ValidationError> validationErrors) {
        this(message, code, path);
        this.validationErrors = validationErrors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;

        public ValidationError() {}

        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
    }
}