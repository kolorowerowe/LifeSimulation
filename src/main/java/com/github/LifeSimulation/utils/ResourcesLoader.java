package com.github.LifeSimulation.utils;

import lombok.Getter;
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

    // SIMULATION PROPERTIES
    @Getter
    private static final Integer initNumberOfSimpleEntities = Integer.parseInt(simulationProperties.getProperty("initNumberOfSimpleEntities"));
    @Getter
    private static final Integer ticksForYear = Integer.parseInt(simulationProperties.getProperty("ticksForYear"));
    @Getter
    private static final Integer maxLifeAge = Integer.parseInt(simulationProperties.getProperty("maxLifeAge"));
    @Getter
    private static final Integer middleAgeThreshold = Integer.parseInt(simulationProperties.getProperty("displayDeathTime"));
    @Getter
    private static final Integer oldAgeThreshold = Integer.parseInt(simulationProperties.getProperty("oldAgeThreshold"));
    @Getter
    private static final Integer displayDeathTime = Integer.parseInt(simulationProperties.getProperty("middleAgeThreshold"));

    //WINDOW PROPERTIES //TODO 29.05: refactor as above
    public static Integer getWindowWidth() {
        return Integer.parseInt(windowProperties.getProperty("width"));
    }

    public static Integer getWindowHeight() {
        return Integer.parseInt(windowProperties.getProperty("height"));
    }

    public static Integer getWorldWidth() {
        return Integer.parseInt(windowProperties.getProperty("worldWidth"));
    }

    public static Integer getWorldHeight() {
        return Integer.parseInt(windowProperties.getProperty("worldHeight"));
    }

    public static String getApplicationName() {
        return windowProperties.getProperty("name");
    }

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


}
