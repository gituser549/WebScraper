package com.parfyonoff.webscraper.file.writer.structuredwriter;

import com.parfyonoff.webscraper.aggregation.AggregatedData;

import java.io.File;
import java.util.List;

public interface StructuredWriter {
    void write(File file, AggregatedData aggregatedData);
    void append(File file, AggregatedData aggregatedData);
}
