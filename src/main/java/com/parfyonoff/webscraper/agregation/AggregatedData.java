package com.parfyonoff.webscraper.agregation;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AggregatedData (
    @JsonProperty("agg_id")
    UUID agg_id,

    @JsonProperty("agg_source")
    String agg_source,

    @JsonProperty("agg_timestamp")
    String agg_timestamp,

    @JsonProperty("data")
    Object data
) {}

