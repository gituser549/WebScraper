package file.writer;

import com.parfyonoff.webscraper.file.writer.WriterException;
import com.parfyonoff.webscraper.file.writer.flatwriter.CsvWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvWriterTest {
    @Test
    public void testCsvWriterCreation() {
        Exception exc = assertThrows(WriterException.class, () -> new CsvWriter(null));
        assertEquals("columns cannot be null or empty", exc.getMessage());

        exc = assertThrows(WriterException.class, () -> new CsvWriter(new ArrayList<>()));
        assertEquals("columns cannot be null or empty", exc.getMessage());

        assertDoesNotThrow(() -> new CsvWriter(List.of("id", "name")));
    }

    @Test
    public void testCsvWriterWrite(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("test.csv").toFile();

        CsvWriter csvWriter = new CsvWriter(List.of("id", "name"));
        csvWriter.write(file, new ArrayList<>());

        assertFalse(file.exists());

        LinkedHashMap<String, String> record = new LinkedHashMap<>();
        record.put("id", "1");
        record.put("name", "Alex");

        assertDoesNotThrow(() -> csvWriter.write(file, List.of(record)));

        assertTrue(file.exists());

        List<String> strs = Arrays.stream(Files.readString(file.toPath()).split("\n")).toList();

        String curStr = strs.getFirst();
        assertEquals("id,name", curStr);

        curStr = strs.get(1);
        assertEquals("1,Alex", curStr);
    }

    @Test
    public void testCsvWriterAppend(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("test.csv").toFile();

        CsvWriter csvWriter = new CsvWriter(List.of("id", "name"));
        assertDoesNotThrow(() -> csvWriter.append(file, new ArrayList<>()));

        assertFalse(file.exists());

        LinkedHashMap<String, String> record = new LinkedHashMap<>();
        record.put("id", "1");
        record.put("name", "Alex");

        assertDoesNotThrow(() -> csvWriter.append(file, List.of(record)));

        assertTrue(file.exists());

        List<String> strs = Arrays.stream(Files.readString(file.toPath()).split("\n")).toList();

        String curStr = strs.getFirst();
        assertEquals("id,name", curStr);

        curStr = strs.get(1);
        assertEquals("1,Alex", curStr);

        LinkedHashMap<String, String> appendingRecord = new LinkedHashMap<>();
        appendingRecord.put("id", "2");
        appendingRecord.put("name", "Brad");

        assertDoesNotThrow(() -> csvWriter.append(file, List.of(appendingRecord)));

        strs = Arrays.stream(Files.readString(file.toPath()).split("\n")).toList();

        curStr = strs.getFirst();
        assertEquals("id,name", curStr);

        curStr = strs.get(1);
        assertEquals("1,Alex", curStr);

        curStr = strs.get(2);
        assertEquals("2,Brad", curStr);
    }
}
