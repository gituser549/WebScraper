package com.parfyonoff.webscraper.applicationexecution;

import java.util.List;

public record ExecutionConfig(
        List<String> apiNamesList,
        String fileName,
        Boolean rewrite,
        String choiceToPrint
) {}
