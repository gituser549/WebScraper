package com.parfyonoff.webscraper.agregation;


import java.util.UUID;

public record AggregatedData (
    UUID id,
    String source,
    String timestamp,
    Object data
) {}

