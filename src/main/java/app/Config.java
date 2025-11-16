package app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

    private static final String CONFIG_FILE = "config.properties";
    private static final String ORS_API_KEY_PROPERTY = "ORS_API_KEY";

    private static String orsApiKey;

    private Config() {
        // utility class; no instances
    }

    /**
     * Returns the OpenRouteService API key loaded from src/main/resources/config.properties.
     */
    public static String getOrsApiKey() {
        if (orsApiKey == null) {
            orsApiKey = loadOrsApiKey();
        }
        return orsApiKey;
    }

    private static String loadOrsApiKey() {
        Properties props = new Properties();

        // Load from classpath: src/main/resources/config.properties
        try (InputStream in = Config.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException("Could not find " + CONFIG_FILE + " on classpath.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load " + CONFIG_FILE, e);
        }

        String key = props.getProperty(ORS_API_KEY_PROPERTY);
        if (key == null || key.isBlank()) {
            throw new IllegalStateException(ORS_API_KEY_PROPERTY + " missing in " + CONFIG_FILE);
        }
        return key.trim();
    }
}