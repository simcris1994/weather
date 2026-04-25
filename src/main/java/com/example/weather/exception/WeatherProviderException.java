package com.example.weather.exception;

public class WeatherProviderException extends RuntimeException {

    public WeatherProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
