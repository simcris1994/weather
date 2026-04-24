package com.example.weather.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(
        double latitude,
        double longitude,
        String timezone,
        @JsonProperty("timezone_abbreviation") String timezoneAbbreviation,
        @JsonProperty("current_units") CurrentUnits currentUnits,
        Current current
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentUnits(
            String time,
            @JsonProperty("temperature_2m") String temperature2m,
            @JsonProperty("wind_speed_10m") String windSpeed10m
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            String time,
            @JsonProperty("temperature_2m") double temperature2m,
            @JsonProperty("wind_speed_10m") double windSpeed10m
    ) {
    }
}