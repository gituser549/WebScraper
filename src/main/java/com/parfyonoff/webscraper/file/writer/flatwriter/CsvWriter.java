package com.parfyonoff.webscraper.file.writer.flatwriter;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.parfyonoff.webscraper.file.FileAccessRegistry;
import com.parfyonoff.webscraper.file.writer.WriterException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class CsvWriter implements FlatWriter {
    private final static CsvMapper csvMapper = new CsvMapper();
    private final CsvSchema csvSchema;

    public CsvWriter(List<String> columnsNames) {
        if (columnsNames == null || columnsNames.isEmpty()) {
            throw new WriterException("columns cannot be null or empty");
        }

        CsvSchema.Builder builder = CsvSchema.builder();
        for (String columnName : columnsNames) {
            builder.addColumn(columnName);
        }

        csvSchema = builder.setUseHeader(true).build();
    }

    public void write(File file, List<Map<String, String>> aggregatedData) {
        if (aggregatedData.isEmpty()) {
            return;
        }

        ReentrantLock fileLock = FileAccessRegistry.getFileLockFromRegistry(file);
        fileLock.lock();
        try {
            if (!file.exists()) {

                boolean created;
                try {
                    created = file.createNewFile();
                } catch (IOException exc) {
                    throw new WriterException("Unexpected IO exception while creating csv file: " + exc.getMessage());
                }

                if (!created) {
                    throw new WriterException("Cannot create file " + file.getAbsolutePath());
                }
            }

            try {
                csvMapper.writer(csvSchema).writeValue(file, aggregatedData);
            } catch (StreamWriteException exc) {
                throw new WriterException("Can't write data to csv file: " + exc.getMessage());
            } catch (DatabindException exc) {
                throw new WriterException("Can't bind data: " + exc.getMessage());
            } catch (IOException exc) {
                throw new WriterException("Unexpected IO exception while writing to csv file: " + exc.getMessage());
            }
        } finally {
            fileLock.unlock();
        }
    }

    public void append(File file, List<Map<String, String>> aggregatedData) {
        if (aggregatedData.isEmpty()) {
            return;
        } else if (!file.exists() || file.length() == 0) {
            write(file, aggregatedData);
            return;
        }

        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        ReentrantLock fileLock = FileAccessRegistry.getFileLockFromRegistry(file);

        fileLock.lock();
        try {
            try (MappingIterator<Map<String, String>> iterator =
                         csvMapper
                                 .readerFor(Map.class)
                                 .with(schema)
                                 .readValues(file)) {

                List<Map<String, String>> fullData = iterator.readAll();
                fullData.addAll(aggregatedData);

                write(file, fullData);
            } catch (StreamWriteException exc) {
                throw new WriterException("Can't append data to csv file: " + exc.getMessage());
            } catch (DatabindException exc) {
                throw new WriterException("Can't bind data: " + exc.getMessage());
            } catch (IOException exc) {
                throw new WriterException("Unexpected IO exception while appending to csv file: " + exc.getMessage());
            }
        } finally {
            fileLock.unlock();
        }
    }
}
