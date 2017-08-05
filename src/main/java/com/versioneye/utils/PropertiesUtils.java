package com.versioneye.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Methods to deal with properties files.
 */
public class PropertiesUtils {

    public void writeProperties(Properties properties, String filePath) throws IOException {
        File file = new File(filePath);
        OutputStream out = new FileOutputStream( file );
        properties.store(out, " Properties for https://www.VersionEye.com");
    }

    public Properties readProperties(String filePath) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream;
        File file = new File(filePath);
        inputStream = new FileInputStream( file );
        properties.load(inputStream);
        return properties;
    }
}
