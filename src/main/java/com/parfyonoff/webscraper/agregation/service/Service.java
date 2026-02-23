package com.parfyonoff.webscraper.agregation.service;

import com.parfyonoff.webscraper.agregation.AggregatedData;
import com.parfyonoff.webscraper.agregation.AggregationException;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.writer.CsvWriter;

import java.time.Instant;
import java.util.*;

public class Service {
    private final Map<String, APIClient<?>> apiClientsMap = new HashMap<>();

    public Service(List<String> apiClientsNames, List<APIClient<?>> apiClients) {
        if (apiClientsNames == null || apiClientsNames.isEmpty()) {
            throw new AggregationException("API Clients names list is null or empty");
        } else if (apiClients == null || apiClients.isEmpty()) {
            throw new AggregationException("API Clients list is null or empty");
        } else if (apiClients.size() != apiClientsNames.size()) {
            throw new AggregationException("API Clients list size is not equal to API Clients list size");
        }

        for (int i = 0; i < apiClientsNames.size(); i++) {
            apiClientsMap.put(apiClientsNames.get(i), apiClients.get(i));
        }
    }

    public AggregatedData fetchAsAggregatedType(String apiClientName) {
        if (apiClientName == null || apiClientName.isBlank()) {
            throw new AggregationException("API Clients name is null or empty");
        }

        APIClient<?> apiClient = apiClientsMap.get(apiClientName);
        if (apiClient == null) {
            throw new AggregationException("API Client not found: " + apiClientName);
        }

        return new AggregatedData(UUID.randomUUID(), apiClientName,  Instant.now().toString(), apiClient.fetchToDTO());
    }

    public List<Map<String, String>> fetchAsMapList(String apiClientName) {
        if (apiClientName == null || apiClientName.isBlank()) {
            throw new AggregationException("API Client name is null or empty");
        } else if (!apiClientsMap.containsKey(apiClientName)) {
            throw new AggregationException("API Client " + apiClientName + " not found");
        }

        List<Map<String, String>> forCSVData = apiClientsMap.get(apiClientName).fetchToMap();
        List<Map<String, String>> aggregatedData = new ArrayList<>();

        String timestamp = Instant.now().toString();
        for (Map<String, String> map : forCSVData) {
            Map<String, String> aggregatedDataItem = new LinkedHashMap<>();
            aggregatedDataItem.put("agg_id", UUID.randomUUID().toString());
            aggregatedDataItem.put("agg_source", apiClientName);
            aggregatedDataItem.put("agg_timestamp", timestamp);
            aggregatedDataItem.putAll(map);
            aggregatedData.add(aggregatedDataItem);
        }

        return aggregatedData;
    }
}
