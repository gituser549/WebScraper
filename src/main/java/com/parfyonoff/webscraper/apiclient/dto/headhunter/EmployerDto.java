package com.parfyonoff.webscraper.apiclient.dto.headhunter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployerDto(
    @JsonProperty("id")
    String id,

    @JsonProperty("name")
    String name,

    @JsonProperty("url")
    String url
) {}
