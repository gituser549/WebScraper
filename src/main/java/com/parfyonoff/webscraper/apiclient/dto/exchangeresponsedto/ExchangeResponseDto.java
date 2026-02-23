package com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeResponseDto (
    @JsonProperty("has_more")
    Boolean hasMore,

    @JsonProperty("quota_max")
    Integer quotaMax,

    @JsonProperty("quota_remaining")
    Integer quotaRemaining,

    @JsonProperty("items")
    List<ExchangeItemsDto> items
) {}
