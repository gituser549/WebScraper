package com.parfyonoff.webscraper.file.writer.structuredwriter;

import com.parfyonoff.webscraper.agregation.AggregatedData;

import java.io.File;

public interface StructuredWriter {
    void write(File file, AggregatedData aggregatedData);
    void append(File file, AggregatedData aggregatedData);
}
