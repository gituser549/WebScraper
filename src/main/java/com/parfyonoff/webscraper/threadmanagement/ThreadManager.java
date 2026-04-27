package com.parfyonoff.webscraper.threadmanagement;

import java.util.concurrent.*;

public class ThreadManager {
    private final static Integer BASIC_TIME_TO_WAIT = 10;
    private final Integer interval;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ConcurrentLinkedDeque<ScheduledFuture<?>> scheduledFuturesQueue;

    public ThreadManager(ScheduledExecutorService scheduledExecutorService, Integer interval) {
        if (scheduledExecutorService == null) {
            throw new ThreadManagementException("scheduledExecutorService cannot be null");
        } else if (interval == null) {
            throw new ThreadManagementException("interval cannot be null");
        }

        this.interval = interval;
        this.scheduledExecutorService = scheduledExecutorService;
        this.scheduledFuturesQueue = new ConcurrentLinkedDeque<>();
    }

    public void execute(Runnable runnable) {
        if (scheduledExecutorService.isShutdown()) {
            throw new ThreadManagementException("Executor has been shutdown before current launching");
        } else if (runnable == null) {
            throw new ThreadManagementException("Runnable is null");
        }

        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                runnable,
                0,
                interval,
                TimeUnit.SECONDS
        );

        scheduledFuturesQueue.add(scheduledFuture);
    }

    public void stop() {
        ScheduledFuture<?> scheduledFuture;
        while ((scheduledFuture = scheduledFuturesQueue.pollFirst()) != null) {
            scheduledFuture.cancel(false);
        }

        scheduledExecutorService.shutdown();

        try {
            if (!scheduledExecutorService.awaitTermination(BASIC_TIME_TO_WAIT, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();

                if (!scheduledExecutorService.awaitTermination(BASIC_TIME_TO_WAIT, TimeUnit.SECONDS)) {
                    throw new ThreadManagementException("Timeout waiting for termination results reached");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted while waiting for scheduled futures " + e.getMessage() + " Cause: " + e.getCause());
        }
    }
}
