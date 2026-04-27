package com.example.weather.service;

import com.example.weather.cache.WeatherCacheEntry;
import com.example.weather.cache.WeatherCacheRepository;
import com.example.weather.client.OpenMeteoClient;
import com.example.weather.config.WeatherCacheProperties;
import com.example.weather.exception.InvalidCoordinatesException;
import com.example.weather.mapper.WeatherMapper;
import com.example.weather.model.external.WeatherResponse;
import com.example.weather.model.internal.OpenMeteoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherCacheRepository weatherCacheRepository;

    @Mock
    private OpenMeteoClient openMeteoClient;

    @Mock
    private WeatherMapper weatherMapper;

    @Mock
    private JsonMapper jsonMapper;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        WeatherCacheProperties cacheProperties = new WeatherCacheProperties();
        cacheProperties.setTtl(Duration.ofMinutes(5));
        cacheProperties.setCoordinateScale(2);

        weatherService = new WeatherService(
                weatherCacheRepository,
                openMeteoClient,
                weatherMapper,
                jsonMapper,
                cacheProperties
        );
    }

    @Test
    void rejectsLatitudeOutsideValidRange() {
        assertThrows(InvalidCoordinatesException.class, () -> weatherService.getWeather(91, 10));
    }

    @Test
    void rejectsLongitudeOutsideValidRange() {
        assertThrows(InvalidCoordinatesException.class, () -> weatherService.getWeather(80, -190));
    }

    @Test
    void returnsFreshCachedResponseWithoutCallingProvider() throws Exception {
        OpenMeteoResponse cachedResponse = createOpenMeteoResponse();
        WeatherResponse mappedResponse = createWeatherResponse();
        WeatherCacheEntry cacheEntry = new WeatherCacheEntry();
        cacheEntry.setExpiresAt(Instant.now().plusSeconds(60));
        cacheEntry.setResponseJson("{\"cached\":true}");

        when(weatherCacheRepository.findByLatitudeAndLongitude(57.05, 9.92))
                .thenReturn(Optional.of(cacheEntry));
        when(jsonMapper.readValue(cacheEntry.getResponseJson(), OpenMeteoResponse.class))
                .thenReturn(cachedResponse);
        when(weatherMapper.toWeatherResponse(cachedResponse)).thenReturn(mappedResponse);

        WeatherResponse result = weatherService.getWeather(57.052193, 9.920227);

        assertEquals(mappedResponse, result);
        verify(openMeteoClient, never()).getWeather(57.05, 9.92);
        verify(weatherCacheRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void fetchesAndCachesProviderResponseUsingNormalizedCoordinates() throws Exception {
        OpenMeteoResponse providerResponse = createOpenMeteoResponse();
        WeatherResponse mappedResponse = createWeatherResponse();

        when(weatherCacheRepository.findByLatitudeAndLongitude(57.05, 9.92))
                .thenReturn(Optional.empty(), Optional.empty());
        when(openMeteoClient.getWeather(57.05, 9.92)).thenReturn(providerResponse);
        when(jsonMapper.writeValueAsString(providerResponse)).thenReturn("{\"cached\":false}");
        when(weatherMapper.toWeatherResponse(providerResponse)).thenReturn(mappedResponse);

        WeatherResponse result = weatherService.getWeather(57.052193, 9.920227);

        assertEquals(mappedResponse, result);
        verify(openMeteoClient).getWeather(57.05, 9.92);

        ArgumentCaptor<WeatherCacheEntry> entryCaptor = ArgumentCaptor.forClass(WeatherCacheEntry.class);
        verify(weatherCacheRepository).save(entryCaptor.capture());

        WeatherCacheEntry savedEntry = entryCaptor.getValue();
        assertEquals(57.05, savedEntry.getLatitude());
        assertEquals(9.92, savedEntry.getLongitude());
        assertEquals("{\"cached\":false}", savedEntry.getResponseJson());
    }

    private OpenMeteoResponse createOpenMeteoResponse() {
        return new OpenMeteoResponse(
                57.052193,
                9.920227,
                "Europe/Berlin",
                "GMT+2",
                new OpenMeteoResponse.CurrentUnits(
                        "iso8601",
                        "°C",
                        "km/h",
                        "%",
                        "°",
                        "km/h",
                        "hPa",
                        "hPa",
                        "%",
                        "cm",
                        "mm",
                        "mm",
                        "mm",
                        "°C"
                ),
                new OpenMeteoResponse.Current(
                        "2026-04-27T12:00",
                        11.0,
                        11.9,
                        51,
                        93,
                        26.6,
                        1025.0,
                        1026.4,
                        63,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        8.0
                )
        );
    }

    private WeatherResponse createWeatherResponse() {
        return new WeatherResponse(
                "2026-04-27T12:00",
                "11.0 °C",
                "11.9 km/h",
                "51 %",
                "93 °",
                "26.6 km/h",
                "1025.0 hPa",
                "1026.4 hPa",
                "63 %",
                "0.0 cm",
                "0.0 mm",
                "0.0 mm",
                "0.0 mm",
                "8.0 °C"
        );
    }
}
