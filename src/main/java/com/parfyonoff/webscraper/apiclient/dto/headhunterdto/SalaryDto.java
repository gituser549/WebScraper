package com.parfyonoff.webscraper.apiclient.dto.headhunterdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SalaryDto(
    @JsonProperty("from")
    Integer from,

    @JsonProperty("to")
    Integer to,

    @JsonProperty("currency")
    String currency,

    @JsonProperty("gross")
    Boolean gross
) {}
