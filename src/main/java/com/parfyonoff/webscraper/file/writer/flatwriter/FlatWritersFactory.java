package com.parfyonoff.webscraper.file.writer.flatwriter;

import java.util.List;

@FunctionalInterface
public interface FlatWritersFactory {
    FlatWriter create(List<String> columnsNames);
}
