package com.example.weather.service;

import com.example.weather.client.OpenMeteoClient;
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
        OpenMeteoResponse response = openMeteoClient.getWeather(latitude, longitude);

        return weatherMapper.toWeatherResponse(response);
    }
}
