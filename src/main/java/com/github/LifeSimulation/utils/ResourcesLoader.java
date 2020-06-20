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
    private static final int worldWidth = Integer.parseInt(simulationProperties.getProperty("worldWidth"));
    @Getter
    private static final int worldHeight = Integer.parseInt(simulationProperties.getProperty("worldHeight"));
    @Getter
    private static final int initNumberOfSimpleEntities = Integer.parseInt(simulationProperties.getProperty("initNumberOfSimpleEntities"));
    @Getter
    private static final int ticksForYear = Integer.parseInt(simulationProperties.getProperty("ticksForYear"));
    @Getter
    private static final int maxLifeAge = Integer.parseInt(simulationProperties.getProperty("maxLifeAge"));
    @Getter
    private static final int adultThreshold = Integer.parseInt(simulationProperties.getProperty("adultThreshold"));
    @Getter
    private static final int oldAgeThreshold = Integer.parseInt(simulationProperties.getProperty("oldAgeThreshold"));
    @Getter
    private static final int displayDeathTime = Integer.parseInt(simulationProperties.getProperty("displayDeathTime"));
    @Getter
    private static final float foodGrowthDensity = Float.parseFloat(simulationProperties.getProperty("foodGrowthDensity"));
    @Getter
    private static final float maximumFoodOnTheGround = Float.parseFloat(simulationProperties.getProperty("maximumFoodOnTheGround"));
    @Getter
    private static final float spatialIndexGridCellSize = Float.parseFloat(simulationProperties.getProperty("spatialIndexGridCellSize"));
    @Getter
    private static final float baseTickRate = Float.parseFloat(simulationProperties.getProperty("baseTickRate"));
    @Getter
    private static final int fastForwardSkipYears = Integer.parseInt(simulationProperties.getProperty("fastForwardSkipYears"));
    @Getter
    private static final String applicationName = windowProperties.getProperty("applicationName");
    @Getter
    private static final int windowWidth = Integer.parseInt(windowProperties.getProperty("windowWidth"));
    @Getter
    private static final int windowHeight = Integer.parseInt(windowProperties.getProperty("windowHeight"));
    @Getter
    private static final float fpsTarget = Float.parseFloat(windowProperties.getProperty("fpsTarget"));

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
