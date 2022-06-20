package io.github.rocketk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author pengyu
 *
 */
public class FileReader {
    public static InputStream getInputStream(String fileName) throws IOException {
        ClassLoader classLoader = FileReader.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
