package file.printer;

import com.parfyonoff.webscraper.file.printer.CsvPrinter;
import com.parfyonoff.webscraper.file.printer.PrinterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


public class CsvPrinterTest {
    @Test
    public void testCsvPrinter(@TempDir Path tempDir) throws IOException {
        File incorrectFile = tempDir.resolve("incorrect.unknown").toFile();

        CsvPrinter csvPrinter = new CsvPrinter();

        Exception exc = assertThrows(PrinterException.class, () -> csvPrinter.printFile(incorrectFile, null));
        assertEquals("file does not exist or is empty", exc.getMessage());

        assertTrue(incorrectFile.createNewFile());

        exc = assertThrows(PrinterException.class, () -> csvPrinter.printFile(incorrectFile, null));
        assertEquals("file does not exist or is empty", exc.getMessage());

        Files.write(incorrectFile.toPath(), "id,name,agg_source\n".getBytes());

        exc = assertThrows(PrinterException.class, () -> csvPrinter.printFile(incorrectFile, null));
        assertEquals("choiceToPrint is null or blank", exc.getMessage());

        exc = assertThrows(PrinterException.class, () -> csvPrinter.printFile(incorrectFile, ""));
        assertEquals("choiceToPrint is null or blank", exc.getMessage());

        exc = assertThrows(PrinterException.class, () -> csvPrinter.printFile(incorrectFile, "all"));
        assertEquals("file must end with extension .csv", exc.getMessage());

        File file = tempDir.resolve("test.csv").toFile();

        Files.write(file.toPath(),
                """
                id,name,agg_source
                1,alex,src1
                2,brad,src2
                3,john,src1
                """.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outputStream));
        try {
            csvPrinter.printFile(file, "src1");
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        outputStream.reset();

        assertTrue(output.contains("1,alex,src1"));
        assertTrue(output.contains("3,john,src1"));

        System.setOut(new PrintStream(outputStream));
        try {
            csvPrinter.printFile(file, "all");
        } finally {
            System.setOut(originalOut);
        }

        output = outputStream.toString();
        outputStream.reset();

        assertTrue(output.contains("1,alex,src1"));
        assertTrue(output.contains("2,brad,src2"));
        assertTrue(output.contains("3,john,src1"));
    }
}
