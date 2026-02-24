package com.parfyonoff.webscraper.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.config.APIClientsConfig;
import com.parfyonoff.webscraper.config.AggregationFieldsConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FilePrinter {
    public static void printFile(ObjectMapper objectMapper, File file, String choiceToPrint) {
        if (!file.exists() || file.length() == 0) {
            throw new FileException("file does not exist or is empty");
        }

        if (file.getName().endsWith(".json")) {
            try {
                JsonNode root;
                if (choiceToPrint == null || choiceToPrint.isEmpty()) {
                    throw new FileException("Invalid choice: " + choiceToPrint);
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
                    throw new FileException("Invalid choice: " + choiceToPrint);
                }
            } catch (IOException exc) {
                throw new FileException("Reading file after writing exception: " + file.getName());
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
                    int apiNameIndex = Arrays.stream(header.split(",")).toList().indexOf(AggregationFieldsConfig.AGG_SOURCE.getAggregationFieldName());

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");

                        String source = parts[apiNameIndex];

                        if (source.equals(choiceToPrint)) {
                            System.out.println(line);
                        }
                    }
                }
            } catch (IOException exc) {
                throw new FileException("Reading file after writing exception: " + file.getName());
            }

        }
    }
}
