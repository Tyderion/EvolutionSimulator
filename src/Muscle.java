import processing.core.PApplet;

import java.util.ArrayList;

class Muscle {
    private MainApp mainApp;
    int axon, c1, c2;
    float len;
    float rigidity;
    float previousTarget;
    Muscle(MainApp mainApp, int taxon, int tc1, int tc2, float tlen, float trigidity) {
        this.mainApp = mainApp;
        axon  = taxon;
        previousTarget = len = tlen;
        c1 = tc1;
        c2 = tc2;
        rigidity = trigidity;
    }
    void applyForce(int i, ArrayList<Node> n) {
        float target = previousTarget;
        if(mainApp.energyDirection == 1 || mainApp.energy >= 0.0001){
            if(axon >= 0 && axon < n.size()){
                target = len* mainApp.toMuscleUsable(n.get(axon).value);
            }else{
                target = len;
            }
        }
        Node ni1 = n.get(c1);
        Node ni2 = n.get(c2);
        float distance = PApplet.dist(ni1.x, ni1.y, ni2.x, ni2.y);
        float angle = PApplet.atan2(ni1.y-ni2.y, ni1.x-ni2.x);
        mainApp.force = PApplet.min(PApplet.max(1-(distance/target), -0.4f), 0.4f);
        ni1.vx += PApplet.cos(angle)* mainApp.force*rigidity/ni1.m;
        ni1.vy += PApplet.sin(angle)* mainApp.force*rigidity/ni1.m;
        ni2.vx -= PApplet.cos(angle)* mainApp.force*rigidity/ni2.m;
        ni2.vy -= PApplet.sin(angle)* mainApp.force*rigidity/ni2.m;
        mainApp.energy = PApplet.max(mainApp.energy+ mainApp.energyDirection* PApplet.abs(previousTarget-target)*rigidity* mainApp.energyUnit,0);
        previousTarget = target;
    }
    Muscle copyMuscle() {
        return new Muscle(mainApp, axon, c1, c2, len, rigidity);
    }
    Muscle modifyMuscle(int nodeNum, float mutability) {
        int newc1 = c1;
        int newc2 = c2;
        int newAxon = axon;
        if(mainApp.random(0,1)< mainApp.bigMutationChance*mutability){
            newc1 = (int)(mainApp.random(0,nodeNum));
        }
        if(mainApp.random(0,1)< mainApp.bigMutationChance*mutability){
            newc2 = (int)(mainApp.random(0,nodeNum));
        }
        if(mainApp.random(0,1)< mainApp.bigMutationChance*mutability){
            newAxon = mainApp.getNewMuscleAxon(nodeNum);
        }
        float newR = PApplet.min(PApplet.max(rigidity*(1+ mainApp.r()*0.9f*mutability),0.01f),0.08f);
        float newLen = PApplet.min(PApplet.max(len+ mainApp.r()*mutability,0.4f),1.25f);

        return new Muscle(mainApp, newAxon, newc1, newc2, newLen, newR);
    }
}
