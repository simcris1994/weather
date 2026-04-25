package com.example.weather.client;

import com.example.weather.exception.WeatherProviderException;
import com.example.weather.model.internal.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenMeteoClient {

    private final RestClient restClient;

    public OpenMeteoClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }

    public OpenMeteoResponse getWeather(double latitude, double longitude) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("timezone", "Europe/Berlin")
                            .queryParam("current", "temperature_2m,wind_speed_10m")
                            .build())
                    .retrieve()
                    .body(OpenMeteoResponse.class);
        } catch (RestClientException exception) {
            throw new WeatherProviderException("Failed to fetch weather data from Open Meteo", exception);
        }
    }
}
