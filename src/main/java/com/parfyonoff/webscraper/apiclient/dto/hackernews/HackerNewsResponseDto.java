package com.parfyonoff.webscraper.apiclient.dto.hackernews;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HackerNewsResponseDto (
    @JsonProperty("hits")
    List<HitsDto> hits,

    @JsonProperty("hitsPerPage")
    Integer hitsPerPage,

    @JsonProperty("nbHits")
    Integer nbHits,

    @JsonProperty("nbPages")
    Integer nbPages,

    @JsonProperty("page")
    Integer page,

    @JsonProperty("processingTimeMS")
    Integer processingTimeMS,

    @JsonProperty("query")
    String query,

    @JsonProperty("serverTimeMS")
    Integer serverTimeMS
) {}
