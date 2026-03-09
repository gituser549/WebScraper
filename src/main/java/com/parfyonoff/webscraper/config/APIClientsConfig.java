package com.parfyonoff.webscraper.config;

import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.stackexchange.ExchangeScraper;
import com.parfyonoff.webscraper.apiclient.dto.hackernews.HackerNewsScraper;
import com.parfyonoff.webscraper.apiclient.dto.headhunter.HeadHunterScraper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum APIClientsConfig {
    EX("ex", ExchangeScraper::new),
    HN("hn", HackerNewsScraper::new),
    HH("hh", HeadHunterScraper::new);

    private final String apiClientName;
    private final APIClientFactory factory;

    APIClientsConfig(String apiClientName, APIClientFactory factory) {
        this.apiClientName = apiClientName;
        this.factory = factory;
    }

    public String getApiClientName() {
        return apiClientName;
    }

    public static List<String> getApiClientsNames() {
        return Arrays.stream(values()).map(APIClientsConfig::getApiClientName).toList();
    }

    public static List<APIClient<?>> getApiClients(Fetcher fetcher) {
        return Arrays.stream(values()).map(val -> val.factory.create(fetcher)).collect(Collectors.toList());
    }

    public static Integer getNumOfApiClients() {
        return getApiClientsNames().size();
    }

    @FunctionalInterface
    public interface APIClientFactory {
        APIClient<?> create(Fetcher fetcher);
    }
}
