package com.parfyonoff.webscraper.file.printer;

import com.parfyonoff.webscraper.config.AggregationFieldsConfig;
import com.parfyonoff.webscraper.file.FileAccessRegistry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class CsvPrinter implements Printer {
    public void printFile(File file, String choiceToPrint) {
        if (!file.exists() || file.length() == 0) {
            throw new PrinterException("file does not exist or is empty");
        }

        String fileName = file.getName();

        if (!fileName.endsWith(".csv")) {
            throw new PrinterException("file must end with extension .csv");
        }

        Object lock = FileAccessRegistry.getFileLockFromRegistry(file);

        synchronized (lock) {
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
                throw new PrinterException("Reading file after writing exception: " + file.getName());
            }
        }
    }
}


