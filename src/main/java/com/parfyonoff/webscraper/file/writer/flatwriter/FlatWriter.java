package com.parfyonoff.webscraper.file.writer.flatwriter;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface FlatWriter {
    void write(File file, List<Map<String, String>> aggregatedData);
    void append(File file, List<Map<String, String>> aggregatedData);
}
