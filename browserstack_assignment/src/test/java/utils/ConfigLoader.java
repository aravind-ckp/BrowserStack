package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        // First: Load from config.properties if it exists
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {}
    }

    public static String get(String key) {
        // First check environment
        String envValue = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envValue != null) {
            return envValue;
        }
        // Then check system properties (-Dkey=value)
        String sysPropValue = System.getProperty(key);
        if (sysPropValue != null) {
            return sysPropValue;
        }
        // Last fallback: the properties file
        return properties.getProperty(key);
    }
}
