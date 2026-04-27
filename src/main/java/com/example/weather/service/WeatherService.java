package com.example.weather.service;

import com.example.weather.cache.WeatherCacheEntry;
import com.example.weather.cache.WeatherCacheRepository;
import com.example.weather.client.OpenMeteoClient;
import com.example.weather.config.WeatherCacheProperties;
import com.example.weather.exception.InvalidCoordinatesException;
import com.example.weather.exception.WeatherProviderException;
import com.example.weather.mapper.WeatherMapper;
import com.example.weather.model.external.WeatherResponse;
import com.example.weather.model.internal.OpenMeteoResponse;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class WeatherService {

    private final WeatherCacheRepository weatherCacheRepository;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherMapper weatherMapper;
    private final JsonMapper jsonMapper;
    private final Duration cacheTtl;
    private final int coordinateScale;

    public WeatherService(WeatherCacheRepository weatherCacheRepository,
                          OpenMeteoClient openMeteoClient,
                          WeatherMapper weatherMapper,
                          JsonMapper jsonMapper,
                          WeatherCacheProperties cacheProperties) {
        this.weatherCacheRepository = weatherCacheRepository;
        this.openMeteoClient = openMeteoClient;
        this.weatherMapper = weatherMapper;
        this.jsonMapper = jsonMapper;
        this.cacheTtl = cacheProperties.getTtl();
        this.coordinateScale = cacheProperties.getCoordinateScale();
    }

    public WeatherResponse getWeather(double latitude, double longitude) {
        validateCoordinates(latitude, longitude);

        double normalizedLatitude = normalizeCoordinate(latitude);
        double normalizedLongitude = normalizeCoordinate(longitude);

        Optional<OpenMeteoResponse> cachedResponse = findFreshCachedResponse(
                normalizedLatitude,
                normalizedLongitude
        );

        if (cachedResponse.isPresent()) {
            return weatherMapper.toWeatherResponse(cachedResponse.get());
        }

        OpenMeteoResponse response = openMeteoClient.getWeather(normalizedLatitude, normalizedLongitude);
        cacheResponse(normalizedLatitude, normalizedLongitude, response);

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

    private Optional<OpenMeteoResponse> findFreshCachedResponse(double latitude, double longitude) {
        return weatherCacheRepository.findByLatitudeAndLongitude(latitude, longitude)
                .filter(entry -> entry.getExpiresAt().isAfter(Instant.now()))
                .flatMap(this::deserializeCachedResponse);
    }

    private Optional<OpenMeteoResponse> deserializeCachedResponse(WeatherCacheEntry entry) {
        try {
            return Optional.of(jsonMapper.readValue(entry.getResponseJson(), OpenMeteoResponse.class));
        } catch (JacksonException exception) {
            return Optional.empty();
        }
    }

    private void cacheResponse(double latitude, double longitude, OpenMeteoResponse response) {
        WeatherCacheEntry entry = weatherCacheRepository.findByLatitudeAndLongitude(latitude, longitude)
                .orElseGet(WeatherCacheEntry::new);

        Instant now = Instant.now();

        entry.setLatitude(latitude);
        entry.setLongitude(longitude);
        entry.setFetchedAt(now);
        entry.setExpiresAt(now.plus(cacheTtl));
        entry.setResponseJson(serializeResponse(response));

        weatherCacheRepository.save(entry);
    }

    private String serializeResponse(OpenMeteoResponse response) {
        try {
            return jsonMapper.writeValueAsString(response);
        } catch (JacksonException exception) {
            throw new WeatherProviderException("Failed to cache weather data", exception);
        }
    }

    private double normalizeCoordinate(double coordinate) {
        return BigDecimal.valueOf(coordinate)
                .setScale(coordinateScale, RoundingMode.HALF_UP)
                .doubleValue();
    }


}
