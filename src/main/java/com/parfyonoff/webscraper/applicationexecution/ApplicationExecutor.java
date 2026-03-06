package com.parfyonoff.webscraper.applicationexecution;

import com.parfyonoff.webscraper.aggregation.service.Service;
import com.parfyonoff.webscraper.file.printer.Printer;
import com.parfyonoff.webscraper.file.writer.flatwriter.FlatWriter;
import com.parfyonoff.webscraper.file.writer.structuredwriter.StructuredWriter;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ApplicationExecutor {
    private final DependenciesConfig dependenciesConfig;

    public ApplicationExecutor(DependenciesConfig dependenciesConfig) {
        this.dependenciesConfig = dependenciesConfig;
    }

    public void run(ExecutionConfig executionConfig) {
        Map<String, FlatWriter> flatWriters = dependenciesConfig.flatWriters();
        Map<String, StructuredWriter> structuredWriters = dependenciesConfig.structuredWriters();
        Service service = dependenciesConfig.service();
        Map<String, Printer> printers = dependenciesConfig.printers();

        List<String> apiNamesList = executionConfig.apiNamesList();
        String fileName = executionConfig.fileName();
        Boolean rewrite = executionConfig.rewrite();
        String choiceToPrint = executionConfig.choiceToPrint();

        if (apiNamesList == null || apiNamesList.isEmpty()) {
            throw new ApplicationExecutorException("Api names list is empty or even null");
        } else if (fileName == null || fileName.isBlank()) {
            throw new ApplicationExecutorException("fileName is null or blank");
        } else if (choiceToPrint == null || choiceToPrint.isBlank()) {
            throw new ApplicationExecutorException("choiceToPrint is null or blank");
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
            throw new ApplicationExecutorException("unknown fileExtension");
        }

        if (!printers.containsKey(fileExtension)) {
            throw new ApplicationExecutorException("printer not found for extension " + fileExtension);
        }
        printers.get(fileExtension).printFile(file, choiceToPrint);
    }
}



