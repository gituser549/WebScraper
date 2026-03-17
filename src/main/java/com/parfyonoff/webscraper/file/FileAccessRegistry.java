package com.parfyonoff.webscraper.file;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FileAccessRegistry {
    private final static Map<String, ReentrantLock> locksMap = new ConcurrentHashMap<>();

    public static ReentrantLock getFileLockFromRegistry(File file) {
        String path = file.getAbsolutePath();
        if (!locksMap.containsKey(path)) {
            locksMap.put(path, new ReentrantLock());
        }

        return locksMap.get(path);
    }
}
