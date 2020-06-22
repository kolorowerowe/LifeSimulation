package com.github.LifeSimulation.objects;

import com.github.LifeSimulation.core.ObjectsManager;
import com.github.LifeSimulation.enums.Gender;
import com.github.LifeSimulation.enums.LivingState;
import com.github.LifeSimulation.environment.Environment;
import com.github.LifeSimulation.environment.TerrainCell;

import java.awt.*;

import static com.github.LifeSimulation.enums.Gender.MALE;
import static com.github.LifeSimulation.enums.LivingState.*;

public class Herbivore extends BreedingEntity {
    private static final Color COLOR_YOUNG_MALE = new Color(160, 200, 255);
    private static final Color COLOR_ADULT_MALE = new Color(100, 140, 255);
    private static final Color COLOR_OLD_MALE = new Color(0, 0, 200);
    private static final Color COLOR_YOUNG_FEMALE = new Color(240, 180, 240);
    private static final Color COLOR_ADULT_FEMALE = new Color(220, 100, 200);
    private static final Color COLOR_OLD_FEMALE = new Color(150, 0, 150);

    // ----- Parameters - can be optimized in later stages of the project -----
    // how fast the entity can eat from the ground, faster eating is less efficient
    private float foodConsumptionRate = 1.f;
    // how low the energy drops before the entity decides to eat
    private float hungryThreshold = 20.f;
    // how low the energy can fall before the entity stops looking for a partner
    private float minimumBreedingThreshold = 35.f;
    // how high the energy must be before the entity stops eating and looks for a partner to breed
    private float saturatedThreshold = 60.f;

    // ----- AI State - used to make decisions -----
    boolean currentlyLookingForFood = true;
    boolean currentlyEating = false;
    boolean currentlyRunningFromPredator = false;
    float runningFromX = 0.0f;
    float runningFromY = 0.0f;
    int changeAngleCooldown = 0;
    int predatorCheckDelay = 0;

    public Herbivore() {
        super();
        statistics.increaseCountOfLivingHerbivores();
    }

    @Override
    public BreedingEntity getOffspring(BreedingEntity other) {
        return new Herbivore();
    }

    @Override
    public boolean canBreedWith(BreedingEntity other) {
        return other instanceof Herbivore;
    }

    @Override
    public boolean doKeepLookingForPartner() {
        return energy >= minimumBreedingThreshold;
    }

    @Override
    public void nonBreedingTick(Environment environment, ObjectsManager objectsManager) {
        // try to eat
        currentlyEating = false;
        if (currentlyLookingForFood) {
            TerrainCell ground = environment.getTerrainCell((int) getPosX(), (int) getPosY());
            float foodOnTheGround = ground.getFood();
            if (foodOnTheGround > foodConsumptionRate) {
                ground.setFood(foodOnTheGround - foodConsumptionRate);
                energy += Math.pow(foodConsumptionRate + 1, 0.9) - 1; // food consumption inefficiency
                currentlyEating = true;
            }
            if (energy > saturatedThreshold) {
                currentlyLookingForFood = false;
            }
        } else {
            if (energy < hungryThreshold) {
                currentlyLookingForFood = true;
            }
        }
        // try to breed
        if (livingState == ALIVE_ADULT && energy > minimumBreedingThreshold && !currentlyLookingForFood && !currentlyRunningFromPredator) {
            setCurrentlyLookingForPartner(true);
        }
        if (changeAngleCooldown > 0) {
            // keep current direction
            if (!currentlyRunningFromPredator) {
                if (!currentlyEating) {
                    moveInDirection(getVelX(), getVelY(), acceleration);
                }
            } else {
                moveInDirection(posX - runningFromX, posY - runningFromY, acceleration);
            }
            changeAngleCooldown--;
        } else {
            float angle;
            if (getVelX() * getVelX() + getVelY() * getVelY() > 0.0001f) {
                angle = (float) Math.atan2(getVelY(), getVelX());
                angle += 0.4f * random.nextFloat() - 0.2f;
            } else {
                angle = (float) (Math.PI * 2 * random.nextFloat());
            }
            moveInDirection((float) Math.cos(angle), (float) Math.sin(angle), acceleration);
            changeAngleCooldown = 40 + random.nextInt(80);
        }
    }

    @Override
    public void tick(Environment environment, ObjectsManager objectsManager) {
        super.tick(environment, objectsManager);
        if (predatorCheckDelay > 0) {
            predatorCheckDelay--;
        } else {
            class SearchData {
                float sumX = 0;
                float sumY = 0;
                int count = 0;
            }
            SearchData temp = new SearchData();
            objectsManager.getSpacialIndexGrid().executeForEachInRadius(posX, posY, 6.0f,
                    (simulationObject, radiusSqr) -> {
                        if (simulationObject instanceof Predator) {
                            temp.sumX += simulationObject.posX;
                            temp.sumY += simulationObject.posY;
                            temp.count++;
                        }
                    });
            if (temp.count > 0) {
                currentlyRunningFromPredator = true;
                setCurrentlyLookingForPartner(false);
                runningFromX = temp.sumX / temp.count;
                runningFromY = temp.sumY / temp.count;
                predatorCheckDelay = 4 + random.nextInt(4);
            } else {
                currentlyRunningFromPredator = false;
                predatorCheckDelay = 10 + random.nextInt(10);
            }
        }
    }

    @Override
    public Color getColorBasedOnAgeAndGender(LivingState livingState, Gender gender) {
        if (livingState == ALIVE_YOUNG) {
            if (gender == MALE) {
                return COLOR_YOUNG_MALE;
            } else {
                return COLOR_YOUNG_FEMALE;
            }
        } else if (livingState == ALIVE_ADULT) {
            if (gender == MALE) {
                return COLOR_ADULT_MALE;
            } else {
                return COLOR_ADULT_FEMALE;
            }
        } else if (livingState == ALIVE_OLD) {
            if (gender == MALE) {
                return COLOR_OLD_MALE;
            } else {
                return COLOR_OLD_FEMALE;
            }
        } else {
            return Color.BLACK;
        }
    }

    @Override
    public void die() {
        if (livingState != DEAD) {
            statistics.decreaseCountOfLivingHerbivores();
            statistics.increaseCountOfDeadHerbivores();
        }
        super.die();
    }
}
