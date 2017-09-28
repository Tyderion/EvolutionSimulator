package evolution;

public class Config {
    private static final int ENERGY_DIRECTION = 1; // if 1, it'll count up how much energy is used.  if -1, it'll count down from the baseline energy, and when energy hits 0, the creature dies.
    private static final float BIG_MUTATION_CHANCE = 0.06f;
    private static final float FRICTION = 4;
    private static final boolean HAS_GROUND = true;
    private static final int[] OPERATION_AXONS = {0, 0, 0, 0, 2, 2, 2, 2, 2, 1, 1, 0};
    private static final int OPERATION_COUNT = 12;
    private static final float HAZEL_STAIRS = -1;
    private static final float PRESSURE_UNIT = 500.0f / 2.37f;
    private static final float ENERGY_UNIT = 20;
    private static final float NAUSEA_UNIT = 5;
    private static final float GRAVITY = 0.005f;
    private static final float AIR_FRICTION = 0.95f;

    public int getEnergyDirection() {
        return ENERGY_DIRECTION;
    }

    public float getBigMutationChance() {
        return BIG_MUTATION_CHANCE;
    }

    public float getFriction() {
        return FRICTION;
    }

    public boolean hasGround() {
        return HAS_GROUND;
    }

    public int[] getOperationAxons() {
        return OPERATION_AXONS;
    }

    public int getOperationCount() {
        return OPERATION_COUNT;
    }

    public float getHazelStairs() {
        return HAZEL_STAIRS;
    }

    public float getPressureUnit() {
        return PRESSURE_UNIT;
    }

    public float getEnergyUnit() {
        return ENERGY_UNIT;
    }

    public float getNauseaUnit() {
        return NAUSEA_UNIT;
    }

    public float getGravity() {
        return GRAVITY;
    }

    public float getAirFriction() {
        return AIR_FRICTION;
    }
}
