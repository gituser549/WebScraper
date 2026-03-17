package com.parfyonoff.webscraper.cli;

import com.parfyonoff.webscraper.applicationbuilding.ApplicationBuilder;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutor;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.config.APIClientsConfig;
import com.parfyonoff.webscraper.threadmanagement.MultiThreadingConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CliRunner {
    private final Scanner scanner;

    public CliRunner() {
        scanner = new Scanner(System.in);
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
        Integer maxTasks = scanner.nextInt();
        System.out.println("Please enter the interval for api polling:");
        Integer interval = scanner.nextInt();

        ApplicationExecutor applicationExecutor = new ApplicationBuilder().build(
                new ExecutionConfig(
                        apiNamesToScrap, fileName, rewrite, choiceToPrint
                ),
                new MultiThreadingConfig(maxTasks, interval)
        );

        runAppAndAwaitForStop(applicationExecutor);
    }

    public void runAppAndAwaitForStop(ApplicationExecutor applicationExecutor) {
        if (applicationExecutor == null) {
            throw new CliException("Application Executor could not be null.");
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
