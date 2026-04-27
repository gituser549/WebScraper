package cli;

import com.parfyonoff.webscraper.applicationbuilding.ApplicationBuilder;
import com.parfyonoff.webscraper.applicationexecution.ApplicationExecutor;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.cli.CliException;
import com.parfyonoff.webscraper.cli.CliRunner;
import com.parfyonoff.webscraper.threadmanagement.MultiThreadingConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CliRunnerTest {

    @Mock
    private ApplicationBuilder applicationBuilder;

    @Test
    void testCliRunnerCreation() {
        Exception exc = assertThrows(CliException.class, () -> new CliRunner(null, null));
        assertEquals( "Scanner or ApplicationBuilder is null", exc.getMessage());

        exc = assertThrows(CliException.class, () -> new CliRunner(new Scanner(System.in), null));
        assertEquals("Scanner or ApplicationBuilder is null", exc.getMessage());

        assertDoesNotThrow(() -> new CliRunner(new Scanner(System.in), applicationBuilder));
    }

    @Test
    void testRunAppAndAwaitForStart() {
        Exception exc = assertThrows(CliException.class, () -> new CliRunner(new Scanner(System.in), applicationBuilder).runAppAndAwaitForStop(null));
        assertEquals("Application Executor cant be null", exc.getMessage());

        ApplicationExecutor applicationExecutor = mock(ApplicationExecutor.class);
        //when(applicationBuilder.build(any(ExecutionConfig.class), any(MultiThreadingConfig.class))).thenReturn(applicationExecutor);

        assertDoesNotThrow(() -> new CliRunner(
                new Scanner
                        ("""
                        123
                        stop
                        """), applicationBuilder)
                .runAppAndAwaitForStop(applicationExecutor));

        verify(applicationExecutor, times(1)).run();
        verify(applicationExecutor, times(1)).stop();
    }

    @Test
    void testStart() {
        Exception exc = assertThrows(CliException.class, () -> new CliRunner(
                new Scanner("""
                        hn hh ex
                        test.json
                        yes
                        all
                        abc
                        3
                        [wait]
                        stop
                        """),
                applicationBuilder)
                .start()
        );
        assertTrue(exc.getMessage().startsWith("Input mismatch exception while reading maxTasks: "));

        exc = assertThrows(CliException.class, () -> new CliRunner(
                new Scanner("""
                        ex hn hh
                        test.json
                        yes
                        all
                        2
                        abc
                        [wait]
                        stop
                        """),
                applicationBuilder)
                .start()
        );
        assertTrue(exc.getMessage().startsWith("Input mismatch exception while reading polling interval: "));

        ApplicationExecutor applicationExecutor = mock(ApplicationExecutor.class);
        when(applicationBuilder.build(any(ExecutionConfig.class), any(MultiThreadingConfig.class))).thenReturn(applicationExecutor);

        assertDoesNotThrow(() -> new CliRunner(
                new Scanner("""
                        hn hh ex
                        test.json
                        yes
                        all
                        3
                        2
                        [wait]
                        stop
                        """),
                applicationBuilder)
                .start()
        );

        verify(applicationExecutor, times(1)).run();
        verify(applicationExecutor, times(1)).stop();
    }
}
