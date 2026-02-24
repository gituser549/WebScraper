package com.parfyonoff.webscraper.config;

import com.parfyonoff.webscraper.file.printer.JsonPrinter;
import com.parfyonoff.webscraper.file.printer.PrinterFactory;
import com.parfyonoff.webscraper.file.writer.structuredwriter.JsonWriter;
import com.parfyonoff.webscraper.file.writer.structuredwriter.StructuredWritersFactory;

import java.util.Arrays;
import java.util.List;

public enum StructuredFileFormatsConfig {
    JSON("json", JsonWriter::new, JsonPrinter::new);

    private final StructuredFileInfo fileInfo;

    StructuredFileFormatsConfig(String fileFormatExtension, StructuredWritersFactory structuredFilesWritersFactory, PrinterFactory printerFactory) {
         fileInfo = new StructuredFileInfo(fileFormatExtension, structuredFilesWritersFactory, printerFactory);
    }

    public StructuredWritersFactory getStructuredFilesWritersFactory() {
        return fileInfo.writersFactory;
    }

    public static List<StructuredWritersFactory> getStructuredFilesWritersFactories() {
        return Arrays.stream(StructuredFileFormatsConfig.values()).map(StructuredFileFormatsConfig::getStructuredFilesWritersFactory).toList();
    }

    public StructuredFileInfo getFileInfo() {
        return fileInfo;
    }

    public static List<StructuredFileInfo> getFileInfos() {
        return Arrays.stream(values()).map(StructuredFileFormatsConfig::getFileInfo).toList();
    }

    public record StructuredFileInfo (
            String fileFormatExtension,
            StructuredWritersFactory writersFactory,
            PrinterFactory printerFactory
    ) {}
}
