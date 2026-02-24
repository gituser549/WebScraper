package com.parfyonoff.webscraper.file.writer.structuredwriter;

@FunctionalInterface
public interface StructuredWritersFactory {
    StructuredWriter create();
}