package com.parfyonoff.webscraper.file.printer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
        } else if (choiceToPrint == null || choiceToPrint.isBlank()) {
            throw new PrinterException("choiceToPrint is null or blank");
        }

        String fileName = file.getName();
        if (!fileName.endsWith(".json")) {
            throw new PrinterException("file must end with extension .json");
        }

        ReentrantLock fileLock = FileAccessRegistry.getFileLockFromRegistry(file);

        fileLock.lock();
        try {
            try {
                JsonNode root;
                if (choiceToPrint.equals("all")) {
                    root = objectMapper.readTree(file);
                    System.out.println(root.toString());
                } else {
                    root = objectMapper.readTree(file);

                    ArrayNode arrayNode = objectMapper.createArrayNode();
                    for (JsonNode node : root) {
                        if (node.path(AggregationFieldsConfig.AGG_SOURCE.getAggregationFieldName()).asText().equals(choiceToPrint)) {
                            arrayNode.add(node);
                        }
                    }

                    System.out.println(arrayNode.toPrettyString());
                }
            } catch (IOException exc) {
                throw new PrinterException("Reading file after writing exception: " + file.getName());
            }
        } finally {
            fileLock.unlock();
        }
    }
}
