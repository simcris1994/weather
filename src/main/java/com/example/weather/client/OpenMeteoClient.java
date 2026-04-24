package com.example.weather.client;

import com.example.weather.model.internal.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenMeteoClient {

    private final RestClient restClient;

    public OpenMeteoClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }

    public OpenMeteoResponse getWeather(double latitude, double longitude) {
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
    }
}
