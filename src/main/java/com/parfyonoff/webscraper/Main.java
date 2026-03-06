package com.parfyonoff.webscraper;

import com.parfyonoff.webscraper.aggregation.AggregationException;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.applicationcomposition.ApplicationCompositor;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutorException;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.cli.CliRunner;
import com.parfyonoff.webscraper.file.printer.PrinterException;
import com.parfyonoff.webscraper.file.writer.WriterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length >= 4) {
                String fileName;

                if (!args[0].equals("--api")) {
                    System.out.println("Invalid key when --api needed");
                    return;
                } else if (!args[args.length - 2].equals("--file")) {
                    System.out.println("Invalid key when --file needed");
                    return;
                }

                List<String> apiNames = new ArrayList<>(Arrays.asList(args).subList(1, args.length - 2));

                fileName = args[args.length - 1];

                new ApplicationCompositor().
                        build().
                        run(new ExecutionConfig(apiNames, fileName, true, "all"));
            } else {
                CliRunner cliRunner = new CliRunner();
                cliRunner.start();
            }
        } catch (ApplicationExecutorException exc) {
            System.out.println("Application Executor exception: " + exc.getMessage());
        } catch (APIClientException exc) {
            System.out.println("API Client exception: " + exc.getMessage());
        } catch (AggregationException exc) {
            System.out.println("Aggregation exception: " + exc.getMessage());
        } catch (WriterException exc) {
            System.out.println("Writer exception: " + exc.getMessage());
        } catch (PrinterException exc) {
            System.out.println("Printer exception: " + exc.getMessage());
        }
    }
}