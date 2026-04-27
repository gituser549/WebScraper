package apiclient.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.headhunter.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeadHunterScraperTest {
    @Test
    public void testHeadHunterScraperCreation() {
        Exception exc = assertThrows(APIClientException.class, () -> new HeadHunterScraper(null));
        assertEquals("fetcher cannot be null", exc.getMessage());

        assertDoesNotThrow(() -> new HeadHunterScraper(new Fetcher(new ObjectMapper(), HttpClient.newHttpClient())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Moscow", "SPb", "Rybinsk"})
    public void testHeadHunterScraperFetch(String city) {
        Fetcher fetcher = mock(Fetcher.class);
        HeadHunterResponseDto headHunterResponseDto = new HeadHunterResponseDto(
                List.of(
                        new HeadHunterItemsDto(
                                UUID.randomUUID().toString(),
                                "someName",
                                new AreaDto(
                                        UUID.randomUUID().toString(),
                                        city,
                                        "https://someCity.com"
                                ),
                                new SalaryDto(
                                        100_000,
                                        150_000,
                                        "RUB",
                                        true
                                ),
                                new EmployerDto(
                                        UUID.randomUUID().toString(),
                                        "someEmployer",
                                        "https://someEmployer.com"
                                )
                        )
                )
        );
        when(fetcher.fetch(any(HttpRequest.class), eq(HeadHunterResponseDto.class))).thenReturn(headHunterResponseDto);

        assertEquals(headHunterResponseDto, new HeadHunterScraper(fetcher).fetchToDTO());

        List<?> headHunterScraperResponse = assertInstanceOf(List.class, new HeadHunterScraper(fetcher).fetchToMap());
        assertInstanceOf(Map.class, headHunterScraperResponse.getFirst());
        assertInstanceOf(String.class, ((Map<?, ?>) headHunterScraperResponse.getFirst()).keySet().iterator().next());
        assertInstanceOf(String.class, ((Map<?, ?>) headHunterScraperResponse.getFirst()).values().iterator().next());

        Map<String, String> testMap = (Map<String, String>) headHunterScraperResponse.getFirst();

        assertEquals(headHunterResponseDto.items().getFirst().id(), testMap.get("hh_id"));
        assertEquals("someName", testMap.get("hh_name"));
        assertEquals(headHunterResponseDto.items().getFirst().area().id(), testMap.get("hh_area_id"));
        assertEquals(city, testMap.get("hh_area_name"));
        assertEquals("https://someCity.com", testMap.get("hh_area_url"));
        assertEquals("100000", testMap.get("hh_salary_from"));
        assertEquals("150000", testMap.get("hh_salary_to"));
        assertEquals("RUB", testMap.get("hh_salary_currency"));
        assertEquals("true", testMap.get("hh_salary_gross"));
        assertEquals(headHunterResponseDto.items().getFirst().employer().id(), testMap.get("hh_employer_id"));
        assertEquals("someEmployer", testMap.get("hh_employer_name"));
        assertEquals("https://someEmployer.com", testMap.get("hh_employer_url"));
    }
}
