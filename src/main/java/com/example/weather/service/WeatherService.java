package com.example.weather.service;

import com.example.weather.client.OpenMeteoClient;
import com.example.weather.exception.InvalidCoordinatesException;
import com.example.weather.mapper.WeatherMapper;
import com.example.weather.model.external.WeatherResponse;
import com.example.weather.model.internal.OpenMeteoResponse;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final OpenMeteoClient openMeteoClient;
    private final WeatherMapper weatherMapper;

    public WeatherService(OpenMeteoClient openMeteoClient,
                          WeatherMapper weatherMapper) {
        this.openMeteoClient = openMeteoClient;
        this.weatherMapper = weatherMapper;
    }

    public WeatherResponse getWeather(double latitude, double longitude) {
        validateCoordinates(latitude, longitude);

        OpenMeteoResponse response = openMeteoClient.getWeather(latitude, longitude);

        return weatherMapper.toWeatherResponse(response);
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new InvalidCoordinatesException("Latitude must be between -90 and 90");
        }

        if (longitude < -180 || longitude > 180) {
            throw new InvalidCoordinatesException("Longitude must be between -180 and 180");
        }
    }
}
