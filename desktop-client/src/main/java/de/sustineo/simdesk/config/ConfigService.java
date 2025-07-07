package de.sustineo.simdesk.config;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Log
@Service
public class ConfigService {
    private static final String PROPERTIES_FILE_PATH = "simdesk-client.properties";

    private final Properties properties = new Properties();

    public ConfigService() {
        try (InputStream inputStream = new FileInputStream(PROPERTIES_FILE_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            // If the properties file does not exist, we will create a new one
            persistProperties();
        }
    }

    /**
     * Retrieves a property from the configuration file.
     *
     * @param configProperty The property key
     * @return The property value, or null if the property does not exist
     */
    public String getProperty(ConfigProperty configProperty) {
        return this.properties.getProperty(configProperty.getKey());
    }

    /**
     * Retrieves a property from the configuration file, or returns a default value if the property does not exist.
     *
     * @param configProperty The property key
     * @return The property value, or the default value if the property does not exist
     */
    public String getPropertyOrDefault(ConfigProperty configProperty) {
        return this.properties.getProperty(configProperty.getKey(), configProperty.getDefaultValue());
    }

    /**
     * Sets a property in the configuration file.
     * If the property does not exist, it will be added.
     *
     * @param configProperty The property key
     * @param value          The property value
     */
    public void setProperty(ConfigProperty configProperty, String value) {
        this.properties.setProperty(configProperty.getKey(), value);
        persistProperties();
    }


    /**
     * Sets multiple properties in the configuration file.
     * If a property does not exist, it will be added.
     *
     * @param properties A map of property keys and values
     */
    public void setProperties(Map<ConfigProperty, String> properties) {
        for (Map.Entry<ConfigProperty, String> entry : properties.entrySet()) {
            this.properties.setProperty(entry.getKey().getKey(), entry.getValue());
        }
        persistProperties();
    }

    /**
     * Persists the current properties to the configuration file.
     * If the file does not exist, it will be created.
     */
    private void persistProperties() {
        try (FileOutputStream outputStream = new FileOutputStream(PROPERTIES_FILE_PATH)) {
            this.properties.store(outputStream, "SimDesk Client Properties");
        } catch (IOException e) {
            log.severe("Failed to save configuration file: " + e.getMessage());
        }
    }
}
