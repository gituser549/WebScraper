package file.writer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parfyonoff.webscraper.aggregation.AggregatedData;
import com.parfyonoff.webscraper.file.writer.WriterException;
import com.parfyonoff.webscraper.file.writer.structuredwriter.JsonWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JsonWriterTest {
    @Test
    public void testWrite(@TempDir Path tempDir) throws IOException {
        File notJsonFile = tempDir.resolve("test.unknown").toFile();

        Exception exc = assertThrows(WriterException.class,
                () ->
                    new JsonWriter().write(
                    notJsonFile,
                    new AggregatedData(
                    null,
                null,
            null,
                    null
                    )
                )
        );
        assertEquals("Not a json file", exc.getMessage());
        assertFalse(notJsonFile.exists());

        File file = tempDir.resolve("test.json").toFile();

        AggregatedData aggregatedData = new AggregatedData(
                UUID.randomUUID(),
                "knowledge",
                Instant.now().toString(),
                null
        );

        assertDoesNotThrow(() -> new JsonWriter().write(file, aggregatedData));
        assertTrue(file.exists());

        ObjectMapper objectMapper = new ObjectMapper();
        assertEquals(aggregatedData, objectMapper.readValue(file, new TypeReference<List<AggregatedData>>() {}).getFirst());
    }

    @Test
    public void testAppend(@TempDir Path tempDir) throws IOException {
        File notJsonFile = tempDir.resolve("test.unknown").toFile();

        Exception exc = assertThrows(WriterException.class,
                () ->
                        new JsonWriter().append(
                                notJsonFile,
                                new AggregatedData(
                                        null,
                                        null,
                                        null,
                                        null
                                )
                        )
        );
        assertEquals("Not a json file", exc.getMessage());
        assertFalse(notJsonFile.exists());

        File file = tempDir.resolve("test.json").toFile();

        AggregatedData aggregatedData = new AggregatedData(
                UUID.randomUUID(),
                "knowledge",
                Instant.now().toString(),
                null
        );

        AggregatedData aggregatedData1 = new AggregatedData(
                UUID.randomUUID(),
                "brain",
                Instant.now().toString(),
                null
        );

        assertDoesNotThrow(() -> new JsonWriter().append(file, aggregatedData));
        assertTrue(file.exists());

        ObjectMapper objectMapper = new ObjectMapper();
        assertEquals(aggregatedData, objectMapper.readValue(file, new TypeReference<List<AggregatedData>>() {}).getFirst());

        assertDoesNotThrow(() -> new JsonWriter().append(file, aggregatedData1));
        assertTrue(file.exists());

        assertEquals(aggregatedData, objectMapper.readValue(file, new TypeReference<List<AggregatedData>>() {}).getFirst());
        assertEquals(aggregatedData1, objectMapper.readValue(file, new TypeReference<List<AggregatedData>>() {}).get(1));
    }
}
