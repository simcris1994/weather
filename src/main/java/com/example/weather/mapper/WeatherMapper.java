package com.example.weather.mapper;

import com.example.weather.model.external.WeatherResponse;
import com.example.weather.model.internal.OpenMeteoResponse;
import org.springframework.stereotype.Component;

@Component
public class WeatherMapper {

    public WeatherResponse toWeatherResponse(OpenMeteoResponse response) {
        return new WeatherResponse(
                response.current().time(),
                response.current().temperature2m() + " " + response.currentUnits().temperature2m(),
                response.current().windSpeed10m() + " " + response.currentUnits().windSpeed10m()
        );
    }
}
