package com.example.weather.exception;

public class InvalidCoordinatesException extends RuntimeException {

    public InvalidCoordinatesException(String message) {
        super(message);
    }
}
