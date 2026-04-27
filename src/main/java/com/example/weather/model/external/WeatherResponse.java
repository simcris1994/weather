package com.example.weather.model.external;

public record WeatherResponse(
        String time,
        String temperature,
        String windSpeed,
        String relativeHumidity,
        String windDirection,
        String windGusts,
        String surfacePressure,
        String pressureMsl,
        String cloudCover,
        String snowfall,
        String showers,
        String rain,
        String precipitation,
        String apparentTemperature
) {}
