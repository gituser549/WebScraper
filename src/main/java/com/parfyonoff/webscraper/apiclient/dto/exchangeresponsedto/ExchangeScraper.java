package com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto;

import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;

public class ExchangeScraper implements APIClient<ExchangeResponseDto> {
    private final URI uri;
    public final String basicURI = "https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&tagged=python&site=stackoverflow&pagesize=10";
    public final String formatURI = "https://api.stackexchange.com/2.3/questions?order=%s&sort=%s&tagged=%s&site=%s&pagesize=%s";
    private final Fetcher fetcher;

    public ExchangeScraper(Fetcher fetcher) {
        if (fetcher == null) {
            throw new APIClientException("fetcher cannot be null");
        }

        this.fetcher = fetcher;
        uri = URI.create(basicURI);
    }

    public ExchangeScraper(Fetcher fetcher, List<String> params) {
        if (fetcher == null) {
            throw new APIClientException("fetcher cannot be null");
        } else if (params == null || params.size() != 5) {
            throw new APIClientException("EXCHANGE SCRAPER: Invalid number of parameters");
        }

        this.fetcher = fetcher;

        try {
            uri = URI.create(String.format(formatURI, params.toArray()));
        } catch (IllegalFormatException exc) {
            throw new APIClientException("EXCHANGE SCRAPER: Invalid format parameters: " + exc.getMessage());
        } catch (IllegalArgumentException exc) {
            throw new APIClientException("EXCHANGE SCRAPER: Invalid URI: " + exc.getMessage());
        }
    }

    @Override
    public ExchangeResponseDto fetchToDTO() {
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        return fetcher.fetch(request, ExchangeResponseDto.class);
    }

    @Override
    public List<Map<String, String>> fetchToMap() {
        ExchangeResponseDto exchangeResponseDto = fetchToDTO();

        List<Map<String, String>> result = new ArrayList<>();
        for (ExchangeItemsDto curDtoItem : exchangeResponseDto.items()) {
            Map<String, String> item = new LinkedHashMap<>();
            result.add(item);

            StringBuffer tags = new StringBuffer();
            for (String tag : curDtoItem.tags()) {
                tags.append(tag);
                tags.append(",");
            }

            if (tags.length() > 0) {
                item.put("tags", tags.substring(0, tags.length() - 1));
            } else {
                item.put("tags", "[]");
            }

            if (curDtoItem.owner() != null) {
                item.put("owner_account_id", String.valueOf(curDtoItem.owner().accountId()));
                item.put("owner_reputation", String.valueOf(curDtoItem.owner().reputation()));
                item.put("owner_link", curDtoItem.owner().link());
            } else {
                item.put("owner_account_id", "");
                item.put("owner_reputation", "");
                item.put("owner_link", "");
            }

            item.put("view_count", curDtoItem.viewCount().toString());
            item.put("score",  curDtoItem.score().toString());
            item.put("answer_count", curDtoItem.answerCount().toString());
            item.put("last_activity_date", curDtoItem.lastActivityDate().toString());
            item.put("link", curDtoItem.link());
            item.put("title", curDtoItem.title());
        }

        return result;
    }
}
