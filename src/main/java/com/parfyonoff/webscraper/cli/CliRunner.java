package com.parfyonoff.webscraper.cli;

import com.parfyonoff.webscraper.applicationcomposition.ApplicationCompositor;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.config.APIClientsConfig;

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

        new ApplicationCompositor().build().run(new ExecutionConfig(apiNamesToScrap, fileName, rewrite, choiceToPrint));
    }

}
