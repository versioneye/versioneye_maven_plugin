package versioneye;

import java.io.*;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: robertreiz
 * Date: 7/14/13
 * Time: 1:21 PM
 */
public class PropertiesUtils {

    public void writeProperties(Properties properties, String filePath) throws Exception{
        File file = new File(filePath);
        OutputStream out = new FileOutputStream( file );
        properties.store(out, " Properties for http://www.VersionEye.com");
    }

    public Properties readProperties(String filePath) throws Exception{
        Properties properties = new Properties();
        InputStream inputStream = null;
        File file = new File(filePath);
        inputStream = new FileInputStream( file );
        properties.load(inputStream);
        return properties;
    }

}
