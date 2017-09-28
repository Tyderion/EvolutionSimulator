import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;

class Creature {
    private Config config;
    ArrayList<Node> n;
    ArrayList<Muscle> m;
    float d;
    int id;
    boolean alive;
    float creatureTimer;
    float mutability;

    Creature(Config config, int tid, ArrayList<Node> tn, ArrayList<Muscle> tm, float td, boolean talive, float tct, float tmut) {
        this.config = config;
        id = tid;
        m = tm;
        n = tn;
        d = td;
        alive = talive;
        creatureTimer = tct;
        mutability = tmut;
    }

    Creature modified(int id) {
        Creature modifiedCreature = new Creature(config, id,
                new ArrayList<Node>(0), new ArrayList<Muscle>(0), 0, true, creatureTimer + Simulator.r() * 16 * mutability, PApplet.min(mutability * Simulator.rand(0.8f, 1.25f), 2));
        for (int i = 0; i < n.size(); i++) {
            modifiedCreature.n.add(n.get(i).modifyNode(mutability, n.size()));
        }
        for (int i = 0; i < m.size(); i++) {
            modifiedCreature.m.add(m.get(i).modifyMuscle(n.size(), mutability));
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability || n.size() <= 2) { //Add a node
            modifiedCreature.addRandomNode();
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability) { //Add a muscle
            modifiedCreature.addRandomMuscle(-1, -1);
        }
        if (Simulator.rand(0, 1) < config.getBigMutationChance() * mutability && modifiedCreature.n.size() >= 4) { //Remove a node
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

    void checkForOverlap() {
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

    void checkForLoneNodes() {
        if (n.size() >= 3) {
            for (int i = 0; i < n.size(); i++) {
                int connections = 0;
                int connectedTo = -1;
                for (int j = 0; j < m.size(); j++) {
                    if (m.get(j).c1 == i || m.get(j).c2 == i) {
                        connections++;
                        connectedTo = j;
                    }
                }
                if (connections <= 1) {
                    int newConnectionNode = PApplet.floor(Simulator.rand(0, n.size()));
                    while (newConnectionNode == i || newConnectionNode == connectedTo) {
                        newConnectionNode = PApplet.floor(Simulator.rand(0, n.size()));
                    }
                    addRandomMuscle(i, newConnectionNode);
                }
            }
        }
    }

    void checkForBadAxons() {
        for (int i = 0; i < n.size(); i++) {
            Node ni = n.get(i);
            if (ni.axon1 >= n.size()) {
                ni.axon1 = (int) (Simulator.rand(0, n.size()));
            }
            if (ni.axon2 >= n.size()) {
                ni.axon2 = (int) (Simulator.rand(0, n.size()));
            }
        }
        for (int i = 0; i < m.size(); i++) {
            Muscle mi = m.get(i);
            if (mi.axon >= n.size()) {
                mi.axon = Muscle.getNewMuscleAxon(n.size());
            }
        }

        for (int i = 0; i < n.size(); i++) {
            Node ni = n.get(i);
            ni.safeInput = (config.getOperationAxons()[ni.operation] == 0);
        }
        int iterations = 0;
        boolean didSomething = false;

        while (iterations < 1000) {
            didSomething = false;
            for (int i = 0; i < n.size(); i++) {
                Node ni = n.get(i);
                if (!ni.safeInput) {
                    if ((config.getOperationAxons()[ni.operation] == 1 && n.get(ni.axon1).safeInput) ||
                            (config.getOperationAxons()[ni.operation] == 2 && n.get(ni.axon1).safeInput && n.get(ni.axon2).safeInput)) {
                        ni.safeInput = true;
                        didSomething = true;
                    }
                }
            }
            if (!didSomething) {
                iterations = 10000;
            }
        }

        for (int i = 0; i < n.size(); i++) {
            Node ni = n.get(i);
            if (!ni.safeInput) { // This node doesn't get its input from a safe place.  CLEANSE IT.
                ni.operation = 0;
                ni.value = Simulator.rand(0, 1);
            }
        }
    }

    void addRandomNode() {
        int parentNode = PApplet.floor(Simulator.rand(0, n.size()));
        float ang1 = Simulator.rand(0, 2 * PConstants.PI);
        float distance = PApplet.sqrt(Simulator.rand(0, 1));
        float x = n.get(parentNode).x + PApplet.cos(ang1) * 0.5f * distance;
        float y = n.get(parentNode).y + PApplet.sin(ang1) * 0.5f * distance;

        int newNodeCount = n.size() + 1;

        n.add(new Node(config, x, y, 0, 0, 0.4f, Simulator.rand(0, 1), Simulator.rand(0, 1), PApplet.floor(Simulator.rand(0, config.getOperationCount())),
                PApplet.floor(Simulator.rand(0, newNodeCount)), PApplet.floor(Simulator.rand(0, newNodeCount)))); //rand(0.1,1),rand(0,1)
        int nextClosestNode = 0;
        float record = 100000;
        for (int i = 0; i < n.size() - 1; i++) {
            if (i != parentNode) {
                float dx = n.get(i).x - x;
                float dy = n.get(i).y - y;
                if (PApplet.sqrt(dx * dx + dy * dy) < record) {
                    record = PApplet.sqrt(dx * dx + dy * dy);
                    nextClosestNode = i;
                }
            }
        }
        addRandomMuscle(parentNode, n.size() - 1);
        addRandomMuscle(nextClosestNode, n.size() - 1);
    }

    void addRandomMuscle(int tc1, int tc2) {
        int axon = Muscle.getNewMuscleAxon(n.size());
        if (tc1 == -1) {
            tc1 = (int) (Simulator.rand(0, n.size()));
            tc2 = tc1;
            while (tc2 == tc1 && n.size() >= 2) {
                tc2 = (int) (Simulator.rand(0, n.size()));
            }
        }
        float len = Simulator.rand(0.5f, 1.5f);
        if (tc1 != -1) {
            len = PApplet.dist(n.get(tc1).x, n.get(tc1).y, n.get(tc2).x, n.get(tc2).y);
        }
        m.add(new Muscle(config, axon, tc1, tc2, len, Simulator.rand(0.02f, 0.08f)));
    }

    void removeRandomNode() {
        int choice = PApplet.floor(Simulator.rand(0, n.size()));
        n.remove(choice);
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

    Creature copyCreature(int newID) {
        ArrayList<Node> n2 = new ArrayList<Node>(0);
        ArrayList<Muscle> m2 = new ArrayList<Muscle>(0);
        for (int i = 0; i < n.size(); i++) {
            n2.add(this.n.get(i).copyNode());
        }
        for (int i = 0; i < m.size(); i++) {
            m2.add(this.m.get(i).copyMuscle());
        }
        if (newID == -1) {
            newID = id;
        }
        return new Creature(config, newID, n2, m2, d, alive, creatureTimer, mutability);
    }
}
