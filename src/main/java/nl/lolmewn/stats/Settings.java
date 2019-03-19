package nl.lolmewn.stats;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {

    private Properties properties;

    public Settings(String localFile) {
        this.properties = new Properties();
        try (InputStream in = new FileInputStream(localFile)) {
            this.properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Settings(InputStream in) {
        this.properties = new Properties();
        try {
            this.properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public int getInt(String key, Integer def) {
        String property = getString(key);
        if (property == null) return def;
        return Integer.parseInt(property);
    }

    public float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }
}
