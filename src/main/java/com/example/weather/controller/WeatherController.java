package com.example.weather.controller;

import com.example.weather.model.external.WeatherResponse;
import com.example.weather.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public WeatherResponse getWeather(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        return weatherService.getWeather(latitude, longitude);
    }
}
