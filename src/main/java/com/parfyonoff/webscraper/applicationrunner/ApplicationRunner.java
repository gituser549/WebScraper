package com.parfyonoff.webscraper.applicationrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.agregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.config.APIClientsConfig;
import com.parfyonoff.webscraper.config.AggregationFieldsConfig;
import com.parfyonoff.webscraper.file.FilePrinter;
import com.parfyonoff.webscraper.file.writer.CsvWriter;
import com.parfyonoff.webscraper.file.writer.JsonWriter;

import java.io.File;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

public class ApplicationRunner {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Fetcher fetcher;
    private final Service service;
    List<String> apiClientsNames;
    List<APIClient<?>> apiClients;
    CsvWriter csvWriter;

    public ApplicationRunner() {
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
            throw new ApplicationRunnerException("fileName is null or blank");
        }

        File file = new File(fileName);

        if (!file.exists()) {
            throw new ApplicationRunnerException("file does not exist");
        }

        for (String apiName : apiNamesList) {
            if (rewrite) {
                if (fileName.endsWith(".json")) {
                    JsonWriter.write(file, service.fetchAsAggregatedType(apiName));
                } else if (fileName.endsWith(".csv")) {
                    csvWriter.write(file, service.fetchAsMapList(apiName));
                } else {
                    throw new ApplicationRunnerException("Invalid file name: " + fileName);
                }

                rewrite = false;
            } else {
                if (fileName.endsWith(".json")) {
                    JsonWriter.append(file, service.fetchAsAggregatedType(apiName));
                } else if (fileName.endsWith(".csv")) {
                    csvWriter.append(file, service.fetchAsMapList(apiName));
                } else {
                    throw new ApplicationRunnerException("Invalid file name: " + fileName);
                }
            }
        }

        FilePrinter.printFile(objectMapper, file, choiceToPrint);
    }

}
