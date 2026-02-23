package com.parfyonoff.webscraper.apiclient;

import java.util.List;
import java.util.Map;

public interface APIClient<T> {
    T fetchToDTO();
    List<Map<String, String>> fetchToMap();
}
