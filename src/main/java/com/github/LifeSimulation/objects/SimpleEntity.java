package com.github.LifeSimulation.objects;

import com.github.LifeSimulation.core.ObjectsManager;
import com.github.LifeSimulation.enums.Gender;
import com.github.LifeSimulation.enums.LivingState;
import com.github.LifeSimulation.environment.Environment;
import com.github.LifeSimulation.environment.TerrainCell;
import com.github.LifeSimulation.utils.ResourcesLoader;
import lombok.Data;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import static com.github.LifeSimulation.enums.Gender.FEMALE;
import static com.github.LifeSimulation.enums.Gender.MALE;
import static com.github.LifeSimulation.enums.LivingState.*;
import static com.github.LifeSimulation.enums.LivingState.ALIVE_ADULT;
import static com.github.LifeSimulation.enums.LivingState.ALIVE_YOUNG;
import lombok.extern.log4j.Log4j;

@Data
public class SimpleEntity extends SimulationObject {
    private static final Color COLOR_YOUNG_MALE = new Color(0, 240, 200);
    private static final Color COLOR_YOUNG_FEMALE = new Color(240, 200, 0);
    private static final Color COLOR_ADULT_MALE = new Color(0, 150, 230);
    private static final Color COLOR_ADULT_FEMALE = new Color(230, 100, 0);
    private static final Color COLOR_OLD_MALE = new Color(0, 0, 220);
    private static final Color COLOR_OLD_FEMALE = new Color(220, 0, 0);
    private static final Color COLOR_DEAD = Color.BLACK;

    // ----- State of the entity -----
    // display color, updated as the entity ages and dies
    private Color color;
    // gender
    private Gender gender;
    // how long the entity had the current age
    private int ticksInCurrentAge = 0;
    // current age
    private int age = 0;
    // state of the entity (young, adult, old)
    private LivingState livingState;
    // how much time has passed since the entity died
    private int ticksSinceDied = 0;
    // current energy, when drops to 0, the entity dies
    private float energy = 20.f;

    // ----- Parameters - can be optimized in later stages of the project -----
    // how much food entity consumes by just living
    private float energyDecayRate = 0.1f;
    // how fast the entity can eat from the ground, faster eating is less efficient
    private float foodConsumptionRate = 1.f;
    // how low the energy drops before the entity decides to eat
    private float hungryThreshold = 20.f;
    // how low the energy can fall before the entity stops looking for a partner
    private float minimumBreedingThreshold = 35.f;
    // how high the energy must be before the entity stops eating and looks for a partner to breed
    private float saturatedThreshold = 60.f;
    // how much energy is used to create children, only half the energy is transferred into the child
    private float energyGivenToChild = 25.f;
    // how fast the entity can accelerate, faster moving entities lose more energy while moving and are less efficient
    private float acceleration = 0.015f;

    // for optimized drawing (avoid constantly creating new objects while drawing)
    static Ellipse2D.Float circle = new Ellipse2D.Float();
    static BasicStroke stroke = new BasicStroke(0);

    // ----- AI State - used to make decisions -----
    boolean currentlyLookingForFood = true;
    boolean currentlyEating = false;
    boolean currentlyLookingForPartner = false;
    SimpleEntity chasedEntity = null;
    int changeAngleCooldown = 0;

    public SimpleEntity() {
        super(0.3f);
        init();
    }

    public SimpleEntity(float x, float y) {
        super(0.3f, x, y);
        init();
    }

    private void init() {
        this.livingState = ALIVE_YOUNG;
        this.gender = getRandomGender();
        setColorBasedOnAgeAndGender(livingState, gender);
        super.statistics.increaseCountOfLivingObjects();
    }

    public void tick(Environment environment, ObjectsManager objectsManager) {
        super.tick(environment, objectsManager);
        if (livingState != DEAD) {
            // Get older
            if (ticksInCurrentAge >= ResourcesLoader.getTicksForYear()) {
                ticksInCurrentAge = 0;
                age++;

                if (age > ResourcesLoader.getOldAgeThreshold()) {
                    this.livingState = ALIVE_OLD;
                    // calculate propability of death only once per entity age
                    if (shouldDie(ResourcesLoader.getMaxLifeAge())) {
                        die();
                    }
                } else if (age > ResourcesLoader.getAdultThreshold()) {
                    this.livingState = ALIVE_ADULT;
                }
                setColorBasedOnAgeAndGender(livingState, gender);
            }
            ticksInCurrentAge++;
            energy -= energyDecayRate;
            if (energy <= 0) {
                die();
            }
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
            if (livingState == ALIVE_ADULT && energy > minimumBreedingThreshold && !currentlyLookingForFood) {
                currentlyLookingForPartner = true;
            }
            if (currentlyLookingForPartner) {
                // only do this with some random chance, should be enough to make things relatively responsive
                // will avoid computationally expensive searching each tick
                if (random.nextInt(10) == 0) {
                    chasedEntity = null;
                    float bestSquareDistance = 1e38f;
                    // find the closes suitable mating partner
                    for (SimulationObject obj : objectsManager.getSimulationObjectList()) {
                        if (obj instanceof SimpleEntity) {
                            SimpleEntity ent = (SimpleEntity) obj;
                            if (ent.livingState == ALIVE_ADULT && ent.currentlyLookingForPartner && ent.gender != gender) {
                                float squareDistanceToEnt = (getPosX() - ent.getPosX()) * (getPosX() - ent.getPosX()) + (getPosY() - ent.getPosY()) * (getPosY() - ent.getPosY());
                                if (squareDistanceToEnt < bestSquareDistance) {
                                    chasedEntity = ent;
                                    bestSquareDistance = squareDistanceToEnt;
                                }
                            }
                        }
                    }
                    // If they're close, they can breed
                    if (bestSquareDistance < 1.f) {
                        SimpleEntity child = new SimpleEntity();
                        child.setPosX((getPosX() + chasedEntity.getPosX()) * 0.5f);
                        child.setPosY((getPosY() + chasedEntity.getPosY()) * 0.5f);
                        child.energy = (energyGivenToChild + chasedEntity.energyGivenToChild) * 0.5f;
                        objectsManager.addObjectToSimulation(child);
                        energy -= energyGivenToChild;
                        chasedEntity.energy -= chasedEntity.energyGivenToChild;
                        currentlyLookingForPartner = false;
                        chasedEntity.currentlyLookingForPartner = false;
                        chasedEntity.chasedEntity = null;
                        chasedEntity = null;
                    }
                }
                if (energy < minimumBreedingThreshold || livingState != ALIVE_ADULT) {
                    currentlyLookingForPartner = false;
                    chasedEntity = null;
                }
            }
            if (chasedEntity != null) {
                // go towards chased entity (breeding partner)
                moveInDirection(chasedEntity.getPosX() - getPosX(), chasedEntity.getPosY() - getPosY());
            } else {
                if (changeAngleCooldown > 0) {
                    // keep current direction
                    if (!currentlyEating) {
                        moveInDirection(getVelX(), getVelY());
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
                    moveInDirection((float) Math.cos(angle), (float) Math.sin(angle));
                    changeAngleCooldown = 40 + random.nextInt(80);
                }
            }
        } else {
            ticksSinceDied++;
            if (ticksSinceDied > ResourcesLoader.getDisplayDeathTime()) {
                setShouldBeRemoved(true);
            }
        }
    }

    // Moves in the direction, normalized to entity's speed
    private void moveInDirection(float directionX, float directionY) {
        float norm = (float) Math.sqrt(directionX * directionX + directionY * directionY);
        if (norm == 0) {
            return;
        }
        float multiplier = acceleration / norm;
        setVelX(getVelX() + directionX * multiplier);
        setVelY(getVelY() + directionY * multiplier);
        energy -= Math.pow(1.f + acceleration * 10.f, 1.1f) - 1.f;
    }

    public void render(Graphics2D g2) {
        //Ellipse2D.Float circle = new Ellipse2D.Float(getPosX() - getRadius(), getPosY() - getRadius(), getRadius() * 2.f, getRadius() * 2.f);
        circle.setFrame(getPosX() - getRadius(), getPosY() - getRadius(), getRadius() * 2.f, getRadius() * 2.f);
        g2.setStroke(stroke);
        g2.setColor(color);
        g2.fill(circle);
    }

    private void setColorBasedOnAgeAndGender(LivingState livingState, Gender gender) {
        if (livingState == ALIVE_YOUNG) {
            if (gender == MALE) {
                this.color = COLOR_YOUNG_MALE;
            } else {
                this.color = COLOR_YOUNG_FEMALE;
            }
        } else if (livingState == ALIVE_ADULT) {
            if (gender == MALE) {
                this.color = COLOR_ADULT_MALE;
            } else {
                this.color = COLOR_ADULT_FEMALE;
            }
        } else if (livingState == ALIVE_OLD) {
            if (gender == MALE) {
                this.color = COLOR_OLD_MALE;
            } else {
                this.color = COLOR_OLD_FEMALE;
            }
        }
    }

    private Gender getRandomGender() {
        return super.random.nextBoolean() ? MALE : FEMALE;
    }

    private boolean shouldDie(int maxLifeAge) {
        double probabilityToDie = 1.0 / (maxLifeAge - age);
        return random.nextDouble() < probabilityToDie;
    }

    private void die() {
        livingState = DEAD;
        color = COLOR_DEAD;
        super.statistics.decreaseCountOfLivingObjects();
        super.statistics.increaseCountOfDiedObjects();
    }

}
