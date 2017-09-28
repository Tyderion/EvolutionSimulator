import processing.core.PApplet;

import java.util.ArrayList;

class Muscle {
    static float force;
    int axon, c1, c2;
    float len;
    float rigidity;
    float previousTarget;
    Muscle(int taxon, int tc1, int tc2, float tlen, float trigidity) {
        axon  = taxon;
        previousTarget = len = tlen;
        c1 = tc1;
        c2 = tc2;
        rigidity = trigidity;
    }

    static int getNewMuscleAxon(int nodeNum) {
        if (Simulator.rand(0, 1) < 0.5) {
            return (int) (Simulator.rand(0, nodeNum));
        } else {
            return -1;
        }
    }

    void applyForce(int i, ArrayList<Node> n) {
        float target = previousTarget;
        if(Simulator.energyDirection == 1 || Simulator.energy >= 0.0001){
            if(axon >= 0 && axon < n.size()){
                target = len* Simulator.toMuscleUsable(n.get(axon).value);
            }else{
                target = len;
            }
        }
        Node ni1 = n.get(c1);
        Node ni2 = n.get(c2);
        float distance = PApplet.dist(ni1.x, ni1.y, ni2.x, ni2.y);
        float angle = PApplet.atan2(ni1.y-ni2.y, ni1.x-ni2.x);
        force = PApplet.min(PApplet.max(1-(distance/target), -0.4f), 0.4f);
        ni1.vx += PApplet.cos(angle)* force*rigidity/ni1.m;
        ni1.vy += PApplet.sin(angle)* force*rigidity/ni1.m;
        ni2.vx -= PApplet.cos(angle)* force*rigidity/ni2.m;
        ni2.vy -= PApplet.sin(angle)* force*rigidity/ni2.m;
        Simulator.energy = PApplet.max(Simulator.energy + Simulator.energyDirection * PApplet.abs(previousTarget-target)*rigidity* Simulator.energyUnit,0);
        previousTarget = target;
    }
    Muscle copyMuscle() {
        return new Muscle(axon, c1, c2, len, rigidity);
    }
    Muscle modifyMuscle(int nodeNum, float mutability) {
        int newc1 = c1;
        int newc2 = c2;
        int newAxon = axon;
        if(Simulator.rand(0,1)< Simulator.bigMutationChance *mutability){
            newc1 = (int)(Simulator.rand(0,nodeNum));
        }
        if(Simulator.rand(0,1)< Simulator.bigMutationChance *mutability){
            newc2 = (int)(Simulator.rand(0,nodeNum));
        }
        if(Simulator.rand(0,1)< Simulator.bigMutationChance *mutability){
            newAxon = getNewMuscleAxon(nodeNum);
        }
        float newR = PApplet.min(PApplet.max(rigidity*(1+ Simulator.r()*0.9f*mutability),0.01f),0.08f);
        float newLen = PApplet.min(PApplet.max(len+ Simulator.r()*mutability,0.4f),1.25f);

        return new Muscle(newAxon, newc1, newc2, newLen, newR);
    }
}
