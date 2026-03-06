package com.parfyonoff.webscraper.apiclient.dto.hackernews;

import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;

import static com.parfyonoff.webscraper.apiclient.dto.hackernews.HackerNewsScraper.HnColumns.*;

public class HackerNewsScraper implements APIClient<HackerNewsResponseDto> {
    private final URI uri;
    public final static String basicURI = "https://hn.algolia.com/api/v1/search?query=devops&tags=story&hitsPerPage=20&page=0";
    private final Fetcher fetcher;

    public HackerNewsScraper(Fetcher fetcher) {
        if (fetcher == null) {
            throw new APIClientException("fetcher cannot be null");
        }

        this.fetcher = fetcher;
        uri = URI.create(basicURI);
    }

    @Override
    public HackerNewsResponseDto fetchToDTO() {
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        return fetcher.fetch(request, HackerNewsResponseDto.class);
    }

    @Override
    public List<Map<String, String>> fetchToMap() {
        HackerNewsResponseDto hackerNewsResponse = fetchToDTO();

        List<Map<String, String>> result = new ArrayList<>();
        for (HitsDto curHitsDto : hackerNewsResponse.hits()) {
            Map<String, String> item = new LinkedHashMap<>();
            result.add(item);

            StringBuilder tags = new StringBuilder();
            for (String tag : curHitsDto.tags()) {
                tags.append(tag);
                tags.append(",");
            }

            if (!tags.isEmpty()) {
                item.put(TAGS.getColumnName(), tags.substring(0, tags.length() - 1));
            } else {
                item.put(TAGS.getColumnName(), "[]");
            }

            item.put(AUTHOR.getColumnName(), curHitsDto.author());

            StringBuilder children = new StringBuilder();
            for (Integer child : curHitsDto.children()) {
                children.append(child.toString());
                children.append(",");
            }

            if (!children.isEmpty()) {
                item.put(CHILDREN.getColumnName(), children.substring(0, children.length() - 1));
            } else {
                item.put(CHILDREN.getColumnName(), "[]");
            }

            item.put(CREATED_AT.getColumnName(), curHitsDto.createdAt());
            item.put(NUM_COMMENTS.getColumnName(), curHitsDto.numComments().toString());
            item.put(STORY_ID.getColumnName(), String.valueOf(curHitsDto.storyId()));
            item.put(TITLE.getColumnName(), String.valueOf(curHitsDto.title()));
            item.put(UPDATED_AT.getColumnName(), curHitsDto.updatedAt());
            item.put(URL.getColumnName(), curHitsDto.url());
        }

        return result;
    }

    @Override
    public List<String> getFlatColumns() {
        return Arrays.stream(values())
                .map(HackerNewsScraper.HnColumns::getColumnName)
                .toList();
    }

    public enum HnColumns {
        TAGS("hn_tags"),
        AUTHOR("hn_author"),
        CHILDREN("hn_children"),
        CREATED_AT("hn_createdAt"),
        NUM_COMMENTS("hn_numComments"),
        STORY_ID("hn_storyId"),
        TITLE("hn_title"),
        UPDATED_AT("hn_updatedAt"),
        URL("hn_url");

        private final String columnName;

        HnColumns(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }
}
