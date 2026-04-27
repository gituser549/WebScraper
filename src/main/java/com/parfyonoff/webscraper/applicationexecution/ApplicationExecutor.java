package com.parfyonoff.webscraper.applicationexecution;

import com.parfyonoff.webscraper.aggregation.AggregatedData;
import com.parfyonoff.webscraper.aggregation.AggregationException;
import com.parfyonoff.webscraper.aggregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.file.FileCleaner;
import com.parfyonoff.webscraper.file.FileException;
import com.parfyonoff.webscraper.file.printer.Printer;
import com.parfyonoff.webscraper.file.writer.WriterException;
import com.parfyonoff.webscraper.file.writer.flatwriter.FlatWriter;
import com.parfyonoff.webscraper.file.writer.structuredwriter.StructuredWriter;
import com.parfyonoff.webscraper.threadmanagement.ThreadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApplicationExecutor {
    private final DependenciesConfig dependenciesConfig;
    private final ExecutionConfig executionConfig;
    private final ThreadManager threadManager;

    public ApplicationExecutor(DependenciesConfig dependenciesConfig, ExecutionConfig executionConfig, ThreadManager threadManager) {
        if (dependenciesConfig == null) {
            throw new ApplicationExecutorException("DependenciesConfig cannot be null");
        } else if (executionConfig == null) {
            throw new ApplicationExecutorException("ExecutionConfig cannot be null");
        } else if (threadManager == null) {
            throw new ApplicationExecutorException("ThreadManager cannot be null");
        }

        this.dependenciesConfig = dependenciesConfig;
        this.executionConfig = executionConfig;
        this.threadManager = threadManager;
    }

    public void run() {
        Map<String, FlatWriter> flatWriters = dependenciesConfig.flatWriters();
        Map<String, StructuredWriter> structuredWriters = dependenciesConfig.structuredWriters();
        Service service = dependenciesConfig.service();

        if (executionConfig.apiNamesList() == null || executionConfig.apiNamesList().isEmpty()) {
            throw new ApplicationExecutorException("Api names list is empty or even null");
        }

        List<String> apiNamesList = new ArrayList<>(executionConfig.apiNamesList());
        String fileName = executionConfig.fileName();

        if (fileName == null || fileName.isBlank()) {
            throw new ApplicationExecutorException("fileName is null or blank");
        }

        Boolean rewrite = executionConfig.rewrite();

        File file = new File(fileName);

        String fileExtension = fileName.substring(file.getName().lastIndexOf('.') + 1);

        FlatWriter flatWriter;
        StructuredWriter structuredWriter;

        SchedulingExecutionJob job;
        if (flatWriters.containsKey(fileExtension)) {
            flatWriter = flatWriters.get(fileExtension);

            job = (apiName) -> {
                List<Map<String, String>> fetchedData = service.fetchAsMapList(apiName);
                flatWriter.append(file, fetchedData);
            };
        } else if (structuredWriters.containsKey(fileExtension)) {
            structuredWriter = structuredWriters.get(fileExtension);

            job = (apiName) -> {
                AggregatedData fetchedData = service.fetchAsAggregatedType(apiName);
                structuredWriter.append(file, fetchedData);
            };
        } else {
            throw new ApplicationExecutorException("unknown fileExtension: " + fileExtension);
        }

        if (rewrite) {
            FileCleaner.clean(file);
        }

        apiNamesList.forEach(apiName -> threadManager.execute(RunnableWrapper.of(job, apiName)));
    }

    public void stop() {
        threadManager.stop();

        Map<String, Printer> printers = dependenciesConfig.printers();
        String fileName = executionConfig.fileName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

        if (!printers.containsKey(fileExtension)) {
            throw new ApplicationExecutorException("printer not found for extension " + fileExtension);
        }

        String choiceToPrint = executionConfig.choiceToPrint();

        if (choiceToPrint == null || choiceToPrint.isBlank()) {
            throw new ApplicationExecutorException("choiceToPrint is null or blank");
        }

        File file = new File(fileName);
        printers.get(fileExtension).printFile(file, choiceToPrint);
    }

    public static class RunnableWrapper {
        public static Runnable of(SchedulingExecutionJob job, String apiName) {
            return () -> {
                try {
                    job.run(apiName);
                } catch (AggregationException | APIClientException | FileException | WriterException exc) {
                    System.out.println(exc.getMessage());
                    throw exc;
                } catch (RuntimeException exc) {
                    System.out.println("Unexpected runtime exception gotten: " + exc.getMessage());
                    throw exc;
                }
            };
        }
    }

    @FunctionalInterface
    public interface SchedulingExecutionJob {
        void run(String apiName);
    }
}