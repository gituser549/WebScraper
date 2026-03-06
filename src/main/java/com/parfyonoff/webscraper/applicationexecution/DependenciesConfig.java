package com.parfyonoff.webscraper.applicationexecution;

import com.parfyonoff.webscraper.aggregation.service.Service;
import com.parfyonoff.webscraper.file.printer.Printer;
import com.parfyonoff.webscraper.file.writer.flatwriter.FlatWriter;
import com.parfyonoff.webscraper.file.writer.structuredwriter.StructuredWriter;

import java.util.Map;

public record DependenciesConfig(
        Map<String, FlatWriter> flatWriters,
        Map<String, StructuredWriter> structuredWriters,
        Service service,
        Map<String, Printer> printers
) {}

