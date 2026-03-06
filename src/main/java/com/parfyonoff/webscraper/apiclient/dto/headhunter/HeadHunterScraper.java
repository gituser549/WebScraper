package com.parfyonoff.webscraper.apiclient.dto.headhunter;

import com.parfyonoff.webscraper.apiclient.APIClient;
import com.parfyonoff.webscraper.apiclient.APIClientException;
import com.parfyonoff.webscraper.apiclient.Fetcher;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;

import static com.parfyonoff.webscraper.apiclient.dto.headhunter.HeadHunterScraper.HhColumns.*;

public class HeadHunterScraper implements APIClient<HeadHunterResponseDto> {
    private final URI uri;
    public final static String basicURI = "https://api.hh.ru/vacancies?text=java&area=1&page=1&only_with_salary=true&salary=150000";
    private final Fetcher fetcher;

    public HeadHunterScraper(Fetcher fetcher) {
        if (fetcher == null) {
            throw new APIClientException("fetcher cannot be null");
        }

        this.fetcher = fetcher;
        uri = URI.create(basicURI);
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

            item.put(ID.getColumnName(), curDtoItem.id());
            item.put(NAME.getColumnName(), curDtoItem.name());

            if (curDtoItem.area() != null) {
                item.put(AREA_ID.getColumnName(), curDtoItem.area().id());
                item.put(AREA_NAME.getColumnName(), curDtoItem.area().name());
                item.put(AREA_URL.getColumnName(), curDtoItem.area().url());
            } else {
                item.put(AREA_ID.getColumnName(), null);
                item.put(AREA_NAME.getColumnName(), null);
                item.put(AREA_URL.getColumnName(), null);
            }

            item.put(SALARY_FROM.getColumnName(), String.valueOf(curDtoItem.salary().from()));
            item.put(SALARY_TO.getColumnName(), String.valueOf(curDtoItem.salary().to()));
            item.put(SALARY_CURRENCY.getColumnName(), curDtoItem.salary().currency());
            item.put(SALARY_GROSS.getColumnName(), String.valueOf(curDtoItem.salary().gross()));

            if (curDtoItem.employer() != null) {
                item.put(EMPLOYER_ID.getColumnName(), curDtoItem.employer().id());
                item.put(EMPLOYER_NAME.getColumnName(), curDtoItem.employer().name());
                item.put(EMPLOYER_URL.getColumnName(), curDtoItem.employer().url());
            } else {
                item.put(EMPLOYER_ID.getColumnName(), null);
                item.put(EMPLOYER_NAME.getColumnName(), null);
                item.put(EMPLOYER_URL.getColumnName(), null);
            }
        }

        return result;
    }

    @Override
    public List<String> getFlatColumns() {
        return Arrays.stream(HhColumns.values())
                .map(HhColumns::getColumnName)
                .toList();
    }

    public enum HhColumns {
        ID("hh_id"),
        NAME("hh_name"),
        AREA_ID("hh_area_id"),
        AREA_NAME("hh_area_name"),
        AREA_URL("hh_area_url"),
        SALARY_FROM("hh_salary_from"),
        SALARY_TO("hh_salary_to"),
        SALARY_CURRENCY("hh_salary_currency"),
        SALARY_GROSS("hh_salary_gross"),
        EMPLOYER_ID("hh_employer_id"),
        EMPLOYER_NAME("hh_employer_name"),
        EMPLOYER_URL("hh_employer_url");

        private final String columnName;

        HhColumns(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

}
