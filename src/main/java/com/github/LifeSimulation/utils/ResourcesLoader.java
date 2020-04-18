package com.github.LifeSimulation.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j
public class ResourcesLoader {

    private static final String WINDOW_PROPERTIES_FILE_NAME = "window.properties";
    private static final String SIMULATION_PROPERTIES_FILE_NAME = "simulation.properties";

    private static final Properties windowProperties = loadPropertiesFromFile(WINDOW_PROPERTIES_FILE_NAME);
    private static final Properties simulationProperties = loadPropertiesFromFile(SIMULATION_PROPERTIES_FILE_NAME);

    private static Properties loadPropertiesFromFile(String fileName) {
        Properties configuration = new Properties();
        try {
            InputStream inputStream = ResourcesLoader.class
                    .getClassLoader()
                    .getResourceAsStream(fileName);
            assert inputStream != null;
            configuration.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            log.error("Couldn't read " + fileName + " file: ", e);
        }
        return configuration;
    }

    public static Integer getWindowWidth(){
        return Integer.parseInt(windowProperties.getProperty("width"));
    }

    public static Integer getWindowHeight(){
        return Integer.parseInt(windowProperties.getProperty("height"));
    }

    public static String getApplicationName(){
        return windowProperties.getProperty("name");
    }

    public static Integer getInitNumberOfSimpleDots(){
        return Integer.parseInt(simulationProperties.getProperty("initNumberOfSimpleDots"));
    }


}
