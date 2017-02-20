package com.versioneye.utils;

import java.io.*;
import java.util.Properties;

/**
 * Methods to deal with properties files.
 */
public class PropertiesUtils {

    public void writeProperties(Properties properties, String filePath) throws Exception{
        File file = new File(filePath);
        OutputStream out = new FileOutputStream( file );
        properties.store(out, " Properties for https://www.VersionEye.com");
    }

    public Properties readProperties(String filePath) throws Exception{
        Properties properties = new Properties();
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream( file );
        properties.load(inputStream);
        return properties;
    }

}
