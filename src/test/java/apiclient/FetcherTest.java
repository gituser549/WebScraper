package apiclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.stackexchange.ExchangeItemsDto;
import com.parfyonoff.webscraper.apiclient.dto.stackexchange.ExchangeResponseDto;
import com.parfyonoff.webscraper.apiclient.dto.stackexchange.OwnerDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FetcherTest {
    @Test
    public void testFetcherCreation() {
        Exception exc = assertThrows(APIClientException.class, () -> new Fetcher(null, null));
        assertEquals("Fetcher: objectMapper cannot be null", exc.getMessage());

        exc = assertThrows(APIClientException.class, () -> new Fetcher(new ObjectMapper(), null));
        assertEquals("Fetcher: httpClient cannot be null", exc.getMessage());

        assertDoesNotThrow(() -> new Fetcher(new ObjectMapper(), HttpClient.newHttpClient()));
    }

    @Test
    public void testFetcherFetch() throws InterruptedException, IOException {
        // happy path
        ExchangeResponseDto exchangeResponseDto = new ExchangeResponseDto(
                false,
                1,
                0,
                List.of(
                        new ExchangeItemsDto(
                                List.of("tag1", "tag2", "tag3"),
                                new OwnerDto(
                                        123,
                                        1,
                                        1,
                                        "https://owner.com"
                                ),
                                0,
                                0,
                                0,
                                1234567L,
                                "https://item.com",
                                "title"
                        )
                )
        );

        HttpClient httpClient = mock(HttpClient.class);
        HttpRequest httpRequest = mock(HttpRequest.class);
        ObjectMapper objectMapper = new ObjectMapper();

        HttpResponse<String> response = mock(HttpResponse.class);

        Fetcher fetcher = assertInstanceOf(Fetcher.class, new Fetcher(objectMapper, httpClient));

        when(response.body()).thenReturn(objectMapper.writeValueAsString(exchangeResponseDto));
        when(response.statusCode()).thenReturn(200);
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(response);

        assertEquals(exchangeResponseDto, fetcher.fetch(httpRequest, ExchangeResponseDto.class));

        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenThrow(InterruptedException.class);

        // interrupted exception
        Exception exc = assertThrows(APIClientException.class, () -> fetcher.fetch(httpRequest, ExchangeResponseDto.class));
        assertTrue(exc.getMessage().startsWith("Fetcher: Interrupted while waiting for request from some API: "));

        // check is already with flag reset
        assertTrue(Thread.interrupted());

        // IOException when sending request
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenThrow(IOException.class);

        exc = assertThrows(APIClientException.class, () -> fetcher.fetch(httpRequest, ExchangeResponseDto.class));
        assertTrue(exc.getMessage().startsWith("Fetcher: IOException while waiting for request from some API: "));

        // bad status code
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(response);
        when(response.body()).thenReturn(objectMapper.writeValueAsString(exchangeResponseDto));
        when(response.statusCode()).thenReturn(404);

        exc = assertThrows(APIClientException.class, () -> fetcher.fetch(httpRequest, ExchangeResponseDto.class));
        assertEquals("Fetcher: Failed scraping : HTTP code : 404", exc.getMessage());

        when(response.statusCode()).thenReturn(200);
        // JsonMapping exception
        objectMapper = mock(ObjectMapper.class);
        Fetcher fetcherJacksonCheck = new Fetcher(objectMapper, httpClient);

        when(objectMapper.readValue(any(String.class), eq(ExchangeResponseDto.class))).thenThrow(JsonMappingException.class);

        exc = assertThrows(APIClientException.class, () -> fetcherJacksonCheck.fetch(httpRequest, ExchangeResponseDto.class));
        assertTrue(exc.getMessage().startsWith("Fetcher: JSON mapping error: "));

        // JsonProcessingException
        when(objectMapper.readValue(any(String.class), eq(ExchangeResponseDto.class))).thenThrow(JsonProcessingException.class);

        exc = assertThrows(APIClientException.class, () -> fetcherJacksonCheck.fetch(httpRequest, ExchangeResponseDto.class));
        assertTrue(exc.getMessage().startsWith("Fetcher: JSON processing error: "));
    }
}
