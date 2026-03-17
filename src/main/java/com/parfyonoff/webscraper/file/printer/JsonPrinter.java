package com.parfyonoff.webscraper.file.printer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.config.APIClientsConfig;
import com.parfyonoff.webscraper.config.AggregationFieldsConfig;
import com.parfyonoff.webscraper.file.FileAccessRegistry;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class JsonPrinter implements Printer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void printFile(File file, String choiceToPrint) {
        if (!file.exists() || file.length() == 0) {
            throw new PrinterException("file does not exist or is empty");
        }

        String fileName = file.getName();
        if (!fileName.endsWith(".json")) {
            throw new PrinterException("file must end with extension .json");
        }

        ReentrantLock fileLock = FileAccessRegistry.getFileLockFromRegistry(file);

        fileLock.lock();
        try {
            JsonNode root;
            if (choiceToPrint == null || choiceToPrint.isEmpty()) {
                throw new PrinterException("Invalid choice: " + choiceToPrint);
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
                throw new PrinterException("Invalid choice: " + choiceToPrint);
            }
        } catch (IOException exc) {
            throw new PrinterException("Reading file after writing exception: " + file.getName());
        }
        fileLock.unlock();
    }
}
