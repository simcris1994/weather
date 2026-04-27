package com.example.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "weather.cache")
public class WeatherCacheProperties {

    private Duration ttl;
    private int coordinateScale;

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    public int getCoordinateScale() {
        return coordinateScale;
    }

    public void setCoordinateScale(int coordinateScale) {
        this.coordinateScale = coordinateScale;
    }
}
