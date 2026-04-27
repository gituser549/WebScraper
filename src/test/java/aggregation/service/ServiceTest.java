package aggregation.service;

import com.parfyonoff.webscraper.aggregation.AggregatedData;
import com.parfyonoff.webscraper.aggregation.AggregationException;
import com.parfyonoff.webscraper.aggregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.dto.hackernews.HackerNewsResponseDto;
import com.parfyonoff.webscraper.apiclient.dto.headhunter.HeadHunterResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    private static final String API_NAME = "testAPIClient";

    @Mock
    private APIClient<HackerNewsResponseDto> testHnApiClient;

    @Mock
    private APIClient<HeadHunterResponseDto> testHhApiClient;

    private HackerNewsResponseDto hackerNewsResponseDto;

    private List<APIClient<?>> testAPIClients;
    private List<String> apiClientsNames;

    @BeforeEach
    public void setup() {
        hackerNewsResponseDto = new HackerNewsResponseDto(
                new ArrayList<>(),
                0,
                0,
                0,
                0,
                0,
                "",
                0
        );

        testAPIClients = new ArrayList<>(List.of(testHnApiClient));
        apiClientsNames = new ArrayList<>(List.of(API_NAME));
    }

    @Test
    void testServiceCreation() {
        Exception exc = assertThrows(AggregationException.class, () -> new Service(null, new ArrayList<>()));
        assertEquals("API Clients names list is null or empty", exc.getMessage());

        exc = assertThrows(AggregationException.class, () -> new Service(new ArrayList<>(), new ArrayList<>()));
        assertEquals("API Clients names list is null or empty", exc.getMessage());

        exc = assertThrows(AggregationException.class, () -> new Service(apiClientsNames, null));
        assertEquals("API Clients list is null or empty", exc.getMessage());

        exc = assertThrows(AggregationException.class, () -> new Service(apiClientsNames, new ArrayList<>()));
        assertEquals("API Clients list is null or empty", exc.getMessage());

        exc = assertThrows(AggregationException.class, () -> new Service(apiClientsNames, new ArrayList<>(List.of(testHnApiClient, testHhApiClient))));
        assertEquals("API Clients list size is not equal to API Clients list size", exc.getMessage());

        assertDoesNotThrow(() -> new Service(apiClientsNames, testAPIClients));

        List<String> doubleApiClientNames = new ArrayList<>(apiClientsNames);
        doubleApiClientNames.add(API_NAME);

        List<APIClient<?>> doubleApiClients = new ArrayList<>(testAPIClients);
        doubleApiClients.add(testHnApiClient);

        assertDoesNotThrow(() -> new Service(doubleApiClientNames, doubleApiClients));
    }

    @Test
    void testFetchAsAggregatedType() {
        Service service = new Service(apiClientsNames, testAPIClients);

        Exception exc = assertThrows(AggregationException.class, () -> service.fetchAsAggregatedType(null));
        assertEquals("API Client name is null or empty", exc.getMessage());

        exc = assertThrows(AggregationException.class, () -> service.fetchAsAggregatedType(""));
        assertEquals("API Client name is null or empty", exc.getMessage());

        String fakeName = "UNKNOWN_NAME";
        exc = assertThrows(AggregationException.class, () -> service.fetchAsAggregatedType(fakeName));
        assertEquals("API Client not found: " + fakeName, exc.getMessage());

        when(testHnApiClient.fetchToDTO()).thenReturn(hackerNewsResponseDto);
        AggregatedData aggData = assertInstanceOf(AggregatedData.class, service.fetchAsAggregatedType(API_NAME));

        verify(testHnApiClient, times(1)).fetchToDTO();

        assertEquals(hackerNewsResponseDto, aggData.data());
    }

    @Test
    void testFetchAsMapList() {
        Service service = new Service(apiClientsNames, testAPIClients);

        Exception exc = assertThrows(AggregationException.class, () -> service.fetchAsMapList(null));
        assertEquals("API Client name is null or empty", exc.getMessage());

        exc = assertThrows(AggregationException.class, () -> service.fetchAsMapList(""));
        assertEquals("API Client name is null or empty", exc.getMessage());

        String fakeName = "UNKNOWN_NAME";
        exc = assertThrows(AggregationException.class, () -> service.fetchAsMapList(fakeName));
        assertEquals("API Client " + fakeName + " not found", exc.getMessage());

        when(testHnApiClient.fetchToMap()).thenReturn(List.of(new LinkedHashMap<>()));

        List<?> list = assertInstanceOf(List.class, service.fetchAsMapList(API_NAME));
        assertInstanceOf(Map.class, list.getFirst());

        assertInstanceOf(String.class, ((Map<?, ?>) list.getFirst()).keySet().iterator().next());
        assertInstanceOf(String.class, ((Map<?, ?>) list.getFirst()).values().iterator().next());

        verify(testHnApiClient, times(1)).fetchToMap();
    }
}
