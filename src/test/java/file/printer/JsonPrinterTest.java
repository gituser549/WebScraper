package file.printer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.parfyonoff.webscraper.aggregation.AggregatedData;
import com.parfyonoff.webscraper.file.printer.JsonPrinter;
import com.parfyonoff.webscraper.file.printer.PrinterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonPrinterTest {
    @Test
    public void testCsvPrinter(@TempDir Path tempDir) throws IOException {
        File incorrectFile = tempDir.resolve("incorrect.unknown").toFile();

        JsonPrinter jsonPrinter = new JsonPrinter();

        Exception exc = assertThrows(PrinterException.class, () -> jsonPrinter.printFile(incorrectFile, null));
        assertEquals("file does not exist or is empty", exc.getMessage());

        assertTrue(incorrectFile.createNewFile());

        exc = assertThrows(PrinterException.class, () -> jsonPrinter.printFile(incorrectFile, null));
        assertEquals("file does not exist or is empty", exc.getMessage());

        Files.write(incorrectFile.toPath(), "id,name,agg_source\n".getBytes());

        exc = assertThrows(PrinterException.class, () -> jsonPrinter.printFile(incorrectFile, null));
        assertEquals("choiceToPrint is null or blank", exc.getMessage());

        exc = assertThrows(PrinterException.class, () -> jsonPrinter.printFile(incorrectFile, ""));
        assertEquals("choiceToPrint is null or blank", exc.getMessage());

        exc = assertThrows(PrinterException.class, () -> jsonPrinter.printFile(incorrectFile, "all"));
        assertEquals("file must end with extension .json", exc.getMessage());

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

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(file, List.of(aggregatedData, aggregatedData1));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outputStream));
        try {
            jsonPrinter.printFile(file, "knowledge");
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        outputStream.reset();

        List<AggregatedData> aggregatedDataList = objectMapper.readValue(output, new TypeReference<>() {});
        assertInstanceOf(AggregatedData.class, aggregatedDataList.getFirst());

        assertEquals(aggregatedData, aggregatedDataList.getFirst());

        System.setOut(new PrintStream(outputStream));
        try {
            jsonPrinter.printFile(file, "all");
        } finally {
            System.setOut(originalOut);
        }

        output = outputStream.toString();
        outputStream.reset();

        aggregatedDataList = objectMapper.readValue(output, new TypeReference<>() {});
        assertInstanceOf(AggregatedData.class, aggregatedDataList.getFirst());

        assertEquals(aggregatedData,  aggregatedDataList.getFirst());
        assertEquals(aggregatedData1, aggregatedDataList.get(1));
    }
}
