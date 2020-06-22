package com.github.LifeSimulation.objects;

import com.github.LifeSimulation.core.ObjectsManager;
import com.github.LifeSimulation.enums.Gender;
import com.github.LifeSimulation.enums.LivingState;
import com.github.LifeSimulation.environment.Environment;
import com.github.LifeSimulation.utils.ResourcesLoader;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import static com.github.LifeSimulation.enums.Gender.FEMALE;
import static com.github.LifeSimulation.enums.Gender.MALE;
import static com.github.LifeSimulation.enums.LivingState.*;
import static com.github.LifeSimulation.enums.LivingState.ALIVE_ADULT;
import static com.github.LifeSimulation.enums.LivingState.ALIVE_YOUNG;

@Log4j
@Data
abstract public class BreedingEntity extends SimulationObject {

    // ----- State of the entity -----
    // display color, updated as the entity ages and dies
    protected Color color;
    // gender
    protected Gender gender;
    // how long the entity had the current age
    protected int ticksInCurrentAge = 0;
    // current age
    protected int age = 0;
    // state of the entity (young, adult, old)
    protected LivingState livingState;
    // how much time has passed since the entity died
    protected int ticksSinceDied = 0;
    // current energy, when drops to 0, the entity dies
    protected float energy = 20.f;
    // body integrity, determines how damaged the entity is
    protected float bodyIntegrity = 400.f;

    // ----- Parameters - can be optimized in later stages of the project -----
    // how much food entity consumes by just living
    protected float energyDecayRate = 0.1f;
    // how fast the entity can accelerate, faster moving entities lose more energy while moving and are less efficient
    protected float acceleration = 0.015f;
    // how much energy is used to create children, only half the energy is transferred into the child
    protected float energyGivenToChild = 25.f;
    // body size, determines maximum body integrity, how much food is in the entity and display radius
    protected float bodySize = 400.f;

    private boolean currentlyLookingForPartner = false;
    private BreedingEntity chasedPartner = null;
    private float partnerSearchRange = BASE_SEARCH_RANGE;

    private static final float BASE_SEARCH_RANGE = 8.f;
    private static final float SEARCH_RANGE_INCREMENT = 8.f;
    private static final float SEARCH_RANGE_MAX = 80.f;
    private static final float HEALING_RATE = 0.001f;

    // for optimized drawing (avoid constantly creating new objects while drawing)
    static Ellipse2D.Float circle = new Ellipse2D.Float();
    static BasicStroke stroke = new BasicStroke(0);

    public BreedingEntity() {
        super(0.3f);
        init();
    }

    public BreedingEntity(float x, float y) {
        super(0.3f, x, y);
        init();
    }

    abstract public Color getColorBasedOnAgeAndGender(LivingState livingState, Gender gender);

    abstract public BreedingEntity getOffspring(BreedingEntity other);

    abstract public boolean canBreedWith(BreedingEntity other);

    abstract public boolean doKeepLookingForPartner();

    abstract public void nonBreedingTick(Environment environment, ObjectsManager objectsManager);

    // Attack the entity by the value, subtracts the body integrity, kills if  necessary, returns true if killed
    public boolean attack(float value) {
        bodyIntegrity -= value;
        if (bodyIntegrity <= 0f) {
            die();
            return true;
        } else {
            return false;
        }
    }

    // Moves in the direction, normalized to entity's speed
    public void moveInDirection(float directionX, float directionY, float acceleration) {
        float norm = (float) Math.sqrt(directionX * directionX + directionY * directionY);
        if (norm == 0) {
            return;
        }
        float multiplier = acceleration / norm;
        setVelX(getVelX() + directionX * multiplier);
        setVelY(getVelY() + directionY * multiplier);
        energy -= Math.pow(1.f + acceleration * 10.f, 1.1f) - 1.f;
    }

    @Override
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
                this.color = getColorBasedOnAgeAndGender(livingState, gender);
            }
            ticksInCurrentAge++;
            energy -= energyDecayRate;
            if (energy <= 0) {
                die();
            }
            if (bodyIntegrity < bodySize) {
                bodyIntegrity += HEALING_RATE * bodySize;
                if (bodyIntegrity > bodySize) {
                    bodyIntegrity = bodySize;
                }
            }
            if (currentlyLookingForPartner) {
                // only do this with some random chance, should be enough to make things relatively responsive
                // will avoid computationally expensive searching each tick
                if (random.nextInt(20) == 0) {
                    class SearchData {
                        BreedingEntity chasedPartner = null;
                        float bestSquareDistance = 1e38f;
                    }

                    SearchData temp = new SearchData();

                    objectsManager.getSpacialIndexGrid().executeForEachInRadius(posX, posY, partnerSearchRange,
                            (simulationObject, radiusSqr) -> {
                                BreedingEntity ent = (BreedingEntity) simulationObject;
                                if (ent.livingState == ALIVE_ADULT && ent.gender != gender && ent.currentlyLookingForPartner && canBreedWith(ent)) {
                                    if (radiusSqr < temp.bestSquareDistance) {
                                        temp.chasedPartner = ent;
                                        temp.bestSquareDistance = radiusSqr;
                                    }
                                }
                            });

                    chasedPartner = temp.chasedPartner;
                    if (chasedPartner != null) {
                        // If they're close, they can breed
                        if (temp.bestSquareDistance < 1.5f * (radius + chasedPartner.radius)) {
                            BreedingEntity child = getOffspring(chasedPartner);
                            child.setPosX((getPosX() + chasedPartner.getPosX()) * 0.5f);
                            child.setPosY((getPosY() + chasedPartner.getPosY()) * 0.5f);
                            child.energy = (energyGivenToChild + chasedPartner.energyGivenToChild) * 0.5f;
                            objectsManager.addObjectToSimulation(child);
                            energy -= energyGivenToChild;
                            chasedPartner.energy -= chasedPartner.energyGivenToChild;
                            chasedPartner.currentlyLookingForPartner = false;
                            chasedPartner.partnerSearchRange = BASE_SEARCH_RANGE;
                            currentlyLookingForPartner = false;
                            partnerSearchRange = BASE_SEARCH_RANGE;
                        }
                    } else {
                        if (partnerSearchRange < SEARCH_RANGE_MAX) {
                            partnerSearchRange += SEARCH_RANGE_INCREMENT;
                        }
                    }
                }
                if (livingState != ALIVE_ADULT || !doKeepLookingForPartner()) {
                    currentlyLookingForPartner = false;
                    partnerSearchRange = BASE_SEARCH_RANGE;
                }
            } else {
                chasedPartner = null;
            }
            if (chasedPartner != null) {
                // go towards chased entity (breeding partner)
                moveInDirection(chasedPartner.getPosX() - getPosX(), chasedPartner.getPosY() - getPosY(), acceleration);
            } else {
                nonBreedingTick(environment, objectsManager);
            }
        } else {
            ticksSinceDied++;
            if (ticksSinceDied > ResourcesLoader.getDisplayDeathTime()) {
                setShouldBeRemoved(true);
            }
        }
    }

    @Override
    public void render(Graphics2D g2) {
        //Ellipse2D.Float circle = new Ellipse2D.Float(getPosX() - getRadius(), getPosY() - getRadius(), getRadius() * 2.f, getRadius() * 2.f);
        circle.setFrame(getPosX() - getRadius(), getPosY() - getRadius(), getRadius() * 2.f, getRadius() * 2.f);
        g2.setStroke(stroke);
        g2.setColor(color);
        g2.fill(circle);
    }

    public void die() {
        if (livingState != DEAD) {
            livingState = DEAD;
            color = getColorBasedOnAgeAndGender(livingState, gender);
            super.statistics.decreaseCountOfLivingObjects();
            super.statistics.increaseCountOfDeadObjects();
        }
    }

    private void init() {
        this.livingState = ALIVE_YOUNG;
        this.gender = getRandomGender();
        color = getColorBasedOnAgeAndGender(livingState, gender);
        super.statistics.increaseCountOfLivingObjects();
    }

    private Gender getRandomGender() {
        return super.random.nextBoolean() ? MALE : FEMALE;
    }

    private boolean shouldDie(int maxLifeAge) {
        double probabilityToDie = 1.0 / (maxLifeAge - age);
        return random.nextDouble() < probabilityToDie;
    }

}
