package com.example.weather.controller;

import com.example.weather.exception.InvalidCoordinatesException;
import com.example.weather.exception.WeatherProviderException;
import com.example.weather.model.external.WeatherResponse;
import com.example.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
@Import(GlobalExceptionHandler.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Test
    void returnsWeatherResponse() throws Exception {
        when(weatherService.getWeather(57.05, 9.92)).thenReturn(new WeatherResponse(
                "2026-04-27T12:00",
                "11.0 °C",
                "11.9 km/h",
                "51 %",
                "93 °0",
                "26.6 km/h",
                "1025.0 hPa",
                "1026.4 hPa",
                "63 %",
                "0.0 cm",
                "0.0 mm",
                "0.0 mm",
                "0.0 mm",
                "8.0 °C"
        ));

        mockMvc.perform(get("/weather")
                        .param("latitude", "57.05")
                        .param("longitude", "9.92"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value("2026-04-27T12:00"))
                .andExpect(jsonPath("$.temperature").value("11.0 °C"))
                .andExpect(jsonPath("$.windSpeed").value("11.9 km/h"));
    }

    @Test
    void returnsBadRequestForInvalidCoordinates() throws Exception {
        when(weatherService.getWeather(91.0, 9.92))
                .thenThrow(new InvalidCoordinatesException("Latitude must be between -90 and 90"));

        mockMvc.perform(get("/weather")
                        .param("latitude", "91")
                        .param("longitude", "9.92"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Latitude must be between -90 and 90"))
                .andExpect(jsonPath("$.path").value("/weather"));
    }

    @Test
    void returnsBadRequestForInvalidParameterType() throws Exception {
        mockMvc.perform(get("/weather")
                        .param("latitude", "not-a-number")
                        .param("longitude", "9.92"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/weather"));
    }

    @Test
    void returnsBadGatewayWhenProviderFails() throws Exception {
        when(weatherService.getWeather(57.05, 9.92))
                .thenThrow(new WeatherProviderException(
                        "Failed to fetch weather data from Open Meteo",
                        new RuntimeException("Open Meteo request failed")
                ));

        mockMvc.perform(get("/weather")
                        .param("latitude", "57.05")
                        .param("longitude", "9.92"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"))
                .andExpect(jsonPath("$.message").value("Failed to fetch weather data from Open Meteo"))
                .andExpect(jsonPath("$.path").value("/weather"));
    }
}
