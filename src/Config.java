public class Config {
    static final int energyDirection = 1; // if 1, it'll count up how much energy is used.  if -1, it'll count down from the baseline energy, and when energy hits 0, the creature dies.
    static final float bigMutationChance = 0.06f;
    static final float FRICTION = 4;
    static final boolean haveGround = true;
    static final int[] operationAxons = {0, 0, 0, 0, 2, 2, 2, 2, 2, 1, 1, 0};
    static final int operationCount = 12;
    static final float hazelStairs = -1;
    static final float pressureUnit = 500.0f / 2.37f;
    static final float energyUnit = 20;
    static final float nauseaUnit = 5;
    static final float gravity = 0.005f;
    static final float airFriction = 0.95f;
}
