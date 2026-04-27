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
            @JsonProperty("wind_speed_10m") String windSpeed10m,
            @JsonProperty("relative_humidity_2m") String relativeHumidity2m,
            @JsonProperty("wind_direction_10m") String windDirection10m,
            @JsonProperty("wind_gusts_10m") String windGusts10m,
            @JsonProperty("surface_pressure") String surfacePressure,
            @JsonProperty("pressure_msl") String pressureMsl,
            @JsonProperty("cloud_cover") String cloudCover,
            String snowfall,
            String showers,
            String rain,
            String precipitation,
            @JsonProperty("apparent_temperature") String apparentTemperature
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            String time,
            @JsonProperty("temperature_2m") double temperature2m,
            @JsonProperty("wind_speed_10m") double windSpeed10m,
            @JsonProperty("relative_humidity_2m") int relativeHumidity2m,
            @JsonProperty("wind_direction_10m") int windDirection10m,
            @JsonProperty("wind_gusts_10m") double windGusts10m,
            @JsonProperty("surface_pressure") double surfacePressure,
            @JsonProperty("pressure_msl") double pressureMsl,
            @JsonProperty("cloud_cover") int cloudCover,
            double snowfall,
            double showers,
            double rain,
            double precipitation,
            @JsonProperty("apparent_temperature") double apparentTemperature
    ) {
    }
}
