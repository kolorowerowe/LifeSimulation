package com.github.LifeSimulation.objects;

import com.github.LifeSimulation.core.ObjectsManager;
import com.github.LifeSimulation.enums.Gender;
import com.github.LifeSimulation.enums.LivingState;
import com.github.LifeSimulation.environment.Environment;
import com.github.LifeSimulation.environment.TerrainCell;

import java.awt.*;

import static com.github.LifeSimulation.enums.Gender.MALE;
import static com.github.LifeSimulation.enums.LivingState.*;

public class Predator extends BreedingEntity {
    private static final Color COLOR_YOUNG_MALE = new Color(255, 150, 150);
    private static final Color COLOR_ADULT_MALE = new Color(255, 70, 70);
    private static final Color COLOR_OLD_MALE = new Color(200, 0, 0);
    private static final Color COLOR_YOUNG_FEMALE = new Color(255, 255, 100);
    private static final Color COLOR_ADULT_FEMALE = new Color(255, 200, 0);
    private static final Color COLOR_OLD_FEMALE = new Color(150, 80, 30);

    // ----- Parameters - can be optimized in later stages of the project -----
    // how low the energy drops before the entity decides to eat
    private float hungryThreshold = 130.f;
    // how low the energy can fall before the entity stops looking for a partner
    private float minimumBreedingThreshold = 35.f;
    // how high the energy must be before the entity stops eating and looks for a partner to breed
    private float saturatedThreshold = 250.f;
    // how much the predator attacks the prey, which influences how fast it kills but also faster killing is less energy efficient
    private float attackValue = 10.f;

    // ----- AI State - used to make decisions -----
    boolean currentlyLookingForFood = true;
    boolean currentlyEating = false;
    int changeAngleCooldown = 0;
    Herbivore target = null;
    float targetSearchRange = BASE_SEARCH_RANGE;

    private static final float BASE_SEARCH_RANGE = 8.f;
    private static final float SEARCH_RANGE_INCREMENT = 8.f;
    private static final float SEARCH_RANGE_MAX = 80.f;

    public Predator() {
        super();
        acceleration = 0.025f;
        energyGivenToChild = 100.f;
        statistics.increaseCountOfLivingPredators();
    }

    @Override
    public BreedingEntity getOffspring(BreedingEntity other) {
        return new Predator();
    }

    @Override
    public boolean canBreedWith(BreedingEntity other) {
        return other instanceof Predator;
    }

    @Override
    public boolean doKeepLookingForPartner() {
        return energy >= minimumBreedingThreshold;
    }

    @Override
    public void nonBreedingTick(Environment environment, ObjectsManager objectsManager) {
        // try to eat
        if (currentlyLookingForFood) {
            if (random.nextInt(20) == 0) {
                class SearchData {
                    Herbivore target = null;
                    float bestSquareDistance = 1e38f;
                }

                SearchData temp = new SearchData();

                objectsManager.getSpacialIndexGrid().executeForEachInRadius(posX, posY, targetSearchRange,
                        (simulationObject, radiusSqr) -> {
                            if (simulationObject instanceof Herbivore) {
                                Herbivore ent = (Herbivore) simulationObject;
                                if (ent.livingState != DEAD) {
                                    if (radiusSqr < temp.bestSquareDistance) {
                                        temp.target = (Herbivore) simulationObject;
                                        temp.bestSquareDistance = radiusSqr;
                                    }
                                }
                            }
                        });
                target = temp.target;
                if (target == null) {
                    if (targetSearchRange < SEARCH_RANGE_MAX) {
                        targetSearchRange += SEARCH_RANGE_INCREMENT;
                    }
                }
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
        if (livingState == ALIVE_ADULT && energy > minimumBreedingThreshold && !currentlyLookingForFood) {
            setCurrentlyLookingForPartner(true);
        }
        if (target == null) {
            if (changeAngleCooldown > 0) {
                // keep current direction
                moveInDirection(getVelX(), getVelY(), acceleration);
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
        } else {
            float dx = target.posX - posX;
            float dy = target.posY - posY;
            moveInDirection(dx, dy, acceleration);
            float distSqr = dx * dx + dy * dy;
            float threshold = (radius + target.radius) * 1.5f;
            threshold *= threshold;
            if (distSqr < threshold) {
                //energy -= 0.1f * Math.pow(attackValue, 1.1);
                if (target.attack(attackValue)) {
                    energy += target.bodySize;
                    target = null;
                    targetSearchRange = SEARCH_RANGE_INCREMENT;
                    changeAngleCooldown = 0;
                }
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
            statistics.decreaseCountOfLivingPredators();
            statistics.increaseCountOfDeadPredators();
        }
        super.die();
    }
}
