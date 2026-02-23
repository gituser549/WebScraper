package com.parfyonoff.webscraper.apiclient.dto.headhunterdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HeadHunterResponseDto (
    @JsonProperty("items")
    List<HeadHunterItemsDto> items
) {}
