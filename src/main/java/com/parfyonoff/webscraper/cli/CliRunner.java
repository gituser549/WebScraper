package com.parfyonoff.webscraper.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.config.AggregationFieldsConfig;
import com.parfyonoff.webscraper.agregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.config.APIClientsConfig;
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

        apiClientsNames = APIClientsConfig.getApiClientsNames();
        apiClients = APIClientsConfig.getApiClients(fetcher);

        List<String> columnsNames = new ArrayList<>(AggregationFieldsConfig.getAggregationFieldsNames());

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
                if (choiceToPrint == null || choiceToPrint.isEmpty()) {
                    throw new CliException("Invalid choice: " + choiceToPrint);
                } else if (choiceToPrint.equals("all")) {
                    root = objectMapper.readTree(file);
                    for (JsonNode node : root) {
                        System.out.println(node.toPrettyString());
                    }
                } else if (APIClientsConfig.getApiClientsNames().contains(choiceToPrint)) {
                    root = objectMapper.readTree(file);

                    for (JsonNode node : root) {
                        if (node.path(AggregationFieldsConfig.AGG_SOURCE.getAggregationFieldName()).asText().equals(choiceToPrint)) {
                            System.out.println(node.toPrettyString());
                        }
                    }
                } else {
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
