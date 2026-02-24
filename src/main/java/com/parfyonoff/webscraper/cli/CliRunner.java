package com.parfyonoff.webscraper.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.applicationrunner.ApplicationRunner;
import com.parfyonoff.webscraper.config.AggregationFieldsConfig;
import com.parfyonoff.webscraper.agregation.service.Service;
import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.Fetcher;
import com.parfyonoff.webscraper.config.APIClientsConfig;
import com.parfyonoff.webscraper.file.writer.CsvWriter;
import com.parfyonoff.webscraper.file.writer.JsonWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CliRunner {
    private Scanner scanner;
    private ApplicationRunner applicationRunner;

    public CliRunner() {
        scanner = new Scanner(System.in);
        applicationRunner = new ApplicationRunner();
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
        applicationRunner.run(apiNamesToScrap, fileName, rewrite, choiceToPrint);
    }

}
