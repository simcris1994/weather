package com.example.weather.cache;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherCacheRepository extends JpaRepository<WeatherCacheEntry, Long> {

    Optional<WeatherCacheEntry> findByLatitudeAndLongitude(double latitude, double longitude);
}
