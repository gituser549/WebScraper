package com.parfyonoff.webscraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.agregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto.ExchangeResponseDto;
import com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto.ExchangeScraper;
import com.parfyonoff.webscraper.apiclient.dto.hackernewsresponsedto.HackerNewsScraper;
import com.parfyonoff.webscraper.apiclient.dto.headhunterdto.HeadHunterScraper;
import com.parfyonoff.webscraper.writer.JsonWriter;

import java.io.File;
import java.net.http.HttpClient;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Fetcher fetcher = new Fetcher(new ObjectMapper(), HttpClient.newHttpClient());
        ExchangeScraper exchangeScraper = new ExchangeScraper(fetcher);


        ExchangeResponseDto exchangeResponseDto = exchangeScraper.fetchToDTO();

        List<APIClient<?>> apiClientsList = List.of(new HackerNewsScraper(fetcher), new ExchangeScraper(fetcher), new HeadHunterScraper(fetcher));
        Service service = new Service(List.of("HackerNews", "Exchange", "HeadHunter"), apiClientsList);

        JsonWriter.append(new File("/Users/mac/OOP_Labs/WebScraper/src/test.json"), service.fetchAsAggregatedType("HeadHunter"));
                /*
        HeadHunterScraper headHunterScraper = new HeadHunterScraper(fetcher);

        HeadHunterResponseDto headHunterResponseDto = headHunterScraper.fetchToDTO();
        AggregatedData aggregatedData = new AggregatedData((int) (Math.random() * 10), "HeadHunter",  Instant.now().toString(), headHunterResponseDto);


        JsonWriter.append(new File("/Users/mac/OOP_Labs/WebScraper/src/test.json"), aggregatedData);
        CsvWriter.append(new File("/Users/mac/OOP_Labs/WebScraper/src/test.csv"), headHunterScraper.fetchToMap());
    */
    }
}