package com.parfyonoff.webscraper.applicationrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.agregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.config.APIClientsConfig;
import com.parfyonoff.webscraper.config.AggregationFieldsConfig;
import com.parfyonoff.webscraper.config.FlatFileFormatsConfig;
import com.parfyonoff.webscraper.config.StructuredFileFormatsConfig;
import com.parfyonoff.webscraper.file.printer.Printer;
import com.parfyonoff.webscraper.file.writer.flatwriter.FlatWriter;
import com.parfyonoff.webscraper.file.writer.structuredwriter.StructuredWriter;

import java.io.File;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApplicationRunner {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Fetcher fetcher;
    private final Service service;
    List<String> apiClientsNames;
    List<APIClient<?>> apiClients;
    Map<String, FlatWriter> flatWriters;
    Map<String, StructuredWriter>  structuredWriters;
    Map<String, Printer> printers;

    public ApplicationRunner() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();

        this.fetcher = new Fetcher(objectMapper, httpClient);

        apiClientsNames = APIClientsConfig.getApiClientsNames();
        apiClients = APIClientsConfig.getApiClients(fetcher);

        List<String> columnsNames = new ArrayList<>(AggregationFieldsConfig.getAggregationFieldsNames());

        apiClients.forEach(client -> columnsNames.addAll(client.getFlatColumns()));

        printers = new LinkedHashMap<>();

        flatWriters = new LinkedHashMap<>();
        FlatFileFormatsConfig.getFlatFileInfos().forEach(flatFileInfo ->  {
            flatWriters.put(flatFileInfo.fileFormatExtension(), flatFileInfo.flatWriterFactory().create(columnsNames));
            printers.put(flatFileInfo.fileFormatExtension(), flatFileInfo.printerFactory().getPrinter());
        });

        structuredWriters = new LinkedHashMap<>();
        StructuredFileFormatsConfig.getFileInfos().forEach(structuredFileInfo -> {
            structuredWriters.put(structuredFileInfo.fileFormatExtension(), structuredFileInfo.writersFactory().create());
            printers.put(structuredFileInfo.fileFormatExtension(), structuredFileInfo.printerFactory().getPrinter());
        });

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

        String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1);

        FlatWriter flatWriter;
        StructuredWriter structuredWriter;

        if (flatWriters.containsKey(fileExtension)) {
            flatWriter = flatWriters.get(fileExtension);

            for (String apiName : apiNamesList) {
                if (rewrite) {
                    flatWriter.write(file, service.fetchAsMapList(apiName));
                    rewrite = false;
                } else {
                    flatWriter.append(file, service.fetchAsMapList(apiName));
                }
            }
        } else if (structuredWriters.containsKey(fileExtension)) {
            structuredWriter = structuredWriters.get(fileExtension);

            for (String apiName : apiNamesList) {
                if (rewrite) {
                    structuredWriter.write(file, service.fetchAsAggregatedType(apiName));
                    rewrite = false;
                } else {
                    structuredWriter.append(file, service.fetchAsAggregatedType(apiName));
                }
            }

        } else {
            throw new ApplicationRunnerException("unknown fileExtension");
        }

        printers.get(fileExtension).printFile(file, choiceToPrint);
    }

}
