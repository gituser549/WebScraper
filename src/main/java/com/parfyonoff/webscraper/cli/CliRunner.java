package com.parfyonoff.webscraper.cli;

import com.parfyonoff.webscraper.applicationbuilding.ApplicationBuilder;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutor;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.config.APIClientsConfig;
import com.parfyonoff.webscraper.threadmanagement.MultiThreadingConfig;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CliRunner {
    private final Scanner scanner;
    private final ApplicationBuilder applicationBuilder;

    public CliRunner(Scanner scanner, ApplicationBuilder applicationBuilder) {
        if (scanner == null || applicationBuilder == null) {
            throw new CliException("Scanner or ApplicationBuilder is null");
        }

        this.scanner = scanner;
        this.applicationBuilder = applicationBuilder;
    }

    public void start() {
        System.out.println("Welcome to Parfyonoff Webscraper!");
        System.out.println("Please enter your choice of api to scrap (allowed: " + APIClientsConfig.getApiClientsNames() + "):");

        List<String> apiNamesToScrap = Arrays.stream(scanner.nextLine().split(" ")).toList();
        System.out.println("Please enter filename (must ends with .json or .csv):");
        String fileName = scanner.nextLine();
        System.out.println("Please enter you want to rewrite file (yes), append to file (no):");
        Boolean rewrite = scanner.nextLine().equals("yes");
        System.out.println("Please choose do you want to print to console all info in file (all), or for exact api (allowed: " + APIClientsConfig.getApiClientsNames() + "):");
        String choiceToPrint = scanner.nextLine();
        System.out.println("Please enter your maximum number of tasks can be taken at the exact moment:");
        int maxTasks;
        try {
            maxTasks = scanner.nextInt();
        } catch (InputMismatchException exc) {
            scanner.nextLine();
            throw new CliException("Input mismatch exception while reading maxTasks: " + exc.getMessage());
        }
        System.out.println("Please enter the interval for api polling:");
        int interval;
        try {
            interval = scanner.nextInt();
        } catch (InputMismatchException exc) {
            scanner.nextLine();
            throw new CliException("Input mismatch exception while reading polling interval: " + exc.getMessage());
        }

        ApplicationExecutor applicationExecutor = applicationBuilder.build(
                new ExecutionConfig(
                        apiNamesToScrap, fileName, rewrite, choiceToPrint
                ),
                new MultiThreadingConfig(maxTasks, interval)
        );

        runAppAndAwaitForStop(applicationExecutor);
    }

    public void runAppAndAwaitForStop(ApplicationExecutor applicationExecutor) {
        if (applicationExecutor == null) {
            throw new CliException("Application Executor cant be null");
        }

        applicationExecutor.run();

        System.out.println("To stop polling api and get the results you should text \"stop\" and press <Enter>");

        while (true) {
            String command = scanner.nextLine();
            if (command.equals("stop")) {
                break;
            }
        }

        applicationExecutor.stop();
    }
}
