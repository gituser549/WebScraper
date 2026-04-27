package file;

import com.parfyonoff.webscraper.file.FileCleaner;
import com.parfyonoff.webscraper.file.FileException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FileCleanerTest {
    @Test
    public void testFileCleanerClean() throws IOException {
        File file = mock(File.class);

        when(file.getAbsolutePath()).thenReturn("path");

        when(file.exists()).thenReturn(true);
        when(file.delete()).thenReturn(false);

        Exception exc = assertThrows(FileException.class, () -> FileCleaner.clean(file));
        assertEquals("Could not delete file: " + file.getAbsolutePath(), exc.getMessage());

        when(file.delete()).thenReturn(true);
        doThrow(new IOException()).when(file).createNewFile();

        exc = assertThrows(FileException.class, () -> FileCleaner.clean(file));
        assertEquals("Could not create new file, got IOException " + file.getAbsolutePath(), exc.getMessage());
        doReturn(false).when(file).createNewFile();

        exc = assertThrows(FileException.class, () -> FileCleaner.clean(file));
        assertEquals("Could not create new file: " + file.getAbsolutePath(), exc.getMessage());

        doReturn(true).when(file).createNewFile();
        assertDoesNotThrow(() -> FileCleaner.clean(file));
    }
}
