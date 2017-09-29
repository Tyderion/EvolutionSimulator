package evolution.parts;

import evolution.Config;
import evolution.Simulator;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

import static processing.core.PApplet.atan2;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.PI;

public class Muscle implements Drawable {
    private static float force;
    private final float scaleToFixBug;
    public int axon, c1, c2;
    public float len;
    public float rigidity;
    public float previousTarget;
    private Config config;

    public Muscle(Config config, int taxon, int tc1, int tc2, float tlen, float trigidity) {
        this.config = config;
        this.scaleToFixBug = config.getScaleToFixBug();
        axon = taxon;
        previousTarget = len = tlen;
        c1 = tc1;
        c2 = tc2;
        rigidity = trigidity;
    }

    public static int getNewMuscleAxon(int nodeNum) {
        if (Simulator.rand(0, 1) < 0.5) {
            return (int) (Simulator.rand(0, nodeNum));
        } else {
            return -1;
        }
    }

    @Override
    public void draw(ArrayList<Node> n, float x, float y, PGraphics graphics) {
        Node ni1 = n.get(c1);
        Node ni2 = n.get(c2);
        float w = 0.15f;
        if (axon >= 0 && axon < n.size()) {
            w = Simulator.toMuscleUsable(n.get(axon).value) * 0.15f;
        }
        graphics.strokeWeight(w * scaleToFixBug);
        graphics.stroke(70, 35, 0, rigidity * 3000);
        graphics.line((ni1.x + x) * scaleToFixBug, (ni1.y + y) * scaleToFixBug, (ni2.x + x) * scaleToFixBug, (ni2.y + y) * scaleToFixBug);
    }

    @Override
    public void drawAxons(ArrayList<Node> n, float x, float y, PGraphics graphics) {
        Node ni1 = n.get(c1);
        Node ni2 = n.get(c2);
        if (axon >= 0 && axon < n.size()) {
            Node axonSource = n.get(axon);
            float muscleMidX = (ni1.x + ni2.x) * 0.5f + x;
            float muscleMidY = (ni1.y + ni2.y) * 0.5f + y;
            Axon.drawSingleAxon(muscleMidX, muscleMidY, axonSource.x + x, axonSource.y + axonSource.m * 0.5f + y, graphics, scaleToFixBug, config.getAxonColor());
            float averageMass = (ni1.m + ni2.m) * 0.5f;
            graphics.fill(config.getAxonColor());
            graphics.textAlign(CENTER);
            graphics.textFont(config.getFont(), 0.4f * averageMass * scaleToFixBug);
            graphics.text(PApplet.nf(Simulator.toMuscleUsable(n.get(axon).value), 0, 2), muscleMidX * scaleToFixBug, muscleMidY * scaleToFixBug);
        }
    }

    public void applyForce(int i, ArrayList<Node> n) {
        float target = previousTarget;
        if (config.getEnergyDirection() == 1 || Simulator.energy >= 0.0001) {
            if (axon >= 0 && axon < n.size()) {
                target = len * Simulator.toMuscleUsable(n.get(axon).value);
            } else {
                target = len;
            }
        }
        Node ni1 = n.get(c1);
        Node ni2 = n.get(c2);
        float distance = PApplet.dist(ni1.x, ni1.y, ni2.x, ni2.y);
        float angle = atan2(ni1.y - ni2.y, ni1.x - ni2.x);
        force = PApplet.min(PApplet.max(1 - (distance / target), -0.4f), 0.4f);
        ni1.vx += cos(angle) * force * rigidity / ni1.m;
        ni1.vy += sin(angle) * force * rigidity / ni1.m;
        ni2.vx -= cos(angle) * force * rigidity / ni2.m;
        ni2.vy -= sin(angle) * force * rigidity / ni2.m;
        Simulator.energy = PApplet.max(Simulator.energy + config.getEnergyDirection() * PApplet.abs(previousTarget - target) * rigidity * config.getEnergyUnit(), 0);
        previousTarget = target;
    }

    public Muscle copyMuscle() {
        return new Muscle(config, axon, c1, c2, len, rigidity);
    }

    public Muscle modifyMuscle(int nodeNum, float mutability) {
        int newc1 = c1;
        int newc2 = c2;
        int newAxon = axon;
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability) {
            newc1 = (int) (Simulator.rand(0, nodeNum));
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability) {
            newc2 = (int) (Simulator.rand(0, nodeNum));
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability) {
            newAxon = getNewMuscleAxon(nodeNum);
        }
        float newR = PApplet.min(PApplet.max(rigidity * (1 + Simulator.r() * 0.9f * mutability), 0.01f), 0.08f);
        float newLen = PApplet.min(PApplet.max(len + Simulator.r() * mutability, 0.4f), 1.25f);

        return new Muscle(config, newAxon, newc1, newc2, newLen, newR);
    }
}
