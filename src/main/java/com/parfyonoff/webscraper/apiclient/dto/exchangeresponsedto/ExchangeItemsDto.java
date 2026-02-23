package com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeItemsDto (
    @JsonProperty("tags")
    List<String> tags,

    @JsonProperty("owner")
    OwnerDto owner,

    @JsonProperty("view_count")
    Integer viewCount,

    @JsonProperty("score")
    Integer score,

    @JsonProperty("answer_count")
    Integer answerCount,

    @JsonProperty("last_activity_date")
    Long lastActivityDate,

    @JsonProperty("link")
    String link,

    @JsonProperty("title")
    String title
) {}
