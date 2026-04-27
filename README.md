# Weather App

Small Java application built with Spring Boot. It connects to the [Open-Meteo](https://open-meteo.com/) API, fetches current weather data for a given location, and exposes that data through a REST endpoint.

## What It Does

- Accepts a latitude and longitude from the client
- Calls the Open-Meteo API for current weather data
- Caches weather responses in an H2 database to avoid unnecessary repeated calls
- Normalizes coordinates to 2 decimals before cache lookup and API requests
- Maps the Open-Meteo response into this application's own response format
- Returns the result as JSON from a Spring Boot REST endpoint

## Tech Stack

- Java
- Spring Boot
- H2

## API

### `GET /weather`

Returns current weather data for the given coordinates.

#### Query Parameters

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| `latitude` | `double` | Yes | Latitude value between `-90` and `90` |
| `longitude` | `double` | Yes | Longitude value between `-180` and `180` |

#### Example Request

```text
http://localhost:8080/weather?latitude=57.052193&longitude=9.920227
```

#### Example Response

```json
{
  "time": "2026-04-27T12:00",
  "temperature": "11.0 \u00b0C",
  "windSpeed": "11.9 km/h",
  "relativeHumidity": "51 %",
  "windDirection": "93 \u00b0",
  "windGusts": "26.6 km/h",
  "surfacePressure": "1025.0 hPa",
  "pressureMsl": "1026.4 hPa",
  "cloudCover": "63 %",
  "snowfall": "0.0 cm",
  "showers": "0.0 mm",
  "rain": "0.0 mm",
  "precipitation": "0.0 mm",
  "apparentTemperature": "8.0 \u00b0C"
}
```

## How The Response Is Built

The application does not return the raw Open-Meteo payload directly. Instead, it takes the relevant fields from Open-Meteo's `current` and `current_units` sections and transforms them into a JSON response tailored for this API.

## Caching

To reduce unnecessary calls to Open-Meteo, the application stores fetched weather responses in an H2 database.

Cached entries expire after 5 minutes.

When a request comes in:

- the application first checks whether a cached response already exists for the requested coordinates
- if a fresh cached entry is found, that cached data is returned
- if not, the application calls Open-Meteo, stores the response, and returns it

This keeps the API responsive and avoids repeatedly calling the upstream weather provider for effectively the same location within the cache lifetime.

## Coordinate Normalization

Before looking up cached data or calling Open-Meteo, the latitude and longitude are normalized to 2 decimal places.

For example:

- `57.052193` becomes `57.05`
- `9.920227` becomes `9.92`

This is done so nearby coordinate values that represent practically the same location are treated as the same cache key. Without normalization, very small differences in the input coordinates could create separate cache entries and lead to unnecessary duplicate requests to Open-Meteo.

## Running The Application

Start the Spring Boot application and call the endpoint on port `8080`:

```bash
http://localhost:8080/weather?latitude=57.052193&longitude=9.920227
```

## Notes

- The application uses Open-Meteo as its weather data provider.
- The endpoint currently focuses on current weather data.
- Weather responses are cached in H2.
- Cached entries expire after 5 minutes.
- Cache keys are based on coordinates normalized to 2 decimal places.
- Coordinates are validated before the Open-Meteo request is made.
