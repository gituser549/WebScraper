package com.parfyonoff.webscraper.apiclient.dto.hackernewsresponsedto;

import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;

public class HackerNewsScraper implements APIClient<HackerNewsResponseDto> {
    private final URI uri;
    public final String basicURI = "https://hn.algolia.com/api/v1/search?query=devops&tags=story&hitsPerPage=20&page=0";
    public final String formatURI = "https://hn.algolia.com/api/v1/search?query=%s&tags=%s&hitsPerPage=%s&page=%s";
    private final Fetcher fetcher;

    public HackerNewsScraper(Fetcher fetcher) {
        if (fetcher == null) {
            throw new APIClientException("fetcher cannot be null");
        }

        this.fetcher = fetcher;
        uri = URI.create(basicURI);
    }

    public HackerNewsScraper(Fetcher fetcher, List<String> params) {
        if (fetcher == null) {
            throw new APIClientException("fetcher cannot be null");
        }
        if (params == null || params.size() != 4) {
            throw new APIClientException("HACKER NEWS SCRAPER: Invalid number of parameters");
        }

        this.fetcher = fetcher;

        try {
            uri = URI.create(String.format(formatURI, params.toArray()));
        } catch (IllegalFormatException exc) {
            throw new APIClientException("HACKER NEWS SCRAPER: Invalid format parameters: " + exc.getMessage());
        } catch (IllegalArgumentException exc) {
            throw new APIClientException("HACKER NEWS SCRAPER: Invalid URI: " + exc.getMessage());
        }
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

            StringBuffer tags = new StringBuffer();
            for (String tag : curHitsDto.tags()) {
                tags.append(tag);
                tags.append(",");
            }

            if (tags.length() > 0) {
                item.put("tags", tags.substring(0, tags.length() - 1));
            } else {
                item.put("tags", "[]");
            }

            item.put("author", curHitsDto.author());

            StringBuffer children = new StringBuffer();
            for (Integer child : curHitsDto.children()) {
                children.append(child.toString());
                children.append(",");
            }

            if (children.length() > 0) {
                item.put("children", children.substring(0, children.length() - 1));
            } else {
                item.put("children", "[]");
            }

            item.put("createdAt", curHitsDto.createdAt());
            item.put("numComments", curHitsDto.numComments().toString());
            item.put("storyId", String.valueOf(curHitsDto.storyId()));
            item.put("title", String.valueOf(curHitsDto.title()));
            item.put("updatedAt", curHitsDto.updatedAt());
            item.put("url", curHitsDto.url());
        }

        return result;
    }
}
