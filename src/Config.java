public class Config {
    private static final int energyDirection = 1; // if 1, it'll count up how much energy is used.  if -1, it'll count down from the baseline energy, and when energy hits 0, the creature dies.
    private static final float bigMutationChance = 0.06f;
    private static final float FRICTION = 4;
    private static final boolean haveGround = true;
    private static final int[] operationAxons = {0, 0, 0, 0, 2, 2, 2, 2, 2, 1, 1, 0};
    private static final int operationCount = 12;
    private static final float hazelStairs = -1;
    private static final float pressureUnit = 500.0f / 2.37f;
    private static final float energyUnit = 20;
    private static final float nauseaUnit = 5;
    private static final float gravity = 0.005f;
    private static final float airFriction = 0.95f;

    public int getEnergyDirection() {
        return energyDirection;
    }

    public float getBigMutationChance() {
        return bigMutationChance;
    }

    public float getFRICTION() {
        return FRICTION;
    }

    public boolean hasGround() {
        return haveGround;
    }

    public int[] getOperationAxons() {
        return operationAxons;
    }

    public int getOperationCount() {
        return operationCount;
    }

    public float getHazelStairs() {
        return hazelStairs;
    }

    public float getPressureUnit() {
        return pressureUnit;
    }

    public float getEnergyUnit() {
        return energyUnit;
    }

    public float getNauseaUnit() {
        return nauseaUnit;
    }

    public float getGravity() {
        return gravity;
    }

    public float getAirFriction() {
        return airFriction;
    }
}
