package utils;

import java.io.*;
import java.util.Properties;

/**
 * Created by Michaël Ludmann on 04/07/15.
 */
public class IniFile {
    private Properties config;

    public IniFile(String path) {
        config = new Properties();
        load(path);
    }

    private void load(String path) {
        File file = new File(path);
        try {
            InputStream fileInputStream = new FileInputStream(file);
            config.load(fileInputStream);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return config.getProperty(key);
    }

    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(config.getProperty(key, Integer.toString(defaultValue)));
    }

    public int getInt(String key) {
        return Integer.parseInt(config.getProperty(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(config.getProperty(key, Boolean.toString(defaultValue)));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(config.getProperty(key));
    }

}

