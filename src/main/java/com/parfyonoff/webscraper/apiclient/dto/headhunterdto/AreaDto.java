package com.parfyonoff.webscraper.apiclient.dto.headhunterdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AreaDto (
    @JsonProperty("id")
    String id,

    @JsonProperty("name")
    String name,

    @JsonProperty("url")
    String url
) {}
