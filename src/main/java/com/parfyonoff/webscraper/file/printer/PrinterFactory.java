package com.parfyonoff.webscraper.file.printer;

@FunctionalInterface
public interface PrinterFactory {
    Printer getPrinter();
}
