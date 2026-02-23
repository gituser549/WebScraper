package com.parfyonoff.webscraper.apiclient.dto.headhunterdto;

import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;

public class HeadHunterScraper implements APIClient<HeadHunterResponseDto> {
    private final URI uri;
    public final String basicURI = "https://api.hh.ru/vacancies?text=java&area=1&page=1&only_with_salary=true&salary=150000";
    public final String formatURI = "https://api.hh.ru/vacancies?text=%s&area=%s&page=%s&only_with_salary=true&salary=%s";
    private final Fetcher fetcher;

    public HeadHunterScraper(Fetcher fetcher) {
        if (fetcher == null) {
            throw new APIClientException("fetcher cannot be null");
        }

        this.fetcher = fetcher;
        uri = URI.create(basicURI);
    }

    public HeadHunterScraper(Fetcher fetcher, List<String> params) {
        if (fetcher == null) {
            throw new APIClientException("fetcher cannot be null");
        } else if (params == null || params.size() != 4) {
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
    public HeadHunterResponseDto fetchToDTO() {
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        return fetcher.fetch(request, HeadHunterResponseDto.class);
    }

    @Override
    public List<Map<String, String>> fetchToMap() {
        HeadHunterResponseDto exchangeResponseDto = fetchToDTO();

        List<Map<String, String>> result = new ArrayList<>();
        for (HeadHunterItemsDto curDtoItem : exchangeResponseDto.items()) {
            Map<String, String> item = new LinkedHashMap<>();
            result.add(item);

            item.put("id", curDtoItem.id());
            item.put("name", curDtoItem.name());

            if (curDtoItem.area() != null) {
                item.put("area_id", curDtoItem.area().id());
                item.put("area_name", curDtoItem.area().name());
                item.put("area_url", curDtoItem.area().url());
            } else {
                item.put("area_id", null);
                item.put("area_name", null);
                item.put("area_url", null);
            }

            item.put("salary_from", String.valueOf(curDtoItem.salary().from()));
            item.put("salary_to", String.valueOf(curDtoItem.salary().to()));
            item.put("salary_currency", curDtoItem.salary().currency());
            item.put("salary_gross", String.valueOf(curDtoItem.salary().gross()));

            if (curDtoItem.employer() != null) {
                item.put("employer_id", curDtoItem.employer().id());
                item.put("employer_name", curDtoItem.employer().name());
                item.put("employer_url", curDtoItem.employer().url());
            }
        }

        return result;
    }
}
