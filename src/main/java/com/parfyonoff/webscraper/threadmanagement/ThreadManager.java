package com.parfyonoff.webscraper.threadmanagement;

import com.parfyonoff.webscraper.config.APIClientsConfig;

import java.util.concurrent.*;

public class ThreadManager {
    private final static Integer BASIC_TIME_TO_WAIT = 10;
    private final MultiThreadingConfig multiThreadingConfig;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ConcurrentLinkedDeque<ScheduledFuture<?>> scheduledFuturesQueue;

    public ThreadManager(MultiThreadingConfig multiThreadingConfig) {
        this.multiThreadingConfig = multiThreadingConfig;

        if (multiThreadingConfig == null) {
            throw new ThreadManagementException("MultiThreadingConfig is null");
        } else if (multiThreadingConfig.maxTasks() == null || multiThreadingConfig.maxTasks() < 1) {
            throw new ThreadManagementException("MultiThreadingConfig.maxTasks() is null or its value is less than 1");
        } else if (multiThreadingConfig.interval() == null || multiThreadingConfig.interval() < 1) {
            throw new ThreadManagementException("Interval is null or its value is less than 1");
        } else if (multiThreadingConfig.maxTasks() > APIClientsConfig.getNumOfApiClients()) {
            System.out.println("Warning! Making amount of parallel tasks larger than amount of api clients (>" + APIClientsConfig.getNumOfApiClients() + ") is miscellaneous");
        }

        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(multiThreadingConfig.maxTasks());
        this.scheduledFuturesQueue = new ConcurrentLinkedDeque<>();
    }

    public void execute(Runnable runnable) {
        if (scheduledExecutorService.isShutdown()) {
            throw new ThreadManagementException("Executor has been shutdown before current launching");
        }

        if (multiThreadingConfig == null) {
            throw new ThreadManagementException("MultiThreadingConfig is null");
        } else if (runnable == null) {
            throw new ThreadManagementException("Runnable is null");
        }

        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                runnable,
                0,
                multiThreadingConfig.interval(),
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
