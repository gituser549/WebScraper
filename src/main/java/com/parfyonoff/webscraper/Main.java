package com.parfyonoff.webscraper;

import com.parfyonoff.webscraper.applicationbuilding.ApplicationBuilder;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutor;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutorException;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.cli.CliException;
import com.parfyonoff.webscraper.cli.CliRunner;
import com.parfyonoff.webscraper.file.FileException;
import com.parfyonoff.webscraper.file.printer.PrinterException;
import com.parfyonoff.webscraper.threadmanagement.MultiThreadingConfig;
import com.parfyonoff.webscraper.threadmanagement.ThreadManagementException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            CliRunner cliRunner = new CliRunner();

            if (args.length >= 4) {
                if (!args[0].equals("--api")) {
                    System.out.println("Invalid key when --api needed");
                    return;
                } else if (!args[args.length - 6].equals("--file")) {
                    System.out.println("Invalid key when --file needed");
                    return;
                } else if (!args[args.length - 4].equals("--n")) {
                    System.out.println("Invalid key when --n needed");
                    return;
                } else if (!args[args.length - 2].equals("--t")) {
                    System.out.println("Invalid key when --t needed");
                    return;
                }

                List<String> apiNames = new ArrayList<>(Arrays.asList(args).subList(1, args.length - 6));
                String fileName = args[args.length - 5];
                Integer maxTasks = Integer.parseInt(args[args.length - 3]);
                Integer interval = Integer.parseInt(args[args.length - 1]);

                ApplicationExecutor applicationExecutor = new ApplicationBuilder().
                        build(new ExecutionConfig(
                                        apiNames,
                                        fileName,
                                        true,
                                        "all"
                                ),
                                new MultiThreadingConfig(
                                        maxTasks,
                                        interval
                                )
                        );

                cliRunner.runAppAndAwaitForStop(applicationExecutor);
            } else {
                cliRunner.start();
            }
        } catch (ThreadManagementException exc) {
            System.out.println("Thread management exception: " + exc.getMessage());
        } catch (CliException exc) {
            System.out.println("CLI exception: " + exc.getMessage());
        } catch (ApplicationExecutorException exc) {
            System.out.println("Application Executor exception: " + exc.getMessage());
        } catch (FileException exc) {
            System.out.println("File exception: " + exc.getMessage());
        } catch (PrinterException exc) {
            System.out.println("Printer exception: " + exc.getMessage());
        }
    }
}