package com.donation.ddb.Domain.Exception;


public class DataNotFoundException extends RuntimeException{
    public DataNotFoundException(String message) {
        super(message);
    }
    public DataNotFoundException(String message,Throwable cause){
        super(message,cause);
    }
    public DataNotFoundException(String entityName, Object identifier) {
        super(String.format("%s not found with identifier: %s", entityName, identifier));
    }
}
