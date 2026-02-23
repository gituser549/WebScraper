package com.parfyonoff.webscraper.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.agregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto.ExchangeItemsDto;
import com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto.ExchangeScraper;
import com.parfyonoff.webscraper.apiclient.dto.hackernewsresponsedto.HackerNewsScraper;
import com.parfyonoff.webscraper.apiclient.dto.headhunterdto.HeadHunterScraper;
import com.parfyonoff.webscraper.writer.CsvWriter;
import com.parfyonoff.webscraper.writer.JsonWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CliRunner {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Fetcher fetcher;
    private final Service service;
    List<String> apiClientsNames;
    List<APIClient<?>> apiClients;
    CsvWriter csvWriter;

    public CliRunner() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();

        this.fetcher = new Fetcher(objectMapper, httpClient);

        apiClientsNames = List.of("ex", "hn", "hh");
        apiClients = List.of(new ExchangeScraper(fetcher), new HackerNewsScraper(fetcher), new HeadHunterScraper(fetcher));

        List<String> columnsNames = new ArrayList<>(List.of("agg_id", "agg_source", "agg_timestamp"));

        apiClients.forEach(client -> columnsNames.addAll(client.getFlatColumns()));
        this.csvWriter = new CsvWriter(columnsNames);

       this.service = new Service(apiClientsNames, apiClients);
    }

    public void run(List<String> apiNamesList, String fileName, Boolean rewrite, String choiceToPrint) {
        if (fileName == null || fileName.isBlank()) {
            throw new CliException("fileName is null or blank");
        }

        File file = new File(fileName);

        if (!file.exists()) {
            throw new CliException("file does not exist");
        }

        for (String apiName : apiNamesList) {
            if (rewrite) {
                if (fileName.endsWith(".json")) {
                    JsonWriter.write(file, service.fetchAsAggregatedType(apiName));
                } else if (fileName.endsWith(".csv")) {
                    csvWriter.write(file, service.fetchAsMapList(apiName));
                } else {
                    throw new CliException("Invalid file name: " + fileName);
                }

                rewrite = false;
            } else {
                if (fileName.endsWith(".json")) {
                    JsonWriter.append(file, service.fetchAsAggregatedType(apiName));
                } else if (fileName.endsWith(".csv")) {
                    csvWriter.append(file, service.fetchAsMapList(apiName));
                } else {
                    throw new CliException("Invalid file name: " + fileName);
                }
            }
        }

        printFile(file, choiceToPrint);
    }

    public void printFile(File file, String choiceToPrint) {
        if (!file.exists() || file.length() == 0) {
            throw new CliException("file does not exist or is empty");
        }

        if (file.getName().endsWith(".json")) {
            try {
                JsonNode root;
                switch (choiceToPrint) {
                    case "all":
                        root = objectMapper.readTree(file);
                        for (JsonNode node : root) {
                            System.out.println(node.toPrettyString());
                        }
                        break;
                    case "ex":
                    case "hn":
                    case "hh":
                        root = objectMapper.readTree(file);
                        for (JsonNode node : root) {
                            if (node.path("source").asText().equals(choiceToPrint)) {
                                System.out.println(node.toPrettyString());
                            }
                        }
                        break;
                    default:
                        throw new CliException("Invalid choice: " + choiceToPrint);
                }
            } catch (IOException exc) {
                throw new CliException("Reading file after writing exception: " + file.getName());
            }
        } else if (file.getName().endsWith(".csv")) {
            try (BufferedReader reader = Files.newBufferedReader(Path.of(file.getAbsolutePath()))) {
                String header = reader.readLine();
                System.out.println(header);

                String line;
                if (choiceToPrint.equals("all")) {
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } else {
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");

                        String source = parts[1];

                        if (source.equals(choiceToPrint)) {
                            System.out.println(line);
                        }
                    }
                }
            } catch (IOException exc) {
                throw new CliException("Reading file after writing exception: " + file.getName());
            }

        }

    }


}
