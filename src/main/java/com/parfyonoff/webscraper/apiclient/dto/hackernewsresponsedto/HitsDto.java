package com.parfyonoff.webscraper.apiclient.dto.hackernewsresponsedto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HitsDto (
    @JsonProperty("_tags")
    List<String> tags,

    @JsonProperty("author")
    String author,

    @JsonProperty("children")
    List<Integer> children,

    @JsonProperty("created_at")
    String createdAt,

    @JsonProperty("num_comments")
    Integer numComments,

    @JsonProperty("objectID")
    String objectID,

    @JsonProperty("points")
    Integer points,

    @JsonProperty("story_id")
    Integer storyId,

    @JsonProperty("title")
    String title,

    @JsonProperty("updated_at")
    String updatedAt,

    @JsonProperty("url")
    String url
) {}
