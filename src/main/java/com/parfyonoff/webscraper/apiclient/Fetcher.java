package com.parfyonoff.webscraper.apiclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Fetcher {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public Fetcher(ObjectMapper objectMapper, HttpClient httpClient) {
        if (objectMapper == null) {
            throw new APIClientException("objectMapper cannot be null");
        } else if (httpClient == null) {
            throw new APIClientException("httpClient cannot be null");
        }

        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    public <T> T fetch(HttpRequest httpRequest, Class<T> dtoClass) {
        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
            throw new APIClientException("EXCHANGE SCRAPER: Interrupted while waiting for request from Exchange API: " + exc.getMessage());
        } catch (IOException exc) {
            throw new APIClientException("EXCHANGE SCRAPER: IOException while waiting for request from Exchange API: " + exc.getMessage());
        }

        if (httpResponse.statusCode() < 200 || httpResponse.statusCode() > 299) {
            throw new APIClientException("EXCHANGE SCRAPER: Failed ExchangeResponseDtoScrapping : HTTP error code : " + httpResponse.statusCode());
        }

        T data;

        try {
            data = objectMapper.readValue(httpResponse.body(), dtoClass);
        } catch (JsonMappingException exc) {
            throw new APIClientException("EXCHANGE SCRAPER: JSON mapping error: " + exc.getMessage());
        } catch (JsonProcessingException exc) {
            throw new APIClientException("EXCHANGE SCRAPER: JSON processing error: " + exc.getMessage());
        }

        return data;
    }
}
