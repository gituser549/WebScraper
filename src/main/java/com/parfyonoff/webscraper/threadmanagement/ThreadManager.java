package com.parfyonoff.webscraper.threadmanagement;

import com.parfyonoff.webscraper.config.APIClientsConfig;

import java.util.*;
import java.util.concurrent.*;

public class ThreadManager {
    private enum BasicConfig {
        WAITING_LIMIT_SECONDS(20),
        BASIC_TIME_TO_WAIT(3),
        BASIC_NUM_OF_PARALLEL_TASKS(APIClientsConfig.getNumOfApiClients());

        private final int amount;

        BasicConfig(int amount) {
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }
    }

    private final MultiThreadingConfig multiThreadingConfig;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Deque<ScheduledFuture<?>> scheduledFuturesQueue;

    public ThreadManager(MultiThreadingConfig multiThreadingConfig) {
        this.multiThreadingConfig = multiThreadingConfig;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(multiThreadingConfig.maxTasks());
        this.scheduledFuturesQueue = new ArrayDeque<>();
    }

    public ThreadManager() {
        this(new MultiThreadingConfig(BasicConfig.BASIC_NUM_OF_PARALLEL_TASKS.amount, BasicConfig.BASIC_TIME_TO_WAIT.amount));
    }

    public void execute(Runnable runnable) {
        if (multiThreadingConfig == null) {
            throw new ThreadManagementException("MultiThreadingConfig is null");
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
        while (!scheduledFuturesQueue.isEmpty()) {
            ScheduledFuture<?> scheduledFuture = scheduledFuturesQueue.removeFirst();
            scheduledFuture.cancel(true);
        }

        scheduledExecutorService.shutdown();

        boolean awaitTerminationResult = false;
        try {
            if (!scheduledExecutorService.awaitTermination(BasicConfig.BASIC_TIME_TO_WAIT.amount, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();

                if (!scheduledExecutorService.awaitTermination(BasicConfig.BASIC_TIME_TO_WAIT.amount, TimeUnit.SECONDS)) {
                    throw new ThreadManagementException("Timeout waiting for termination results reached");
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for scheduled futures " + e.getCause());
        }


    }
}
