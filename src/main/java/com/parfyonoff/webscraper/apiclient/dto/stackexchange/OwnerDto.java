package com.parfyonoff.webscraper.apiclient.dto.stackexchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OwnerDto (
    @JsonProperty("account_id")
    Integer accountId,

    @JsonProperty("reputation")
    Integer reputation,

    @JsonProperty("accept_rate")
    Integer acceptRate,

    @JsonProperty("link")
    String link
) {}
