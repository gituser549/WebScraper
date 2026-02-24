package com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto;

import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;

import static com.parfyonoff.webscraper.apiclient.dto.exchangeresponsedto.ExchangeScraper.ExColumns.*;

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
                item.put(TAGS.getColumnName(), tags.substring(0, tags.length() - 1));
            } else {
                item.put(TAGS.getColumnName(), "[]");
            }

            if (curDtoItem.owner() != null) {
                item.put(OWNER_ID.getColumnName(), String.valueOf(curDtoItem.owner().accountId()));
                item.put(OWNER_REPUTATION.getColumnName(), String.valueOf(curDtoItem.owner().reputation()));
                item.put(OWNER_LINK.getColumnName(), curDtoItem.owner().link());
            } else {
                item.put(OWNER_ID.getColumnName(), "");
                item.put(OWNER_REPUTATION.getColumnName(), "");
                item.put(OWNER_LINK.getColumnName(), "");
            }

            item.put(VIEW_COUNT.getColumnName(), curDtoItem.viewCount().toString());
            item.put(SCORE.getColumnName(),  curDtoItem.score().toString());
            item.put(ANSWER_COUNT.getColumnName(), curDtoItem.answerCount().toString());
            item.put(LAST_ACTIVITY_DATE.getColumnName(), curDtoItem.lastActivityDate().toString());
            item.put(LINK.getColumnName(), curDtoItem.link());
            item.put(TITLE.getColumnName(), curDtoItem.title());
        }

        return result;
    }

    @Override
    public List<String> getFlatColumns() {
        return Arrays.stream(values())
                .map(ExchangeScraper.ExColumns::getColumnName)
                .toList();
    }

    public enum ExColumns {
        TAGS("ex_tags"),
        OWNER_ID("ex_owner_account_id"),
        OWNER_REPUTATION("ex_owner_reputation"),
        OWNER_LINK("ex_owner_link"),
        VIEW_COUNT("ex_view_count"),
        SCORE("ex_score"),
        ANSWER_COUNT("ex_answer_count"),
        LAST_ACTIVITY_DATE("ex_last_activity_date"),
        LINK("ex_link"),
        TITLE("ex_title");

        private final String columnName;

        ExColumns(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }
}
