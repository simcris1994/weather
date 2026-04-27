package com.example.weather.mapper;

import com.example.weather.model.external.WeatherResponse;
import com.example.weather.model.internal.OpenMeteoResponse;
import org.springframework.stereotype.Component;

@Component
public class WeatherMapper {

    public WeatherResponse toWeatherResponse(OpenMeteoResponse response) {
        OpenMeteoResponse.Current current = response.current();
        OpenMeteoResponse.CurrentUnits currentUnits = response.currentUnits();

        return new WeatherResponse(
                current.time(),
                formatMeasurement(current.temperature2m(), currentUnits.temperature2m()),
                formatMeasurement(current.windSpeed10m(), currentUnits.windSpeed10m()),
                formatMeasurement(current.relativeHumidity2m(), currentUnits.relativeHumidity2m()),
                formatMeasurement(current.windDirection10m(), currentUnits.windDirection10m()),
                formatMeasurement(current.windGusts10m(), currentUnits.windGusts10m()),
                formatMeasurement(current.surfacePressure(), currentUnits.surfacePressure()),
                formatMeasurement(current.pressureMsl(), currentUnits.pressureMsl()),
                formatMeasurement(current.cloudCover(), currentUnits.cloudCover()),
                formatMeasurement(current.snowfall(), currentUnits.snowfall()),
                formatMeasurement(current.showers(), currentUnits.showers()),
                formatMeasurement(current.rain(), currentUnits.rain()),
                formatMeasurement(current.precipitation(), currentUnits.precipitation()),
                formatMeasurement(current.apparentTemperature(), currentUnits.apparentTemperature())
        );
    }

    private String formatMeasurement(Object value, String unit) {
        return value + " " + unit;
    }
}
