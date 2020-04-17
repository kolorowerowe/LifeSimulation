package com.github.LifeSimulation.utils;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j
@NoArgsConstructor
public class ResourcesLoader {

    private static final String WINDOW_PROPERTIES_FILE_NAME = "window.properties";

    public static Properties loadWindowProperties() {

        Properties configuration = new Properties();

        try {
            InputStream inputStream = ResourcesLoader.class
                    .getClassLoader()
                    .getResourceAsStream(WINDOW_PROPERTIES_FILE_NAME);
            assert inputStream != null;

            configuration.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
           log.error("Couldn't read window.properties file: ", e);
        }

        return configuration;
    }
}
