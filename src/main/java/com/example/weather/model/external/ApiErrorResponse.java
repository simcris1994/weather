package com.example.weather.model.external;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path
) {
}
