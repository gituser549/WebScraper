package com.parfyonoff.webscraper.file;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class FileCleaner {
    public static void clean(File file) throws FileException {
        ReentrantLock fileLock = FileAccessRegistry.getFileLockFromRegistry(file);

        fileLock.lock();
        if (file.exists()) {
            boolean deleteResult = file.delete();
            if (!deleteResult) {
                throw new FileException("Could not delete file: " + file.getAbsolutePath());
            }
        }

        boolean createResult;
        try {
            createResult = file.createNewFile();
        } catch (IOException exc) {
            throw new FileException("Could not create new file, got IOException " + file.getAbsolutePath());
        }

        if (!createResult) {
            throw new FileException("Could not create new file: " + file.getAbsolutePath());
        }
        fileLock.unlock();
    }
}
