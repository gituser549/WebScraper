package apiclient.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.stackexchange.ExchangeItemsDto;
import com.parfyonoff.webscraper.apiclient.dto.stackexchange.ExchangeResponseDto;
import com.parfyonoff.webscraper.apiclient.dto.stackexchange.ExchangeScraper;
import com.parfyonoff.webscraper.apiclient.dto.stackexchange.OwnerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExchangeScraperTest {
    @Test
    public void testExchangeScraperCreation() {
        Exception exc = assertThrows(APIClientException.class, () -> new ExchangeScraper(null));
        assertEquals("fetcher cannot be null", exc.getMessage());

        assertDoesNotThrow(() -> new ExchangeScraper(new Fetcher(new ObjectMapper(), HttpClient.newHttpClient())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"title1", "title2", "title3"})
    public void testExchangeScraperFetch(String title) {
        Fetcher fetcher = mock(Fetcher.class);
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
                                title
                        )
                )
        );

        when(fetcher.fetch(any(HttpRequest.class), eq(ExchangeResponseDto.class))).thenReturn(exchangeResponseDto);

        assertEquals(exchangeResponseDto, new ExchangeScraper(fetcher).fetchToDTO());

        List<?> exchangeScraperResponse = assertInstanceOf(List.class, new ExchangeScraper(fetcher).fetchToMap());
        assertInstanceOf(Map.class, exchangeScraperResponse.getFirst());
        assertInstanceOf(String.class, ((Map<?, ?>) exchangeScraperResponse.getFirst()).keySet().iterator().next());
        assertInstanceOf(String.class, ((Map<?, ?>) exchangeScraperResponse.getFirst()).values().iterator().next());

        Map<String, String> testMap = (Map<String, String>) exchangeScraperResponse.getFirst();

        assertEquals("tag1,tag2,tag3", testMap.get("ex_tags"));
        assertEquals("123",  testMap.get("ex_owner_account_id"));
        assertEquals("1",  testMap.get("ex_owner_reputation"));
        assertEquals("https://owner.com",  testMap.get("ex_owner_link"));
        assertEquals("https://item.com",  testMap.get("ex_link"));
        assertEquals(title,  testMap.get("ex_title"));
    }
}
