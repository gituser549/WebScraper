package com.parfyonoff.webscraper.threadmanagement;

public record MultiThreadingConfig(
    Integer maxTasks,
    Integer interval
) {}
