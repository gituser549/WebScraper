package apiclient.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.hackernews.HackerNewsResponseDto;
import com.parfyonoff.webscraper.apiclient.dto.hackernews.HackerNewsScraper;
import com.parfyonoff.webscraper.apiclient.dto.hackernews.HitsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HackerNewsScraperTest {
    @Test
    public void testHackerNewsScraperCreation() {
        Exception exc = assertThrows(APIClientException.class, () -> new HackerNewsScraper(null));
        assertEquals("fetcher cannot be null", exc.getMessage());

        assertDoesNotThrow(() -> new HackerNewsScraper(new Fetcher(new ObjectMapper(), HttpClient.newHttpClient())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://github.com", "https://localhost:8080", "https://hh.ru"})
    public void testHackerNewsScraperFetch(String url) {
        Fetcher fetcher = mock(Fetcher.class);
        HackerNewsResponseDto hackerNewsResponseDto = new HackerNewsResponseDto(
                new ArrayList<>(List.of(new HitsDto(
                        List.of("one", "two", "three"),
                        "smb",
                        List.of(1, 2, 3),
                        "2026-03-25",
                        0,
                        UUID.randomUUID().toString(),
                        1,
                        1,
                        url,
                        "2026-03-25",
                        "https://localhost:8080"
                ))) {},
                0,
                0,
                0,
                0,
                0,
                "",
                0
        );

        when(fetcher.fetch(any(HttpRequest.class), eq(HackerNewsResponseDto.class))).thenReturn(hackerNewsResponseDto);

        assertEquals(hackerNewsResponseDto, new HackerNewsScraper(fetcher).fetchToDTO());


        List<?> hackerNewsScraperResponse = assertInstanceOf(List.class, new HackerNewsScraper(fetcher).fetchToMap());
        assertInstanceOf(Map.class, hackerNewsScraperResponse.getFirst());
        assertInstanceOf(String.class, ((Map<?, ?>) hackerNewsScraperResponse.getFirst()).keySet().iterator().next());
        assertInstanceOf(String.class, ((Map<?, ?>) hackerNewsScraperResponse.getFirst()).values().iterator().next());

        Map<String, String> testMap = (Map<String, String>) hackerNewsScraperResponse.getFirst();
        assertEquals("one,two,three", testMap.get("hn_tags"));
        assertEquals("smb", testMap.get("hn_author"));
        assertEquals("1,2,3", testMap.get("hn_children"));
        assertEquals("2026-03-25", testMap.get("hn_createdAt"));
        assertEquals("0", testMap.get("hn_numComments"));
        assertEquals("https://localhost:8080", testMap.get("hn_url"));
        assertEquals("2026-03-25", testMap.get("hn_updatedAt"));
        assertEquals(url, testMap.get("hn_title"));
    }
}
