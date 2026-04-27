package threadmanagement;

import com.parfyonoff.webscraper.threadmanagement.ThreadManagementException;
import com.parfyonoff.webscraper.threadmanagement.ThreadManager;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ThreadManagerTest {
    @Test
    public void testThreadManagerCreation() {
        Exception exc = assertThrows(ThreadManagementException.class, () -> new ThreadManager(null, null));
        assertEquals("scheduledExecutorService cannot be null", exc.getMessage());

        exc = assertThrows(ThreadManagementException.class, () -> new ThreadManager(new ScheduledThreadPoolExecutor(1), null));
        assertEquals("interval cannot be null", exc.getMessage());


        assertDoesNotThrow(() -> new ThreadManager(new ScheduledThreadPoolExecutor(3), 2));
    }

    @Test
    public void testBasicProblemsAndCorrectRunAndStop() throws InterruptedException {
        ThreadManager threadManager1 = new ThreadManager(new ScheduledThreadPoolExecutor(3), 2);

        assertDoesNotThrow(threadManager1::stop);

        Exception exc = assertThrows(ThreadManagementException.class, () -> threadManager1.execute(() -> {}));
        assertEquals("Executor has been shutdown before current launching", exc.getMessage());

        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        ScheduledFuture<?> scheduledFuture1 = mock(ScheduledFuture.class);
        ScheduledFuture<?> scheduledFuture2 = mock(ScheduledFuture.class);

        doReturn(scheduledFuture1, scheduledFuture2)
                .when(scheduledExecutorService)
                .scheduleWithFixedDelay(
                        any(Runnable.class),
                        anyLong(),
                        anyLong(),
                        any(TimeUnit.class)
                );

        doReturn(true)
                .when(scheduledExecutorService)
                .awaitTermination(anyLong(), any(TimeUnit.class));

        ThreadManager threadManager2 = new ThreadManager(scheduledExecutorService, 2);

        exc = assertThrows(ThreadManagementException.class, () -> threadManager2.execute(null));
        assertEquals("Runnable is null", exc.getMessage());

        assertDoesNotThrow(() -> threadManager2.execute(() -> {}));
        assertDoesNotThrow(() -> threadManager2.execute(() -> {}));

        assertDoesNotThrow(threadManager2::stop);

        verify(scheduledExecutorService).shutdown();
        verify(scheduledFuture1).cancel(false);
        verify(scheduledFuture2).cancel(false);
    }

    @Test
    public void testShutdownNowWithoutException() throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        ScheduledFuture<?> scheduledFuture1 = mock(ScheduledFuture.class);
        ScheduledFuture<?> scheduledFuture2 = mock(ScheduledFuture.class);

        doReturn(scheduledFuture1, scheduledFuture2)
                .when(scheduledExecutorService)
                .scheduleWithFixedDelay(
                        any(Runnable.class),
                        anyLong(),
                        anyLong(),
                        any(TimeUnit.class)
                );

        doReturn(false).doReturn(true)
                .when(scheduledExecutorService)
                .awaitTermination(anyLong(), any(TimeUnit.class));


        ThreadManager threadManager = new ThreadManager(scheduledExecutorService, 2);

        assertDoesNotThrow(() -> threadManager.execute(() -> {}));
        assertDoesNotThrow(() -> threadManager.execute(() -> {}));

        assertDoesNotThrow(threadManager::stop);

        verify(scheduledExecutorService).shutdown();
        verify(scheduledExecutorService).shutdownNow();
        verify(scheduledFuture1).cancel(false);
        verify(scheduledFuture2).cancel(false);
    }

    @Test
    public void testShutdownNowWithException() throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        ScheduledFuture<?> scheduledFuture1 = mock(ScheduledFuture.class);
        ScheduledFuture<?> scheduledFuture2 = mock(ScheduledFuture.class);

        doReturn(scheduledFuture1, scheduledFuture2)
                .when(scheduledExecutorService)
                .scheduleWithFixedDelay(
                        any(Runnable.class),
                        anyLong(),
                        anyLong(),
                        any(TimeUnit.class)
                );

        doReturn(false).doReturn(false)
                .when(scheduledExecutorService)
                .awaitTermination(anyLong(), any(TimeUnit.class));


        ThreadManager threadManager = new ThreadManager(scheduledExecutorService, 2);

        assertDoesNotThrow(() -> threadManager.execute(() -> {}));
        assertDoesNotThrow(() -> threadManager.execute(() -> {}));

        Exception exc = assertThrows(ThreadManagementException.class, threadManager::stop);
        assertEquals("Timeout waiting for termination results reached", exc.getMessage());
    }

    @Test
    public void testInterruptedExceptionWhileWaitingForTermination() throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        ScheduledFuture<?> scheduledFuture1 = mock(ScheduledFuture.class);
        ScheduledFuture<?> scheduledFuture2 = mock(ScheduledFuture.class);

        doReturn(scheduledFuture1, scheduledFuture2)
                .when(scheduledExecutorService)
                .scheduleWithFixedDelay(
                        any(Runnable.class),
                        anyLong(),
                        anyLong(),
                        any(TimeUnit.class)
                );

        doThrow(InterruptedException.class)
                .when(scheduledExecutorService)
                .awaitTermination(anyLong(), any(TimeUnit.class));

        ThreadManager threadManager = new ThreadManager(scheduledExecutorService, 2);

        assertDoesNotThrow(() -> threadManager.execute(() -> {}));
        assertDoesNotThrow(() -> threadManager.execute(() -> {}));

        assertDoesNotThrow(threadManager::stop);

        // check is already with flag reset
        assertTrue(Thread.interrupted());
    }
}
