package com.booking.exception;

public class ExistingDataException extends RuntimeException{

    public ExistingDataException(String message) {
        super(message);
    }
}
