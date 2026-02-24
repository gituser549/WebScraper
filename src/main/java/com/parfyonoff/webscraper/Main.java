package com.parfyonoff.webscraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.agregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto.ExchangeResponseDto;
import com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto.ExchangeScraper;
import com.parfyonoff.webscraper.apiclient.dto.hackernewsresponsedto.HackerNewsScraper;
import com.parfyonoff.webscraper.apiclient.dto.headhunterdto.HeadHunterScraper;
import com.parfyonoff.webscraper.cli.CliException;
import com.parfyonoff.webscraper.cli.CliRunner;
import com.parfyonoff.webscraper.writer.JsonWriter;

import java.io.File;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        if (args.length >= 4) {
            String fileName;
            List<String> apiNames = new ArrayList<>();

            if (!args[0].equals("--api")) {
                System.out.println("Invalid key when --api needed");
                return;
            } else if (!args[args.length - 2].equals("--file")) {
                System.out.println("Invalid key when --file needed");
            }

            for (int i = 1; i < args.length - 2; i++) {
                apiNames.add(args[i]);
            }

            fileName = args[args.length - 1];

            CliRunner cliRunner = new CliRunner();
            cliRunner.run(apiNames, fileName, true, "all");
        }
        //CliRunner cliRunner = new CliRunner();
        //cliRunner.run(List.of("hn", "ex", "hh"), "/Users/mac/OOP_Labs/WebScraper/src/test.json", true, "all");

        /*
        Fetcher fetcher = new Fetcher(new ObjectMapper(), HttpClient.newHttpClient());
        ExchangeScraper exchangeScraper = new ExchangeScraper(fetcher);


        ExchangeResponseDto exchangeResponseDto = exchangeScraper.fetchToDTO();

        List<APIClient<?>> apiClientsList = List.of(new HackerNewsScraper(fetcher), new ExchangeScraper(fetcher), new HeadHunterScraper(fetcher));
        Service service = new Service(List.of("HackerNews", "Exchange", "HeadHunter"), apiClientsList);

        JsonWriter.append(new File("/Users/mac/OOP_Labs/WebScraper/src/test.json"), service.fetchAsAggregatedType("HeadHunter"));

        HeadHunterScraper headHunterScraper = new HeadHunterScraper(fetcher);

        HeadHunterResponseDto headHunterResponseDto = headHunterScraper.fetchToDTO();
        AggregatedData aggregatedData = new AggregatedData((int) (Math.random() * 10), "HeadHunter",  Instant.now().toString(), headHunterResponseDto);


        JsonWriter.append(new File("/Users/mac/OOP_Labs/WebScraper/src/test.json"), aggregatedData);
        CsvWriter.append(new File("/Users/mac/OOP_Labs/WebScraper/src/test.csv"), headHunterScraper.fetchToMap());
    */
    }
}