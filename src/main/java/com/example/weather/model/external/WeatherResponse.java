package com.example.weather.model.external;

public record WeatherResponse(
        String time,
        String temperature,
        String windSpeed
) {}
