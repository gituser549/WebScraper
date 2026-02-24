package com.parfyonoff.webscraper.config;

import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto.ExchangeScraper;
import com.parfyonoff.webscraper.apiclient.dto.hackernewsresponsedto.HackerNewsScraper;
import com.parfyonoff.webscraper.apiclient.dto.headhunterdto.HeadHunterScraper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum APIClientsConfig {
    EX("ex", fetcher -> new ExchangeScraper(fetcher)),
    HN("hn", fetcher -> new HackerNewsScraper(fetcher)),
    HH("hh", fetcher -> new HeadHunterScraper(fetcher)),;

    private final String apiClientName;
    private final APIClientFactory factory;

    APIClientsConfig(String apiClientName, APIClientFactory factory) {
        this.apiClientName = apiClientName;
        this.factory = factory;
    }

    public String getApiClientName() {
        return apiClientName;
    }

    public APIClientFactory getFactory() {
        return factory;
    }

    public static List<String> getApiClientsNames() {
        return Arrays.stream(values()).map(APIClientsConfig::getApiClientName).toList();
    }

    public static List<APIClient<?>> getApiClients(Fetcher fetcher) {
        return Arrays.stream(values()).map(val -> val.factory.create(fetcher)).collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface APIClientFactory {
        APIClient<?> create(Fetcher fetcher);
    }
}
