package com.parfyonoff.webscraper.config;

import java.util.Arrays;
import java.util.List;

public enum AggregationFieldsConfig {
    AGG_ID("agg_id"),
    AGG_SOURCE("agg_source"),
    AGG_TIMESTAMP("agg_timestamp");

    private final String aggregationFieldName;

    AggregationFieldsConfig(String aggregationFieldName) {
        this.aggregationFieldName = aggregationFieldName;
    }

    public String getAggregationFieldName() {
        return aggregationFieldName;
    }

    public static List<String> getAggregationFieldsNames() {
        return Arrays.stream(values()).map(AggregationFieldsConfig::getAggregationFieldName).toList();
    }
}
