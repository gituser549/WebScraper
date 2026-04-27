package applicationexecution;

import com.parfyonoff.webscraper.aggregation.AggregationException;
import com.parfyonoff.webscraper.aggregation.service.Service;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutor;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutorException;
import com.parfyonoff.webscraper.applicationexecution.DependenciesConfig;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.file.FileCleaner;
import com.parfyonoff.webscraper.file.printer.CsvPrinter;
import com.parfyonoff.webscraper.file.printer.JsonPrinter;
import com.parfyonoff.webscraper.file.printer.Printer;
import com.parfyonoff.webscraper.file.writer.flatwriter.CsvWriter;
import com.parfyonoff.webscraper.file.writer.flatwriter.FlatWriter;
import com.parfyonoff.webscraper.file.writer.structuredwriter.JsonWriter;
import com.parfyonoff.webscraper.file.writer.structuredwriter.StructuredWriter;
import com.parfyonoff.webscraper.threadmanagement.ThreadManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationExecutorTest {

    private final List<String> apiNamesList = new ArrayList<>(List.of("hn", "hh", "ex"));
    private final String fileJson = "test.json";
    private final String fileCsv = "test.csv";

    private final Map<String, FlatWriter> flatWriters = new LinkedHashMap<>();
    private final Map<String, StructuredWriter> structuredWriters = new LinkedHashMap<>();
    private final Map<String, Printer> printers = new LinkedHashMap<>();

    @Mock
    private Service service;

    @BeforeEach
    public void setup() {
        flatWriters.put("csv", mock(CsvWriter.class));
        structuredWriters.put("json", mock(JsonWriter.class));
    }

    @Test
    public void testApplicationExecutorCreation() {
        ThreadManager threadManager = mock(ThreadManager.class);

        Exception exc = assertThrows(ApplicationExecutorException.class, () -> new ApplicationExecutor(null, null, null));
        assertEquals("DependenciesConfig cannot be null", exc.getMessage());

        exc = assertThrows(ApplicationExecutorException.class, () -> new ApplicationExecutor(new DependenciesConfig(null, null, null, null), null, null));
        assertEquals("ExecutionConfig cannot be null", exc.getMessage());

        exc = assertThrows(ApplicationExecutorException.class, () -> new ApplicationExecutor(new DependenciesConfig(null, null, null, null), new ExecutionConfig(null, null, null, null), null));
        assertEquals("ThreadManager cannot be null", exc.getMessage());

        assertDoesNotThrow(() -> new ApplicationExecutor(new DependenciesConfig(flatWriters, structuredWriters, service, printers), new ExecutionConfig(apiNamesList, fileJson, true, "all"), threadManager));
    }

    @Test
    public void testRunWithBrokenConfig() {
        ThreadManager threadManager = mock(ThreadManager.class);

        Exception exc = assertThrows(ApplicationExecutorException.class, () -> new ApplicationExecutor(
                new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                new ExecutionConfig(null, fileJson, true, "all"),
                threadManager).run()
        );
        assertEquals("Api names list is empty or even null", exc.getMessage());

        exc = assertThrows(ApplicationExecutorException.class, () -> new ApplicationExecutor(
                new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                new ExecutionConfig(new ArrayList<>(), fileJson, true, "all"),
                threadManager).run()
        );
        assertEquals("Api names list is empty or even null", exc.getMessage());

        exc = assertThrows(ApplicationExecutorException.class, () -> new ApplicationExecutor(
                new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                new ExecutionConfig(apiNamesList, "", true, "all"),
                threadManager).run()
        );
        assertEquals("fileName is null or blank", exc.getMessage());

        exc = assertThrows(ApplicationExecutorException.class, () -> new ApplicationExecutor(
                new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                new ExecutionConfig(apiNamesList, null, true, "all"),
                threadManager).run()
        );
        assertEquals("fileName is null or blank", exc.getMessage());

        String unknownFileExtension = "unknown";
        exc = assertThrows(ApplicationExecutorException.class, () -> new ApplicationExecutor(
                new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                new ExecutionConfig(apiNamesList, "test." + unknownFileExtension, true, "all"),
                threadManager).run()
        );
        assertEquals("unknown fileExtension: " + unknownFileExtension, exc.getMessage());
    }

    @Test
    public void testStructuredRunAndStop() {
        ThreadManager threadManager = mock(ThreadManager.class);

        try (MockedStatic<FileCleaner> mocked = mockStatic(FileCleaner.class)) {
            // printer not found
            ApplicationExecutor applicationExecutor = new ApplicationExecutor(
                    new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                    new ExecutionConfig(apiNamesList, fileJson, true, "all"),
                    threadManager);

            assertDoesNotThrow(applicationExecutor::run);
            Exception exc = assertThrows(ApplicationExecutorException.class, applicationExecutor::stop);
            assertEquals("printer not found for extension json", exc.getMessage());

            // printer added
            printers.put("json", mock(JsonPrinter.class));
            // printer added

            // choiceToPrint is null
            applicationExecutor = new ApplicationExecutor(
                    new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                    new ExecutionConfig(apiNamesList, fileJson, true, null),
                    threadManager);

            assertDoesNotThrow(applicationExecutor::run);

            exc = assertThrows(ApplicationExecutorException.class, applicationExecutor::stop);
            assertEquals("choiceToPrint is null or blank", exc.getMessage());

            // choiceToPrint is blank
            applicationExecutor = new ApplicationExecutor(
                    new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                    new ExecutionConfig(apiNamesList, fileJson, true, ""),
                    threadManager);

            assertDoesNotThrow(applicationExecutor::run);

            exc = assertThrows(ApplicationExecutorException.class, applicationExecutor::stop);
            assertEquals("choiceToPrint is null or blank", exc.getMessage());

            // correct work
            applicationExecutor = new ApplicationExecutor(
                    new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                    new ExecutionConfig(apiNamesList, fileJson, true, "all"),
                    threadManager);

            assertDoesNotThrow(applicationExecutor::run);
            assertDoesNotThrow(applicationExecutor::stop);

            verify(threadManager, times(4 * apiNamesList.size())).execute(any(Runnable.class));
            verify(threadManager, times(4)).stop();
            mocked.verify(() -> FileCleaner.clean(new File(fileJson)), times(4));
        }
    }

    @Test
    public void testFlatRunAndStop() {
        ThreadManager threadManager = mock(ThreadManager.class);

        try (MockedStatic<FileCleaner> mocked = mockStatic(FileCleaner.class)) {
            // printer not found
            ApplicationExecutor applicationExecutor = new ApplicationExecutor(
                    new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                    new ExecutionConfig(apiNamesList, fileCsv, true, "all"),
                    threadManager);

            assertDoesNotThrow(applicationExecutor::run);

            Exception exc = assertThrows(ApplicationExecutorException.class, applicationExecutor::stop);
            assertEquals("printer not found for extension csv", exc.getMessage());

            // printer added
            printers.put("csv", mock(CsvPrinter.class));
            // printer added

            // choiceToPrint is null
           applicationExecutor = new ApplicationExecutor(
                    new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                    new ExecutionConfig(apiNamesList, fileCsv, true, null),
                    threadManager);

            assertDoesNotThrow(applicationExecutor::run);

            exc = assertThrows(ApplicationExecutorException.class, applicationExecutor::stop);
            assertEquals("choiceToPrint is null or blank", exc.getMessage());

            // choiceToPrint is blank
            applicationExecutor = new ApplicationExecutor(
                    new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                    new ExecutionConfig(apiNamesList, fileCsv, true, ""),
                    threadManager);

            assertDoesNotThrow(applicationExecutor::run);

            exc = assertThrows(ApplicationExecutorException.class, applicationExecutor::stop);
            assertEquals("choiceToPrint is null or blank", exc.getMessage());

            // correct work
            applicationExecutor = new ApplicationExecutor(
                    new DependenciesConfig(flatWriters, structuredWriters, service, printers),
                    new ExecutionConfig(apiNamesList, fileCsv, true, "all"),
                    threadManager);

            assertDoesNotThrow(applicationExecutor::run);
            assertDoesNotThrow(applicationExecutor::stop);

            verify(threadManager, times(4 * apiNamesList.size())).execute(any(Runnable.class));
            verify(threadManager, times(4)).stop();
            mocked.verify(() -> FileCleaner.clean(new File(fileCsv)), times(4));
        }
    }

    @Test
    public void testWrapper() {
        ApplicationExecutor.SchedulingExecutionJob job1 =
                (apiName) -> { throw new AggregationException("example: " + apiName); };

        Exception exc = assertThrows(AggregationException.class,
                () -> ApplicationExecutor.RunnableWrapper.of(job1, "example").run());
        assertEquals("example: example", exc.getMessage());

        ApplicationExecutor.SchedulingExecutionJob job2 =
                (apiName) -> { throw new RuntimeException("example: " + apiName); };

        exc = assertThrows(RuntimeException.class,
                () -> ApplicationExecutor.RunnableWrapper.of(job2, "example").run());
        assertEquals("example: example", exc.getMessage());

        ApplicationExecutor.SchedulingExecutionJob job3 =
                (apiName) -> System.out.println("example: " + apiName);

        assertDoesNotThrow(() -> ApplicationExecutor.RunnableWrapper.of(job3, "example").run());
    }
}
