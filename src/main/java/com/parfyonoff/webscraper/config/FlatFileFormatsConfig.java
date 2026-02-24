package com.parfyonoff.webscraper.config;

import com.parfyonoff.webscraper.file.printer.CsvPrinter;
import com.parfyonoff.webscraper.file.printer.PrinterFactory;
import com.parfyonoff.webscraper.file.writer.flatwriter.CsvWriter;
import com.parfyonoff.webscraper.file.writer.flatwriter.FlatWritersFactory;

import java.util.Arrays;
import java.util.List;


public enum FlatFileFormatsConfig {
    CSV("csv", columnsNames -> new CsvWriter(columnsNames), () -> new CsvPrinter());

    private final FlatFileInfo flatFileInfo;

    FlatFileFormatsConfig(String fileFormatExtension, FlatWritersFactory flatWritersFactory, PrinterFactory printerFactory) {
        flatFileInfo = new FlatFileInfo(fileFormatExtension, flatWritersFactory, printerFactory);
    }

    public String getFileFormatExtension() {
        return flatFileInfo.fileFormatExtension;
    }

    public static List<String> getFileFormatsExtensions() {
        return Arrays.stream(values()).map(FlatFileFormatsConfig::getFileFormatExtension).toList();
    }

    public static List<String> getAggregationFieldsNames() {
        return Arrays.stream(values()).map(FlatFileFormatsConfig::getFileFormatExtension).toList();
    }

    public FlatWritersFactory getFlatFilesWritersFactory() {
        return flatFileInfo.flatWriterFactory;
    }

    public FlatFileInfo getFlatFileInfo() {
        return flatFileInfo;
    }

    public static List<FlatFileInfo> getFlatFileInfos() {
        return Arrays.stream(values()).map(FlatFileFormatsConfig::getFlatFileInfo).toList();
    }

    public static List<FlatWritersFactory> getFlatFilesWritersFactories() {
        return Arrays.stream(FlatFileFormatsConfig.values()).map(FlatFileFormatsConfig::getFlatFilesWritersFactory).toList();
    }

    public record FlatFileInfo (
        String fileFormatExtension,
        FlatWritersFactory flatWriterFactory,
        PrinterFactory printerFactory
    ) {}
}
