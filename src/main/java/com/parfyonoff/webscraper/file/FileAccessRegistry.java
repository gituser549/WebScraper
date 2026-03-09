package com.parfyonoff.webscraper.file;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileAccessRegistry {
    private final static Map<String, Object> locksMap = new ConcurrentHashMap<>();

    public static Object getFileLockFromRegistry(File file) {
        String path = file.getAbsolutePath();
        if (!locksMap.containsKey(path)) {
            locksMap.put(path, new Object());
        }

        return locksMap.get(path);
    }
}
