package com.parfyonoff.webscraper.applicationcomposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.aggregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutor;
import com.parfyonoff.webscraper.applicationexecution.DependenciesConfig;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.config.APIClientsConfig;
import com.parfyonoff.webscraper.config.AggregationFieldsConfig;
import com.parfyonoff.webscraper.config.FlatFileFormatsConfig;
import com.parfyonoff.webscraper.config.StructuredFileFormatsConfig;
import com.parfyonoff.webscraper.file.printer.Printer;
import com.parfyonoff.webscraper.file.writer.flatwriter.FlatWriter;
import com.parfyonoff.webscraper.file.writer.structuredwriter.StructuredWriter;
import com.parfyonoff.webscraper.threadmanagement.MultiThreadingConfig;
import com.parfyonoff.webscraper.threadmanagement.ThreadManager;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApplicationCompositor {
    private final Service service;
    private final Map<String, FlatWriter> flatWriters;
    private final Map<String, StructuredWriter>  structuredWriters;
    private final Map<String, Printer> printers;

    public ApplicationCompositor() {
        List<String> apiClientsNames = APIClientsConfig.getApiClientsNames();
        List<APIClient<?>> apiClients = APIClientsConfig.getApiClients(new Fetcher(new ObjectMapper(), HttpClient.newHttpClient()));

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


    public ApplicationExecutor build(ExecutionConfig executionConfig, MultiThreadingConfig multiThreadingConfig) {
        return new ApplicationExecutor(new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                executionConfig,
                new ThreadManager(multiThreadingConfig)
            );
    }

    public ApplicationExecutor build(ExecutionConfig executionConfig) {
        return new ApplicationExecutor(new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                executionConfig,
                new ThreadManager()
        );
    }
}
