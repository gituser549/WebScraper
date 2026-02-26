package com.parfyonoff.webscraper.applicationrunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.aggregation.service.Service;
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
    private final Service service;
    List<String> apiClientsNames;
    List<APIClient<?>> apiClients;
    Map<String, FlatWriter> flatWriters;
    Map<String, StructuredWriter>  structuredWriters;
    Map<String, Printer> printers;

    public ApplicationRunner() {
        apiClientsNames = APIClientsConfig.getApiClientsNames();
        apiClients = APIClientsConfig.getApiClients(new Fetcher(new ObjectMapper(), HttpClient.newHttpClient()));

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
        if (apiNamesList == null || apiNamesList.isEmpty()) {
            throw new ApplicationRunnerException("Api names list is empty or even null");
        } else if (fileName == null || fileName.isBlank()) {
            throw new ApplicationRunnerException("fileName is null or blank");
        } else if (choiceToPrint == null || choiceToPrint.isBlank()) {
            throw new ApplicationRunnerException("choiceToPrint is null or blank");
        }

        File file = new File(fileName);

        String fileExtension = fileName.substring(file.getName().lastIndexOf('.') + 1);

        FlatWriter flatWriter;
        StructuredWriter structuredWriter;

        if (flatWriters.containsKey(fileExtension)) {
            flatWriter = flatWriters.get(fileExtension);

            if (rewrite) {
                flatWriter.write(file, service.fetchAsMapList(apiNamesList.getFirst()));
                apiNamesList =  apiNamesList.subList(1, apiNamesList.size());
            }

            for (String apiName : apiNamesList) {
                flatWriter.append(file, service.fetchAsMapList(apiName));
            }
        } else if (structuredWriters.containsKey(fileExtension)) {
            structuredWriter = structuredWriters.get(fileExtension);

            if (rewrite) {
                structuredWriter.write(file, service.fetchAsAggregatedType(apiNamesList.getFirst()));
                apiNamesList =  apiNamesList.subList(1, apiNamesList.size());
            }

            for (String apiName : apiNamesList) {
                structuredWriter.append(file, service.fetchAsAggregatedType(apiName));
            }
        } else {
            throw new ApplicationRunnerException("unknown fileExtension");
        }

        if (!printers.containsKey(fileExtension)) {
            throw new ApplicationRunnerException("printer not found for extension " + fileExtension);
        }
        printers.get(fileExtension).printFile(file, choiceToPrint);
    }

}
