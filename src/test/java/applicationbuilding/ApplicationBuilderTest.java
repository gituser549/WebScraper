package applicationbuilding;

import com.parfyonoff.webscraper.applicationbuilding.ApplicationBuilder;
import com.parfyonoff.webscraper.applicationbuilding.ApplicationBuildingException;
import com.parfyonoff.webscraper.applicationexecution.ExecutionConfig;
import com.parfyonoff.webscraper.threadmanagement.MultiThreadingConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationBuilderTest {
    @Test
    void testApplicationBuilderCreation() {
        assertDoesNotThrow(ApplicationBuilder::new);
    }

    @Test
    void testBuild() {

        ApplicationBuilder applicationBuilder = new ApplicationBuilder();

        ExecutionConfig validExecutionConfig = new ExecutionConfig(
                List.of("hn", "hh", "ex"),
                "test.json",
                true,
                "all"
        );

        MultiThreadingConfig validMultiThreadingConfig = new MultiThreadingConfig(
                3,
                2
        );

        assertDoesNotThrow(() -> applicationBuilder.build(validExecutionConfig, validMultiThreadingConfig));

        Exception exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(null, validMultiThreadingConfig)
        );
        assertEquals("ExecutionConfig cannot be null", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(validExecutionConfig, null)
        );
        assertEquals("MultiThreadingConfig cannot be null", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(
                        new ExecutionConfig(null, "test.json", true, "all"),
                        validMultiThreadingConfig
                )
        );
        assertEquals("apiNamesList cannot be null", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(
                        new ExecutionConfig(List.of("hn"), null, true, "all"),
                        validMultiThreadingConfig
                )
        );
        assertEquals("fileName cannot be null", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(
                        new ExecutionConfig(List.of("hn"), "test.json", null, "all"),
                        validMultiThreadingConfig
                )
        );
        assertEquals("rewrite flag cannot be null", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(
                        validExecutionConfig,
                        new MultiThreadingConfig(0, 2)
                )
        );
        assertEquals("maxTasks must be positive", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(
                        validExecutionConfig,
                        new MultiThreadingConfig(-1, 2)
                )
        );
        assertEquals("maxTasks must be positive", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(
                        validExecutionConfig,
                        new MultiThreadingConfig(3, null)
                )
        );
        assertEquals("interval cannot be null", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(
                        validExecutionConfig,
                        new MultiThreadingConfig(3, 0)
                )
        );
        assertEquals("interval must be positive", exc.getMessage());

        exc = assertThrows(
                ApplicationBuildingException.class,
                () -> applicationBuilder.build(
                        validExecutionConfig,
                        new MultiThreadingConfig(3, -1)
                )
        );
        assertEquals("interval must be positive", exc.getMessage());

    }
}
