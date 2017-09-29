package evolution.parts;

import evolution.Config;
import evolution.Simulator;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.ArrayList;

public class Creature implements Drawable {
    private Config config;
    public ArrayList<Node> nodes;
    public ArrayList<Muscle> m;
    public float fitness;
    public int id;
    public boolean alive;
    public float mutability;

//    public Creature(Config config, int tid, ArrayList<Node> tn, ArrayList<Muscle> tm, float td, boolean talive, float tct, float tmut) {
//    public Creature(Config config, int id, ArrayList<Node> nodes, ArrayList<Muscle> muscles, boolean alive, float mutability) {
//        this(config, id, nodes, muscles, 0, alive, mutability);
//    }

    public Creature(Config config, int id, ArrayList<Node> nodes, ArrayList<Muscle> muscles, float fitness, boolean alive,float timer, float mutability) {
        this.config = config;
        this.id = id;
        this.m = muscles;
        this.nodes = nodes;
        this.fitness = fitness;
        this.alive = alive;
        this.mutability = mutability;
    }

    @Override
    public void draw(ArrayList<Node> n, float x, float y, PGraphics graphics) {
        for (Muscle aM : m) {
            aM.draw(n, x, y, graphics);
        }
        for (Node aN : n) {
            aN.draw(null, x, y, graphics);
        }
        for (Muscle aM : m) {
            aM.drawAxons(n, x, y, graphics);
        }
        for (int i = 0; i < n.size(); i++) {
            n.get(i).drawAxons(n, x, y, graphics);
        }
    }

    @Override
    public void drawAxons(ArrayList<Node> n, float x, float y, PGraphics graphics) {
        // Nothing to do
    }

    public Creature modified(int id) {
        Creature modifiedCreature = new Creature(config, id,
                new ArrayList<Node>(0), new ArrayList<Muscle>(0), 0, true, 0, PApplet.min(mutability * Simulator.rand(0.8f, 1.25f), 2));
        for (int i = 0; i < nodes.size(); i++) {
            modifiedCreature.nodes.add(nodes.get(i).modifyNode(mutability, nodes.size()));
        }
        for (int i = 0; i < m.size(); i++) {
            modifiedCreature.m.add(m.get(i).modifyMuscle(nodes.size(), mutability));
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability || nodes.size() <= 2) { //Add a node
            modifiedCreature.addRandomNode();
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability) { //Add a muscle
            modifiedCreature.addRandomMuscle(-1, -1);
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability && modifiedCreature.nodes.size() >= 4) { //Remove a node
            modifiedCreature.removeRandomNode();
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability && modifiedCreature.m.size() >= 2) { //Remove a muscle
            modifiedCreature.removeRandomMuscle();
        }
        modifiedCreature.checkForOverlap();
        modifiedCreature.checkForLoneNodes();
        modifiedCreature.checkForBadAxons();
        return modifiedCreature;
    }

    public void checkForOverlap() {
        ArrayList<Integer> bads = new ArrayList<Integer>();
        for (int i = 0; i < m.size(); i++) {
            for (int j = i + 1; j < m.size(); j++) {
                if (m.get(i).c1 == m.get(j).c1 && m.get(i).c2 == m.get(j).c2) {
                    bads.add(i);
                } else if (m.get(i).c1 == m.get(j).c2 && m.get(i).c2 == m.get(j).c1) {
                    bads.add(i);
                } else if (m.get(i).c1 == m.get(i).c2) {
                    bads.add(i);
                }
            }
        }
        for (int i = bads.size() - 1; i >= 0; i--) {
            int b = bads.get(i) + 0;
            if (b < m.size()) {
                m.remove(b);
            }
        }
    }

    public void checkForLoneNodes() {
        if (nodes.size() >= 3) {
            for (int i = 0; i < nodes.size(); i++) {
                int connections = 0;
                int connectedTo = -1;
                for (int j = 0; j < m.size(); j++) {
                    if (m.get(j).c1 == i || m.get(j).c2 == i) {
                        connections++;
                        connectedTo = j;
                    }
                }
                if (connections <= 1) {
                    int newConnectionNode = PApplet.floor(Simulator.rand(0, nodes.size()));
                    while (newConnectionNode == i || newConnectionNode == connectedTo) {
                        newConnectionNode = PApplet.floor(Simulator.rand(0, nodes.size()));
                    }
                    addRandomMuscle(i, newConnectionNode);
                }
            }
        }
    }

    public void checkForBadAxons() {
        for (int i = 0; i < nodes.size(); i++) {
            Node ni = nodes.get(i);
            if (ni.axon1 >= nodes.size()) {
                ni.axon1 = (int) (Simulator.rand(0, nodes.size()));
            }
            if (ni.axon2 >= nodes.size()) {
                ni.axon2 = (int) (Simulator.rand(0, nodes.size()));
            }
        }
        for (int i = 0; i < m.size(); i++) {
            Muscle mi = m.get(i);
            if (mi.axon >= nodes.size()) {
                mi.axon = Muscle.getNewMuscleAxon(nodes.size());
            }
        }

        for (int i = 0; i < nodes.size(); i++) {
            Node ni = nodes.get(i);
            ni.safeInput = (config.getOperationAxons()[ni.operation] == 0);
        }
        int iterations = 0;
        boolean didSomething = false;

        while (iterations < 1000) {
            didSomething = false;
            for (int i = 0; i < nodes.size(); i++) {
                Node ni = nodes.get(i);
                if (!ni.safeInput) {
                    if ((config.getOperationAxons()[ni.operation] == 1 && nodes.get(ni.axon1).safeInput) ||
                            (config.getOperationAxons()[ni.operation] == 2 && nodes.get(ni.axon1).safeInput && nodes.get(ni.axon2).safeInput)) {
                        ni.safeInput = true;
                        didSomething = true;
                    }
                }
            }
            if (!didSomething) {
                iterations = 10000;
            }
        }

        for (int i = 0; i < nodes.size(); i++) {
            Node ni = nodes.get(i);
            if (!ni.safeInput) { // This node doesn't get its input from a safe place.  CLEANSE IT.
                ni.operation = 0;
                ni.value = Simulator.rand(0, 1);
            }
        }
    }

    void addRandomNode() {
        int parentNode = PApplet.floor(Simulator.rand(0, nodes.size()));
        float ang1 = Simulator.rand(0, 2 * PConstants.PI);
        float distance = PApplet.sqrt(Simulator.rand(0, 1));
        float x = nodes.get(parentNode).x + PApplet.cos(ang1) * 0.5f * distance;
        float y = nodes.get(parentNode).y + PApplet.sin(ang1) * 0.5f * distance;

        int newNodeCount = nodes.size() + 1;

        nodes.add(new Node(config, x, y, 0, 0, 0.4f, Simulator.rand(0, 1), Simulator.rand(0, 1), PApplet.floor(Simulator.rand(0, config.getOperationCount())),
                PApplet.floor(Simulator.rand(0, newNodeCount)), PApplet.floor(Simulator.rand(0, newNodeCount)))); //rand(0.1,1),rand(0,1)
        int nextClosestNode = 0;
        float record = 100000;
        for (int i = 0; i < nodes.size() - 1; i++) {
            if (i != parentNode) {
                float dx = nodes.get(i).x - x;
                float dy = nodes.get(i).y - y;
                if (PApplet.sqrt(dx * dx + dy * dy) < record) {
                    record = PApplet.sqrt(dx * dx + dy * dy);
                    nextClosestNode = i;
                }
            }
        }
        addRandomMuscle(parentNode, nodes.size() - 1);
        addRandomMuscle(nextClosestNode, nodes.size() - 1);
    }

    void addRandomMuscle(int tc1, int tc2) {
        int axon = Muscle.getNewMuscleAxon(nodes.size());
        if (tc1 == -1) {
            tc1 = (int) (Simulator.rand(0, nodes.size()));
            tc2 = tc1;
            while (tc2 == tc1 && nodes.size() >= 2) {
                tc2 = (int) (Simulator.rand(0, nodes.size()));
            }
        }
        float len = Simulator.rand(0.5f, 1.5f);
        if (tc1 != -1) {
            len = PApplet.dist(nodes.get(tc1).x, nodes.get(tc1).y, nodes.get(tc2).x, nodes.get(tc2).y);
        }
        m.add(new Muscle(config, axon, tc1, tc2, len, Simulator.rand(0.02f, 0.08f)));
    }

    void removeRandomNode() {
        int choice = PApplet.floor(Simulator.rand(0, nodes.size()));
        nodes.remove(choice);
        int i = 0;
        while (i < m.size()) {
            if (m.get(i).c1 == choice || m.get(i).c2 == choice) {
                m.remove(i);
            } else {
                i++;
            }
        }
        for (int j = 0; j < m.size(); j++) {
            if (m.get(j).c1 >= choice) {
                m.get(j).c1--;
            }
            if (m.get(j).c2 >= choice) {
                m.get(j).c2--;
            }
        }
    }

    void removeRandomMuscle() {
        int choice = PApplet.floor(Simulator.rand(0, m.size()));
        m.remove(choice);
    }

    public Creature copyCreature(int newID) {
        ArrayList<Node> n2 = new ArrayList<Node>(0);
        ArrayList<Muscle> m2 = new ArrayList<Muscle>(0);
        for (int i = 0; i < nodes.size(); i++) {
            n2.add(this.nodes.get(i).copyNode());
        }
        for (int i = 0; i < m.size(); i++) {
            m2.add(this.m.get(i).copyMuscle());
        }
        if (newID == -1) {
            newID = id;
        }
        return new Creature(config, newID, n2, m2, fitness, alive, 0, mutability);
    }
}
