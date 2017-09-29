package evolution;

import evolution.parts.*;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.MouseEvent;

import java.util.ArrayList;

public class Simulator extends PApplet {
    public static ArrayList<Rectangle> rects = new ArrayList<Rectangle>(0);
    public static float energy = 0;
    public static float totalNodeNausea = 0;
    public static int simulationTimer = 0;
    static Simulator self;
    final float windowSizeMultiplier = 0.8f;
    final int SEED = 0;
    private final Config config;
    PFont font;
    ArrayList<Float[]> percentile = new ArrayList<Float[]>(0);
    ArrayList<Integer[]> barCounts = new ArrayList<Integer[]>(0);
    ArrayList<Integer[]> speciesCounts = new ArrayList<Integer[]>(0);
    ArrayList<Integer> topSpeciesCounts = new ArrayList<Integer>(0);
    ArrayList<Creature> creatureDatabase = new ArrayList<Creature>(0);
    //    PGraphics graphImage;
//    PGraphics screenImage;
//    PGraphics popUpImage;
//    PGraphics segBarImage;
    int histBarsPerMeter = 5;
    String[] operationNames = {"#", "time", "px", "py", "+", "-", "*", "÷", "%", "sin", "sig", "pres"};
    String fitnessUnit = "m";
    String fitnessName = "Distance";
    float baselineEnergy = 0.0f;
    boolean saveFramesPerGeneration = true;
    int lastImageSaved = -1;
    int minBar = -10;
    int maxBar = 100;
    int barLen = maxBar - minBar;
    int gensToDo = 0;
    float cTimer = 60;
    float postFontSize = 0.96f;
    float scaleToFixBug = 1000;
    float averageNodeNausea = 0;
    float lineY1 = -0.08f; // These are for the lines of text on each node.
    float lineY2 = 0.35f;
    int axonColor = color(255, 255, 0);
    int windowWidth = 1280;
    int windowHeight = 720;
    int timer = 0;
    float camX = 0;
    float camY = 0;
    int frames = 60;
    Menu menu = Menu.START_SCREEN;
    int gen = -1;
    float sliderX = 1170;
    int genSelected = 0;
    boolean drag = false;
    boolean justGotBack = false;
    int creatures = 0;
    int creaturesTested = 0;
    int fontSize = 0;
    int[] fontSizes = {
            50, 36, 25, 20, 16, 14, 11, 9
    };
    int statusWindow = -4;
    int prevStatusWindow = -4;
    int overallTimer = 0;
    boolean miniSimulation = false;
    int creatureWatching = 0;
    int[] creaturesInPosition = new int[1000];
    float camZoom = 0.015f;
    float target;
    float averageX;
    float averageY;
    int speed;
    int id;
    boolean stepbystep;
    boolean stepbystepslow;
    boolean slowDies;
    int timeShow;
    int[] p = {
            0, 10, 20, 30, 40, 50, 60, 70, 80, 90,
            100, 200, 300, 400, 500, 600, 700, 800, 900, 910, 920, 930, 940, 950, 960, 970, 980, 990, 999
    };
    ArrayList<Node> n = new ArrayList<Node>();
    ArrayList<Muscle> m = new ArrayList<Muscle>();
    Creature[] c = new Creature[1000];
    ArrayList<Creature> c2 = new ArrayList<Creature>();
    private Simulation simulation;
    private Ground ground;

    public Simulator() {
        self = this;
        config = new Config();
    }

    public static void main(String[] args) {
        PApplet.main(Simulator.class.getCanonicalName(), args);
    }

    public static float rand(float l, float h) {
        return self.random(l, h);
    }

    public static float toMuscleUsable(float f) {
        return min(max(f, 0.5f), 1.5f);
    }

    public static float r() {
        return pow(rand(-1, 1), 19);
    }

    float inter(int a, int b, float offset) {
        return ((float) a) + ((float) b - (float) a) * offset;
    }

    void drawGround(int toImage) {
        if (toImage == 0) {
            ground.draw(averageX, averageY, 800, 1600, 900, camX, camZoom, this.g);
        } else if (toImage == 2) {
            ground.draw(averageX, averageY, 300, 600, 600, camX, camZoom, simulation.popUpImage);
        }
    }

    void draw(Drawable drawable, ArrayList<Node> n, float x, float y, int toImage) {
        if (toImage == 0) {
            drawable.draw(n, x, y, this.g);
        } else if (toImage == 1) {
            drawable.draw(n, x, y, simulation.screenImage);
        } else if (toImage == 2) {
            drawable.draw(n, x, y, simulation.popUpImage);
        }
    }

    void drawPosts(int toImage) {
        int startPostY = min(-8, (int) (averageY / 4) * 4 - 4);
        if (toImage == 0) {
            noStroke();
            textAlign(CENTER);
            textFont(font, postFontSize * scaleToFixBug);
            for (int postY = startPostY; postY <= startPostY + 8; postY += 4) {
                for (int i = (int) (averageX / 5 - 5); i <= (int) (averageX / 5 + 5); i++) {
                    fill(255);
                    rect((i * 5.0f - 0.1f) * scaleToFixBug, (-3.0f + postY) * scaleToFixBug, 0.2f * scaleToFixBug, 3.0f * scaleToFixBug);
                    rect((i * 5.0f - 1) * scaleToFixBug, (-3.0f + postY) * scaleToFixBug, 2.0f * scaleToFixBug, 1.0f * scaleToFixBug);
                    fill(120);
                    textAlign(CENTER);
                    text(i + " m", i * 5.0f * scaleToFixBug, (-2.17f + postY) * scaleToFixBug);
                }
            }
        } else if (toImage == 2) {
            simulation.popUpImage.textAlign(CENTER);
            simulation.popUpImage.textFont(font, postFontSize * scaleToFixBug);
            simulation.popUpImage.noStroke();
            for (int postY = startPostY; postY <= startPostY + 8; postY += 4) {
                for (int i = (int) (averageX / 5 - 5); i <= (int) (averageX / 5 + 5); i++) {
                    simulation.popUpImage.fill(255);
                    simulation.popUpImage.rect((i * 5 - 0.1f) * scaleToFixBug, (-3.0f + postY) * scaleToFixBug, 0.2f * scaleToFixBug, 3 * scaleToFixBug);
                    simulation.popUpImage.rect((i * 5 - 1) * scaleToFixBug, (-3.0f + postY) * scaleToFixBug, 2 * scaleToFixBug, 1 * scaleToFixBug);
                    simulation.popUpImage.fill(120);
                    simulation.popUpImage.text(i + " m", i * 5 * scaleToFixBug, (-2.17f + postY) * scaleToFixBug);
                }
            }
        }
    }

    void drawArrow(float x) {
        textAlign(CENTER);
        textFont(font, postFontSize * scaleToFixBug);
        noStroke();
        fill(120, 0, 255);
        rect((x - 1.7f) * scaleToFixBug, -4.8f * scaleToFixBug, 3.4f * scaleToFixBug, 1.1f * scaleToFixBug);
        beginShape();
        vertex(x * scaleToFixBug, -3.2f * scaleToFixBug);
        vertex((x - 0.5f) * scaleToFixBug, -3.7f * scaleToFixBug);
        vertex((x + 0.5f) * scaleToFixBug, -3.7f * scaleToFixBug);
        endShape(CLOSE);
        fill(255);
        text(((float) (round(x * 2)) / 10) + " m", x * scaleToFixBug, -3.91f * scaleToFixBug);
    }

    void drawGraphImage() {
        image(simulation.graphImage, 50, 180, 650, 380);
        image(simulation.segBarImage, 50, 580, 650, 100);
        if (gen >= 1) {
            stroke(0, 160, 0, 255);
            strokeWeight(3);
            float genWidth = 590.0f / gen;
            float lineX = 110 + genSelected * genWidth;
            line(lineX, 180, lineX, 500 + 180);
            Integer[] s = speciesCounts.get(genSelected);
            textAlign(LEFT);
            textFont(font, 12);
            noStroke();
            for (int i = 1; i < 101; i++) {
                int c = s[i] - s[i - 1];
                if (c >= 25) {
                    float y = ((s[i] + s[i - 1]) / 2) / 1000.0f * 100 + 573;
                    if (i - 1 == topSpeciesCounts.get(genSelected)) {
                        stroke(0);
                        strokeWeight(2);
                    } else {
                        noStroke();
                    }
                    fill(255, 255, 255);
                    rect(lineX + 3, y, 56, 14);
                    colorMode(HSB, 1.0f);
                    fill(getColor(i - 1, true));
                    text("S" + floor((i - 1) / 10) + "" + ((i - 1) % 10) + ": " + c, lineX + 5, y + 11);
                    colorMode(RGB, 255);
                }
            }
            noStroke();
        }
    }

    int getColor(int i, boolean adjust) {
        colorMode(HSB, 1.0f);
        float col = (i * 1.618034f) % 1;
        if (i == 46) {
            col = 0.083333f;
        }
        float light = 1.0f;
        if (abs(col - 0.333f) <= 0.18f && adjust) {
            light = 0.7f;
        }
        return color(col, 1.0f, light);
    }

    void drawGraph(int graphWidth, int graphHeight) {
        simulation.graphImage.beginDraw();
        simulation.graphImage.smooth();
        simulation.graphImage.background(220);
        if (gen >= 1) {
            drawLines(90, (int) (graphHeight * 0.05), graphWidth - 90, (int) (graphHeight * 0.9));
            drawSegBars(90, 0, graphWidth - 90, 150);
        }
        simulation.graphImage.endDraw();
    }

    void drawLines(int x, int y, int graphWidth, int graphHeight) {
        float gh = (float) (graphHeight);
        float genWidth = (float) (graphWidth) / gen;
        float best = extreme(1);
        float worst = extreme(-1);
        float meterHeight = (float) (graphHeight) / (best - worst);
        float zero = (best / (best - worst)) * gh;
        float unit = setUnit(best, worst);
        simulation.graphImage.stroke(150);
        simulation.graphImage.strokeWeight(2);
        simulation.graphImage.fill(150);
        simulation.graphImage.textFont(font, 18);
        simulation.graphImage.textAlign(RIGHT);
        for (float i = ceil((worst - (best - worst) / 18.0f) / unit) * unit; i < best + (best - worst) / 18.0f; i += unit) {
            float lineY = y - i * meterHeight + zero;
            simulation.graphImage.line(x, lineY, graphWidth + x, lineY);
            simulation.graphImage.text(showUnit(i, unit) + " " + fitnessUnit, x - 5, lineY + 4);
        }
        simulation.graphImage.stroke(0);
        for (int i = 0; i < 29; i++) {
            int k;
            if (i == 28) {
                k = 14;
            } else if (i < 14) {
                k = i;
            } else {
                k = i + 1;
            }
            if (k == 14) {
                simulation.graphImage.stroke(255, 0, 0, 255);
                simulation.graphImage.strokeWeight(5);
            } else {
                stroke(0);
                if (k == 0 || k == 28 || (k >= 10 && k <= 18)) {
                    simulation.graphImage.strokeWeight(3);
                } else {
                    simulation.graphImage.strokeWeight(1);
                }
            }
            for (int j = 0; j < gen; j++) {
                simulation.graphImage.line(x + j * genWidth, (-percentile.get(j)[k]) * meterHeight + zero + y,
                        x + (j + 1) * genWidth, (-percentile.get(j + 1)[k]) * meterHeight + zero + y);
            }
        }
    }

    void drawSegBars(int x, int y, int graphWidth, int graphHeight) {
        simulation.segBarImage.beginDraw();
        simulation.segBarImage.smooth();
        simulation.segBarImage.noStroke();
        simulation.segBarImage.colorMode(HSB, 1);
        simulation.segBarImage.background(0, 0, 0.5f);
        float genWidth = (float) (graphWidth) / gen;
        int gensPerBar = floor(gen / 500) + 1;
        for (int i = 0; i < gen; i += gensPerBar) {
            int i2 = min(i + gensPerBar, gen);
            float barX1 = x + i * genWidth;
            float barX2 = x + i2 * genWidth;
            int cum = 0;
            for (int j = 0; j < 100; j++) {
                simulation.segBarImage.fill(getColor(j, false));
                simulation.segBarImage.beginShape();
                simulation.segBarImage.vertex(barX1, y + speciesCounts.get(i)[j] / 1000.0f * graphHeight);
                simulation.segBarImage.vertex(barX1, y + speciesCounts.get(i)[j + 1] / 1000.0f * graphHeight);
                simulation.segBarImage.vertex(barX2, y + speciesCounts.get(i2)[j + 1] / 1000.0f * graphHeight);
                simulation.segBarImage.vertex(barX2, y + speciesCounts.get(i2)[j] / 1000.0f * graphHeight);
                simulation.segBarImage.endShape();
            }
        }
        simulation.segBarImage.endDraw();
        colorMode(RGB, 255);
    }

    float extreme(float sign) {
        float record = -sign;
        for (int i = 0; i < gen; i++) {
            float toTest = percentile.get(i + 1)[(int) (14 - sign * 14)];
            if (toTest * sign > record * sign) {
                record = toTest;
            }
        }
        return record;
    }

    float setUnit(float best, float worst) {
        float unit2 = 3 * log(best - worst) / log(10) - 2;
        if ((unit2 + 90) % 3 < 1) {
            return pow(10, floor(unit2 / 3));
        } else if ((unit2 + 90) % 3 < 2) {
            return pow(10, floor((unit2 - 1) / 3)) * 2;
        } else {
            return pow(10, floor((unit2 - 2) / 3)) * 5;
        }
    }

    String showUnit(float i, float unit) {
        if (unit < 1) {
            return nf(i, 0, 2) + "";
        } else {
            return (int) (i) + "";
        }
    }

    ArrayList<Creature> quickSort(ArrayList<Creature> c) {
        if (c.size() <= 1) {
            return c;
        } else {
            ArrayList<Creature> less = new ArrayList<Creature>();
            ArrayList<Creature> more = new ArrayList<Creature>();
            ArrayList<Creature> equal = new ArrayList<Creature>();
            Creature c0 = c.get(0);
            equal.add(c0);
            for (int i = 1; i < c.size(); i++) {
                Creature ci = c.get(i);
                if (ci.fitness == c0.fitness) {
                    equal.add(ci);
                } else if (ci.fitness < c0.fitness) {
                    less.add(ci);
                } else {
                    more.add(ci);
                }
            }
            ArrayList<Creature> total = new ArrayList<Creature>();
            total.addAll(quickSort(more));
            total.addAll(equal);
            total.addAll(quickSort(less));
            return total;
        }
    }

    void toStableConfiguration(int nodeNum, int muscleNum) {
        for (int j = 0; j < 200; j++) {
            for (int i = 0; i < muscleNum; i++) {
                m.get(i).applyForce(i, n);
            }
            for (int i = 0; i < nodeNum; i++) {
                n.get(i).applyForces();
            }
        }
        for (int i = 0; i < nodeNum; i++) {
            Node ni = n.get(i);
            ni.vx = 0;
            ni.vy = 0;
        }
    }

    void adjustToCenter(int nodeNum) {
        float avx = 0;
        float lowY = -1000;
        for (int i = 0; i < nodeNum; i++) {
            Node ni = n.get(i);
            avx += ni.x;
            if (ni.y + ni.m / 2 > lowY) {
                lowY = ni.y + ni.m / 2;
            }
        }
        avx /= nodeNum;
        for (int i = 0; i < nodeNum; i++) {
            Node ni = n.get(i);
            ni.x -= avx;
            ni.y -= lowY;
        }
    }

    void simulate() {
        for (int i = 0; i < m.size(); i++) {
            m.get(i).applyForce(i, n);
        }
        for (int i = 0; i < n.size(); i++) {
            Node ni = n.get(i);
            ni.applyGravity();
            ni.applyForces();
            ni.hitWalls();
            ni.doMath(i, n);
        }
        for (int i = 0; i < n.size(); i++) {
            n.get(i).realizeMathValues(i);
        }
        averageNodeNausea = totalNodeNausea / n.size();
        simulationTimer++;
        timer++;
    }

    void setAverages() {
        averageX = 0;
        averageY = 0;
        for (int i = 0; i < n.size(); i++) {
            Node ni = n.get(i);
            averageX += ni.x;
            averageY += ni.y;
        }
        averageX = averageX / n.size();
        averageY = averageY / n.size();
    }

    public void mouseWheel(MouseEvent event) {
        float delta = event.getCount();
        if (menu == Menu.SIMULATION_RUNNING) {
            if (delta == -1) {
                camZoom *= 0.9090909;
                if (camZoom < 0.002) {
                    camZoom = 0.002f;
                }
                textFont(font, postFontSize);
            } else if (delta == 1) {
                camZoom *= 1.1;
                if (camZoom > 0.1) {
                    camZoom = 0.1f;
                }
                textFont(font, postFontSize);
            }
        }
    }

    public void mousePressed() {
        if (gensToDo >= 1) {
            gensToDo = 0;
        }
        float mX = mouseX / windowSizeMultiplier;
        float mY = mouseY / windowSizeMultiplier;
        if (menu == Menu.MAIN_SCREEN && gen >= 1 && abs(mY - 365) <= 25 && abs(mX - sliderX - 25) <= 25) {
            drag = true;
        }
    }

    void openMiniSimulation() {
        simulationTimer = 0;
        if (gensToDo == 0) {
            miniSimulation = true;
            int id;
            Creature cj;
            if (statusWindow <= -1) {
                cj = creatureDatabase.get((genSelected - 1) * 3 + statusWindow + 3);
                id = cj.id;
            } else {
                id = statusWindow;
                cj = c2.get(id);
            }
            setGlobalVariables(cj);
            creatureWatching = id;
        }
    }

    void setMenu(Menu m) {
        menu = m;
        if (m == Menu.MAIN_SCREEN) {
            drawGraph(975, 570);
        }
    }

    String zeros(int n, int zeros) {
        String s = n + "";
        for (int i = s.length(); i < zeros; i++) {
            s = "0" + s;
        }
        return s;
    }

    void startASAP() {
        setMenu(Menu.START_SIMULATION);
        creaturesTested = 0;
        stepbystep = false;
        stepbystepslow = false;
    }

    public void mouseReleased() {
        drag = false;
        miniSimulation = false;
        float mX = mouseX / windowSizeMultiplier;
        float mY = mouseY / windowSizeMultiplier;
        if (menu == Menu.START_SCREEN && abs(mX - windowWidth / 2) <= 200 && abs(mY - 400) <= 100) {
            setMenu(Menu.MAIN_SCREEN);
        } else if (menu == Menu.MAIN_SCREEN && gen == -1 && abs(mX - 120) <= 100 && abs(mY - 300) <= 50) {
            setMenu(Menu.CREATE_CREATURES);
        } else if (menu == Menu.MAIN_SCREEN && gen >= 0 && abs(mX - 990) <= 230) {
            if (abs(mY - 40) <= 20) {
                setMenu(Menu.START_SIMULATION);
                speed = 1;
                creaturesTested = 0;
                stepbystep = true;
                stepbystepslow = true;
            }
            if (abs(mY - 90) <= 20) {
                setMenu(Menu.START_SIMULATION);
                creaturesTested = 0;
                stepbystep = true;
                stepbystepslow = false;
            }
            if (abs(mY - 140) <= 20) {
                if (mX < 990) {
                    gensToDo = 1;
                } else {
                    gensToDo = 1000000000;
                }
                startASAP();
            }
        } else if (menu == Menu.VIEW_GENERATED && abs(mX - 1030) <= 130 && abs(mY - 684) <= 20) {
            gen = 0;
            setMenu(Menu.MAIN_SCREEN);
        } else if (menu == Menu.SIMULATION_RESULT && abs(mX - 1030) <= 130 && abs(mY - 684) <= 20) {
            setMenu(Menu.SIMULATION_SORT_VIS);
        } else if ((menu == Menu.SIMULATION_RUNNING || menu == Menu.START_SIMULATION) && mY >= windowHeight - 40) {
            if (mX < 90) {
                for (int s = timer; s < 900; s++) {
                    simulate();
                }
                timer = 1021;
            } else if (mX >= 120 && mX < 360) {
                speed *= 2;
                if (speed == 1024) speed = 900;
                if (speed >= 1800) speed = 1;
            } else if (mX >= windowWidth - 120) {
                for (int s = timer; s < 900; s++) {
                    simulate();
                }
                timer = 0;
                creaturesTested++;
                for (int i = creaturesTested; i < 1000; i++) {
                    setGlobalVariables(c[i]);
                    for (int s = 0; s < 900; s++) {
                        simulate();
                    }
                    setAverages();
                    setFitness(i);
                }
                setMenu(Menu.CLEANUP_SIMULATION);
            }
        } else if (menu == Menu.SIMULATION_SORT_VIS && mX < 90 && mY >= windowHeight - 40) {
            timer = 100000;
        } else if (menu == Menu.SIMULATION_RESULT_SORTED && abs(mX - 1030) <= 130 && abs(mY - 690) <= 20) {
            setMenu(Menu.KILL_500);
        } else if (menu == Menu.KILLS_VISUALIZE && abs(mX - 1130) <= 80 && abs(mY - 690) <= 20) {
            setMenu(Menu.REPRODUCE);
        } else if (menu == Menu.SHOW_NEW_CREATURES && abs(mX - 1130) <= 80 && abs(mY - 690) <= 20) {
            setMenu(Menu.MAIN_SCREEN);
        }
    }

    void drawScreenImage(int stage) {
        simulation.screenImage.beginDraw();
        simulation.screenImage.pushMatrix();
        simulation.screenImage.scale(15.0f / scaleToFixBug);
        simulation.screenImage.smooth();
        simulation.screenImage.background(220, 253, 102);
        simulation.screenImage.noStroke();
        for (int j = 0; j < 1000; j++) {
            Creature cj = c2.get(j);
            if (stage == 3) cj = c[cj.id - (gen * 1000) - 1001];
            int j2 = j;
            if (stage == 0) {
                j2 = cj.id - (gen * 1000) - 1;
                creaturesInPosition[j2] = j;
            }
            int x = j2 % 40;
            int y = floor(j2 / 40);
            if (stage >= 1) y++;
            drawCreature(cj, x * 3 + 5.5f, y * 2.5f + 4, 1);
        }
        timer = 0;
        simulation.screenImage.popMatrix();
        simulation.screenImage.pushMatrix();
        simulation.screenImage.scale(1.5f);

        simulation.screenImage.textAlign(CENTER);
        simulation.screenImage.textFont(font, 24);
        simulation.screenImage.fill(100, 100, 200);
        simulation.screenImage.noStroke();
        if (stage == 0) {
            simulation.screenImage.rect(900, 664, 260, 40);
            simulation.screenImage.fill(0);
            simulation.screenImage.text("All 1,000 creatures have been tested.  Now let's sort them!", windowWidth / 2 - 200, 690);
            simulation.screenImage.text("Sort", windowWidth - 250, 690);
        } else if (stage == 1) {
            simulation.screenImage.rect(900, 670, 260, 40);
            simulation.screenImage.fill(0);
            simulation.screenImage.text("Fastest creatures at the top!", windowWidth / 2, 30);
            simulation.screenImage.text("Slowest creatures at the bottom. (Going backward = slow)", windowWidth / 2 - 200, 700);
            simulation.screenImage.text("Kill 500", windowWidth - 250, 700);
        } else if (stage == 2) {
            simulation.screenImage.rect(1050, 670, 160, 40);
            simulation.screenImage.fill(0);
            simulation.screenImage.text("Faster creatures are more likely to survive because they can outrun their predators.  Slow creatures get eaten.", windowWidth / 2, 30);
            simulation.screenImage.text("Because of random chance, a few fast ones get eaten, while a few slow ones survive.", windowWidth / 2 - 130, 700);
            simulation.screenImage.text("Reproduce", windowWidth - 150, 700);
            for (int j = 0; j < 1000; j++) {
                Creature cj = c2.get(j);
                int x = j % 40;
                int y = floor(j / 40) + 1;
                if (cj.alive) {
                    drawCreature(cj, x * 30 + 55, y * 25 + 40, 0);
                } else {
                    simulation.screenImage.rect(x * 30 + 40, y * 25 + 17, 30, 25);
                }
            }
        } else if (stage == 3) {
            simulation.screenImage.rect(1050, 670, 160, 40);
            simulation.screenImage.fill(0);
            simulation.screenImage.text("These are the 1000 creatures of generation #" + (gen + 2) + ".", windowWidth / 2, 30);
            simulation.screenImage.text("What perils will they face?  Find out next time!", windowWidth / 2 - 130, 700);
            simulation.screenImage.text("Back", windowWidth - 150, 700);
        }
        simulation.screenImage.endDraw();
        simulation.screenImage.popMatrix();
    }

    void drawpopUpImage() {
        camZoom = 0.009f;
        setAverages();
        camX += (averageX - camX) * 0.1;
        camY += (averageY - camY) * 0.1;
        simulation.popUpImage.beginDraw();
        simulation.popUpImage.smooth();

        simulation.popUpImage.pushMatrix();
        simulation.popUpImage.translate(225, 225);
        simulation.popUpImage.scale(1.0f / camZoom / scaleToFixBug);
        simulation.popUpImage.translate(-camX * scaleToFixBug, -camY * scaleToFixBug);

        if (simulationTimer < 900) {
            simulation.popUpImage.background(120, 200, 255);
        } else {
            simulation.popUpImage.background(60, 100, 128);
        }
        drawPosts(2);
        drawGround(2);
        drawCreaturePieces(n, m, 0, 0, 2);
        simulation.popUpImage.noStroke();
        simulation.popUpImage.endDraw();
        simulation.popUpImage.popMatrix();
    }

    void drawCreature(Creature cj, float x, float y, int toImage) {
        draw(cj,cj.nodes, x, y, toImage);
    }

    void drawCreaturePieces(ArrayList<Node> n, ArrayList<Muscle> m, float x, float y, int toImage) {
        draw(new Creature(config, -1, n, m, false, 0),n,  x, y, toImage);
    }

    void drawHistogram(int x, int y, int hw, int hh) {
        int maxH = 1;
        for (int i = 0; i < barLen; i++) {
            if (barCounts.get(genSelected)[i] > maxH) {
                maxH = barCounts.get(genSelected)[i];
            }
        }
        fill(200);
        noStroke();
        rect(x, y, hw, hh);
        fill(0, 0, 0);
        float barW = (float) hw / barLen;
        float multiplier = (float) hh / maxH * 0.9f;
        textAlign(LEFT);
        textFont(font, 16);
        stroke(128);
        strokeWeight(2);
        int unit = 100;
        if (maxH < 300) unit = 50;
        if (maxH < 100) unit = 20;
        if (maxH < 50) unit = 10;
        for (int i = 0; i < hh / multiplier; i += unit) {
            float theY = y + hh - i * multiplier;
            line(x, theY, x + hw, theY);
            if (i == 0) theY -= 5;
            text(i, x + hw + 5, theY + 7);
        }
        textAlign(CENTER);
        for (int i = minBar; i <= maxBar; i += 10) {
            if (i == 0) {
                stroke(0, 0, 255);
            } else {
                stroke(128);
            }
            float theX = x + (i - minBar) * barW;
            text(nf((float) i / histBarsPerMeter, 0, 1), theX, y + hh + 14);
            line(theX, y, theX, y + hh);
        }
        noStroke();
        for (int i = 0; i < barLen; i++) {
            float h = min(barCounts.get(genSelected)[i] * multiplier, hh);
            if (i + minBar == floor(percentile.get(min(genSelected, percentile.size() - 1))[14] * histBarsPerMeter)) {
                fill(255, 0, 0);
            } else {
                fill(0, 0, 0);
            }
            rect(x + i * barW, y + hh - h, barW, h);
        }
    }

    void drawStatusWindow(boolean isFirstFrame) {
        int x, y, px, py;
        int rank = (statusWindow + 1);
        Creature cj;
        stroke(abs(overallTimer % 30 - 15) * 17);
        strokeWeight(3);
        noFill();
        if (statusWindow >= 0) {
            cj = c2.get(statusWindow);
            if (menu == Menu.SIMULATION_RESULT) {
                int id = ((cj.id - 1) % 1000);
                x = id % 40;
                y = floor(id / 40);
            } else {
                x = statusWindow % 40;
                y = floor(statusWindow / 40) + 1;
            }
            px = x * 30 + 55;
            py = y * 25 + 10;
            if (px <= 1140) {
                px += 80;
            } else {
                px -= 80;
            }
            rect(x * 30 + 40, y * 25 + 17, 30, 25);
        } else {
            cj = creatureDatabase.get((genSelected - 1) * 3 + statusWindow + 3);
            x = 760 + (statusWindow + 3) * 160;
            y = 180;
            px = x;
            py = y;
            rect(x, y, 140, 140);
            int[] ranks = {
                    1000, 500, 1
            };
            rank = ranks[statusWindow + 3];
        }
        noStroke();
        fill(255);
        rect(px - 60, py, 120, 52);
        fill(0);
        textFont(font, 13);
        textAlign(CENTER);
        text("#" + rank, px, py + 12);
        text("ID: " + cj.id, px, py + 24);
        text("Fitness: " + nf(cj.fitness, 0, 3), px, py + 36);
        colorMode(HSB, 1);
        int sp = (cj.nodes.size() % 10) * 10 + (cj.m.size() % 10);
        fill(getColor(sp, true));
        text("Species: S" + (cj.nodes.size() % 10) + "" + (cj.m.size() % 10), px, py + 48);
        colorMode(RGB, 255);
        if (miniSimulation) {
            int py2 = py - 125;
            if (py >= 360) {
                py2 -= 180;
            } else {
                py2 += 180;
            }
            //py = min(max(py,0),420);
            int px2 = min(max(px - 90, 10), 970);
            drawpopUpImage();
            image(simulation.popUpImage, px2, py2, 300, 300);

    /*fill(255, 255, 255);
    rect(px2+240, py2+10, 50, 30);
    rect(px2+10, py2+10, 100, 30);
    fill(0, 0, 0);
    textFont(font, 30);
    textAlign(RIGHT);
    text(int(simulationTimer/60), px2+285, py2+36);
    textAlign(LEFT);
    text(nf(averageX/5.0, 0, 3), px2+15, py2+36);*/
            drawStats(px2 + 295, py2, 0.45f);

            simulate();
            int shouldBeWatching = statusWindow;
            if (statusWindow <= -1) {
                cj = creatureDatabase.get((genSelected - 1) * 3 + statusWindow + 3);
                shouldBeWatching = cj.id;
            }
            if (creatureWatching != shouldBeWatching || isFirstFrame) {
                openMiniSimulation();
            }
        }
    }

    public void setup() {
        frameRate(60);
        randomSeed(SEED);
        noSmooth();
        size((int) (windowWidth * windowSizeMultiplier), (int) (windowHeight * windowSizeMultiplier));
        ellipseMode(CENTER);
        Float[] beginPercentile = new Float[29];
        Integer[] beginBar = new Integer[barLen];
        Integer[] beginSpecies = new Integer[101];
        for (int i = 0; i < 29; i++) {
            beginPercentile[i] = 0.0f;
        }
        for (int i = 0; i < barLen; i++) {
            beginBar[i] = 0;
        }
        for (int i = 0; i < 101; i++) {
            beginSpecies[i] = 500;
        }

        percentile.add(beginPercentile);
        barCounts.add(beginBar);
        speciesCounts.add(beginSpecies);
        topSpeciesCounts.add(0);

        PGraphics graphImage = createGraphics(975, 570);
        PGraphics screenImage = createGraphics(1920, 1080);
        PGraphics popUpImage = createGraphics(450, 450);
        PGraphics segBarImage = createGraphics(975, 150);

        simulation = new Simulation(graphImage, screenImage, popUpImage, segBarImage);

        simulation.segBarImage.beginDraw();
        simulation.segBarImage.smooth();
        simulation.segBarImage.background(220);
        simulation.segBarImage.endDraw();
        simulation.popUpImage.beginDraw();
        simulation.popUpImage.smooth();
        simulation.popUpImage.background(220);
        simulation.popUpImage.endDraw();

        font = loadFont("Helvetica-Bold-96.vlw");
        config.setFont(font);
        config.setAxonColor(axonColor);
        textFont(font, 96);
        textAlign(CENTER);

  /*rects.add(new Rectangle(4,-7,9,-3));
   rects.add(new Rectangle(6,-1,10,10));
   rects.add(new Rectangle(9.5,-1.5,13,10));
   rects.add(new Rectangle(12,-2,16,10));
   rects.add(new Rectangle(15,-2.5,19,10));
   rects.add(new Rectangle(18,-3,22,10));
   rects.add(new Rectangle(21,-3.5,25,10));
   rects.add(new Rectangle(24,-4,28,10));
   rects.add(new Rectangle(27,-4.5,31,10));
   rects.add(new Rectangle(30,-5,34,10));
   rects.add(new Rectangle(33,-5.5,37,10));
   rects.add(new Rectangle(36,-6,40,10));
   rects.add(new Rectangle(39,-6.5,100,10));*/

        //rects.add(new Rectangle(-100,-100,100,-2.8));
        //rects.add(new Rectangle(-100,0,100,100));
        //Snaking thing below:
  /*rects.add(new Rectangle(-400,-10,1.5,-1.5));
   rects.add(new Rectangle(-400,-10,3,-3));
   rects.add(new Rectangle(-400,-10,4.5,-4.5));
   rects.add(new Rectangle(-400,-10,6,-6));
   rects.add(new Rectangle(0.75,-0.75,400,4));
   rects.add(new Rectangle(2.25,-2.25,400,4));
   rects.add(new Rectangle(3.75,-3.75,400,4));
   rects.add(new Rectangle(5.25,-5.25,400,4));
   rects.add(new Rectangle(-400,-5.25,0,4));*/

        ground = new Ground(config, rects);
    }

    public void draw() {
        scale(windowSizeMultiplier);
        if (menu == Menu.START_SCREEN) {
            background(255);
            fill(100, 200, 100);
            noStroke();
            rect(windowWidth / 2 - 200, 300, 400, 200);
            fill(0);
            text("EVOLUTION!", windowWidth / 2, 200);
            text("START", windowWidth / 2, 430);
        } else if (menu == Menu.MAIN_SCREEN) {
            noStroke();
            fill(0);
            background(255, 200, 130);
            textFont(font, 32);
            textAlign(LEFT);
            textFont(font, 96);
            text("Generation " + max(genSelected, 0), 20, 100);
            textFont(font, 28);
            if (gen == -1) {
                fill(100, 200, 100);
                rect(20, 250, 200, 100);
                fill(0);
                text("Since there are no creatures yet, create 1000 creatures!", 20, 160);
                text("They will be randomly created, and also very simple.", 20, 200);
                text("CREATE", 56, 312);
            } else {
                fill(100, 200, 100);
                rect(760, 20, 460, 40);
                rect(760, 70, 460, 40);
                rect(760, 120, 230, 40);
                if (gensToDo >= 2) {
                    fill(128, 255, 128);
                } else {
                    fill(70, 140, 70);
                }
                rect(990, 120, 230, 40);
                fill(0);
                text("Do 1 step-by-step generation.", 770, 50);
                text("Do 1 quick generation.", 770, 100);
                text("Do 1 gen ASAP.", 770, 150);
                text("Do gens ALAP.", 1000, 150);
                text("Median " + fitnessName, 50, 160);
                textAlign(CENTER);
                textAlign(RIGHT);
                text((float) (round(percentile.get(min(genSelected, percentile.size() - 1))[14] * 1000)) / 1000 + " " + fitnessUnit, 700, 160);
                drawHistogram(760, 410, 460, 280);
                drawGraphImage();
                if (saveFramesPerGeneration && gen > lastImageSaved) {
                    saveFrame("imgs//" + zeros(gen, 5) + ".png");
                    lastImageSaved = gen;
                }
            }
            if (gensToDo >= 1) {
                gensToDo--;
                if (gensToDo >= 1) {
                    startASAP();
                }
            }
        } else if (menu == Menu.CREATE_CREATURES) {
            creatures = 0;
            background(220, 253, 102);
            pushMatrix();
            scale(10.0f / scaleToFixBug);
            for (int y = 0; y < 25; y++) {
                for (int x = 0; x < 40; x++) {
                    n.clear();
                    m.clear();
                    int nodeNum = (int) (random(3, 6));
                    int muscleNum = (int) (random(nodeNum - 1, nodeNum * 3 - 6));
                    for (int i = 0; i < nodeNum; i++) {
                        n.add(new Node(config, random(-1, 1), random(-1, 1), 0, 0, 0.4f, random(0, 1), random(0, 1),
                                floor(random(0, config.getOperationCount())), floor(random(0, nodeNum)), floor(random(0, nodeNum)))); //replaced all nodes' sizes with 0.4, used to be random(0.1,1), random(0,1)
                    }
                    for (int i = 0; i < muscleNum; i++) {
                        int tc1 = 0;
                        int tc2 = 0;
                        int taxon = Muscle.getNewMuscleAxon(nodeNum);
                        if (i < nodeNum - 1) {
                            tc1 = i;
                            tc2 = i + 1;
                        } else {
                            tc1 = (int) (random(0, nodeNum));
                            tc2 = tc1;
                            while (tc2 == tc1) {
                                tc2 = (int) (random(0, nodeNum));
                            }
                        }
                        float s = 0.8f;
                        if (i >= 10) {
                            s *= 1.414;
                        }
                        float len = random(0.5f, 1.5f);
                        m.add(new Muscle(config, taxon, tc1, tc2, len, random(0.02f, 0.08f)));
                    }
                    toStableConfiguration(nodeNum, muscleNum);
                    adjustToCenter(nodeNum);
                    float heartbeat = random(40, 80);
                    c[y * 40 + x] = new Creature(config, y * 40 + x + 1, new ArrayList<Node>(n), new ArrayList<Muscle>(m),  true, 1.0f);
                    drawCreature(c[y * 40 + x], x * 3 + 5.5f, y * 2.5f + 3, 0);
                    c[y * 40 + x].checkForOverlap();
                    c[y * 40 + x].checkForLoneNodes();
                    c[y * 40 + x].checkForBadAxons();
                }
            }
            setMenu(Menu.VIEW_GENERATED);
            popMatrix();
            noStroke();
            fill(100, 100, 200);
            rect(900, 664, 260, 40);
            fill(0);
            textAlign(CENTER);
            textFont(font, 24);
            text("Here are your 1000 randomly generated creatures!!!", windowWidth / 2 - 200, 690);
            text("Back", windowWidth - 250, 690);
        } else if (menu == Menu.START_SIMULATION) {
            setGlobalVariables(c[creaturesTested]);
            camZoom = 0.01f;
            setMenu(Menu.SIMULATION_RUNNING);
            if (!stepbystepslow) {
                for (int i = 0; i < 1000; i++) {
                    setGlobalVariables(c[i]);
                    for (int s = 0; s < 900; s++) {
                        simulate();
                    }
                    setAverages();
                    setFitness(i);
                }
                setMenu(Menu.CLEANUP_SIMULATION);
            }
        }
        if (menu == Menu.SIMULATION_RUNNING) { //simulate running
            if (timer <= 900) {
                background(120, 200, 255);
                for (int s = 0; s < speed; s++) {
                    if (timer < 900) {
                        simulate();
                    }
                }
                setAverages();
                if (speed < 30) {
                    for (int s = 0; s < speed; s++) {
                        camX += (averageX - camX) * 0.06;
                        camY += (averageY - camY) * 0.06;
                    }
                } else {
                    camX = averageX;
                    camY = averageY;
                }
                pushMatrix();
                translate(width / 2.0f, height / 2.0f);
                scale(1.0f / camZoom / scaleToFixBug);
                translate(-camX * scaleToFixBug, -camY * scaleToFixBug);

                drawPosts(0);
                drawGround(0);
                drawCreaturePieces(n, m, 0, 0, 0);
                drawArrow(averageX);
                popMatrix();
                drawStats(windowWidth - 10, 0, 0.7f);
                drawSkipButton();
                drawOtherButtons();
            }
            if (timer == 900) {
                if (speed < 30) {
                    noStroke();
                    fill(0, 0, 0, 130);
                    rect(0, 0, windowWidth, windowHeight);
                    fill(0, 0, 0, 255);
                    rect(windowWidth / 2 - 500, 200, 1000, 240);
                    fill(255, 0, 0);
                    textAlign(CENTER);
                    textFont(font, 96);
                    text("evolution.parts.Creature's " + fitnessName + ":", windowWidth / 2, 300);
                    text(nf(averageX * 0.2f, 0, 2) + " " + fitnessUnit, windowWidth / 2, 400);
                } else {
                    timer = 1020;
                }
                setFitness(creaturesTested);
            }
            if (timer >= 1020) {
                setMenu(Menu.START_SIMULATION);
                creaturesTested++;
                if (creaturesTested == 1000) {
                    setMenu(Menu.CLEANUP_SIMULATION);
                }
                camX = 0;
            }
            if (timer >= 900) {
                timer += speed;
            }
        }
        if (menu == Menu.CLEANUP_SIMULATION) {
            //sort
            c2 = new ArrayList<Creature>(0);
            for (int i = 0; i < 1000; i++) {
                c2.add(c[i]);
            }
            c2 = quickSort(c2);
            percentile.add(new Float[29]);
            for (int i = 0; i < 29; i++) {
                percentile.get(gen + 1)[i] = c2.get(p[i]).fitness;
            }
            creatureDatabase.add(c2.get(999).copyCreature(-1));
            creatureDatabase.add(c2.get(499).copyCreature(-1));
            creatureDatabase.add(c2.get(0).copyCreature(-1));

            Integer[] beginBar = new Integer[barLen];
            for (int i = 0; i < barLen; i++) {
                beginBar[i] = 0;
            }
            barCounts.add(beginBar);
            Integer[] beginSpecies = new Integer[101];
            for (int i = 0; i < 101; i++) {
                beginSpecies[i] = 0;
            }
            for (int i = 0; i < 1000; i++) {
                int bar = floor(c2.get(i).fitness * histBarsPerMeter - minBar);
                if (bar >= 0 && bar < barLen) {
                    barCounts.get(gen + 1)[bar]++;
                }
                int species = (c2.get(i).nodes.size() % 10) * 10 + c2.get(i).m.size() % 10;
                beginSpecies[species]++;
            }
            speciesCounts.add(new Integer[101]);
            speciesCounts.get(gen + 1)[0] = 0;
            int cum = 0;
            int record = 0;
            int holder = 0;
            for (int i = 0; i < 100; i++) {
                cum += beginSpecies[i];
                speciesCounts.get(gen + 1)[i + 1] = cum;
                if (beginSpecies[i] > record) {
                    record = beginSpecies[i];
                    holder = i;
                }
            }
            topSpeciesCounts.add(holder);
            if (stepbystep) {
                drawScreenImage(0);
                setMenu(Menu.SIMULATION_RESULT);
            } else {
                setMenu(Menu.KILL_500);
            }
        }
        if (menu == Menu.SIMULATION_SORT_VIS) {
            //cool sorting animation
            background(220, 253, 102);
            pushMatrix();
            scale(10.0f / scaleToFixBug);
            float transition = 0.5f - 0.5f * cos(min((float) (timer) / 60, PI));
            for (int j = 0; j < 1000; j++) {
                Creature cj = c2.get(j);
                int j2 = cj.id - (gen * 1000) - 1;
                int x1 = j2 % 40;
                int y1 = floor(j2 / 40);
                int x2 = j % 40;
                int y2 = floor(j / 40) + 1;
                float x3 = inter(x1, x2, transition);
                float y3 = inter(y1, y2, transition);
                drawCreature(cj, x3 * 3 + 5.5f, y3 * 2.5f + 4, 0);
            }
            popMatrix();
            if (stepbystepslow) {
                timer += 2;
            } else {
                timer += 10;
            }
            drawSkipButton();
            if (timer > 60 * PI) {
                drawScreenImage(1);
                setMenu(Menu.SIMULATION_RESULT_SORTED);
            }
        }
        float mX = mouseX / windowSizeMultiplier;
        ;
        float mY = mouseY / windowSizeMultiplier;
        ;
        prevStatusWindow = statusWindow;
        //TODO: WTF IS THIS MENU CALUCLATING
        if (abs(menu.value - 9) <= 2 && gensToDo == 0 && !drag) {
            if (abs(mX - 639.5f) <= 599.5) {
                if (menu == Menu.SIMULATION_RESULT && abs(mY - 329) <= 312) {
                    statusWindow = creaturesInPosition[floor((mX - 40) / 30) + floor((mY - 17) / 25) * 40];
                    //TODO: WTF IS THIS MENU CALUCLATING
                } else if (menu.value >= 9 && abs(mY - 354) <= 312) {
                    statusWindow = floor((mX - 40) / 30) + floor((mY - 42) / 25) * 40;
                } else {
                    statusWindow = -4;
                }
            } else {
                statusWindow = -4;
            }
        } else if (menu == Menu.MAIN_SCREEN && genSelected >= 1 && gensToDo == 0 && !drag) {
            statusWindow = -4;
            if (abs(mY - 250) <= 70) {
                if (abs(mX - 990) <= 230) {
                    float modX = (mX - 760) % 160;
                    if (modX < 140) {
                        statusWindow = floor((mX - 760) / 160) - 3;
                    }
                }
            }
        } else {
            statusWindow = -4;
        }
        if (menu == Menu.KILL_500) {
            //Kill!
            for (int j = 0; j < 500; j++) {
                float f = ((float) j) / 1000.0f;
                float rand = (pow(random(-1, 1), 3) + 1) / 2; //cube function
                slowDies = (f <= rand);
                int j2;
                int j3;
                if (slowDies) {
                    j2 = j;
                    j3 = 999 - j;
                } else {
                    j2 = 999 - j;
                    j3 = j;
                }
                Creature cj = c2.get(j2);
                cj.alive = true;
                Creature ck = c2.get(j3);
                ck.alive = false;
            }
            if (stepbystep) {
                drawScreenImage(2);
                setMenu(Menu.KILLS_VISUALIZE);
            } else {
                setMenu(Menu.REPRODUCE);
            }
        }
        if (menu == Menu.REPRODUCE) { //Reproduce and mutate
            justGotBack = true;
            for (int j = 0; j < 500; j++) {
                int j2 = j;
                if (!c2.get(j).alive) j2 = 999 - j;
                Creature cj = c2.get(j2);
                Creature cj2 = c2.get(999 - j2);

                c2.set(j2, cj.copyCreature(cj.id + 1000));        //duplicate

                c2.set(999 - j2, cj.modified(cj2.id + 1000));   //mutated offspring 1
                n = c2.get(999 - j2).nodes;
                m = c2.get(999 - j2).m;
                toStableConfiguration(n.size(), m.size());
                adjustToCenter(n.size());
            }
            for (int j = 0; j < 1000; j++) {
                Creature cj = c2.get(j);
                c[cj.id - (gen * 1000) - 1001] = cj.copyCreature(-1);
            }
            drawScreenImage(3);
            gen++;
            if (stepbystep) {
                setMenu(Menu.SHOW_NEW_CREATURES);
            } else {
                setMenu(Menu.MAIN_SCREEN);
            }
        }
        //TODO WTF IS THAT MENU CALUCATION
        if (menu.value % 2 == 1 && abs(menu.value - 10) <= 3) {
            image(simulation.screenImage, 0, 0, 1280, 720);
        }
        if (menu == Menu.MAIN_SCREEN || gensToDo >= 1) {
            mX = mouseX / windowSizeMultiplier;
            ;
            mY = mouseY / windowSizeMultiplier;
            ;
            noStroke();
            if (gen >= 1) {
                textAlign(CENTER);
                if (gen >= 5) {
                    genSelected = round((sliderX - 760) * (gen - 1) / 410) + 1;
                } else {
                    genSelected = round((sliderX - 760) * gen / 410);
                }
                if (drag) sliderX = min(max(sliderX + (mX - 25 - sliderX) * 0.2f, 760), 1170);
                fill(100);
                rect(760, 340, 460, 50);
                fill(220);
                rect(sliderX, 340, 50, 50);
                int fs = 0;
                if (genSelected >= 1) {
                    fs = floor(log(genSelected) / log(10));
                }
                fontSize = fontSizes[fs];
                textFont(font, fontSize);
                fill(0);
                text(genSelected, sliderX + 25, 366 + fontSize * 0.3333f);
            }
            if (genSelected >= 1) {
                textAlign(CENTER);
                for (int k = 0; k < 3; k++) {
                    fill(220);
                    rect(760 + k * 160, 180, 140, 140);
                    pushMatrix();
                    translate(830 + 160 * k, 290);
                    scale(60.0f / scaleToFixBug);
                    drawCreature(creatureDatabase.get((genSelected - 1) * 3 + k), 0, 0, 0);
                    popMatrix();
                }
                fill(0);
                textFont(font, 16);
                text("Worst Creature", 830, 310);
                text("Median Creature", 990, 310);
                text("Best Creature", 1150, 310);
            }
            if (justGotBack) justGotBack = false;
        }
        if (statusWindow >= -3) {
            drawStatusWindow(prevStatusWindow == -4);
            if (statusWindow >= -3 && !miniSimulation) {
                openMiniSimulation();
            }
        }
  /*if(menu >= 1){
   fill(255);
   rect(0,705,100,15);
   fill(0);
   textAlign(LEFT);
   textFont(font,12);
   int g = gensToDo;
   if(gensToDo >= 10000){
   g = 1000000000-gensToDo;
   }
   text(g,2,715);
   }*/
        overallTimer++;
    }

    void drawStats(float x, float y, float size) {
        textAlign(RIGHT);
        textFont(font, 32);
        fill(0);
        pushMatrix();
        translate(x, y);
        scale(size);
        text("evolution.parts.Creature ID: " + id, 0, 32);
        if (speed > 60) {
            timeShow = (int) ((timer + creaturesTested * 37) / 60) % 15;
        } else {
            timeShow = (timer / 60);
        }
        text("Time: " + nf(timeShow, 0, 2) + " / 15 sec.", 0, 64);
        text("Playback Speed: x" + max(1, speed), 0, 96);
        String extraWord = "used";
        if (config.getEnergyDirection() == -1) {
            extraWord = "left";
        }
        text("X: " + nf(averageX / 5.0f, 0, 2) + "", 0, 128);
        text("Y: " + nf(-averageY / 5.0f, 0, 2) + "", 0, 160);
        text("Energy " + extraWord + ": " + nf(energy, 0, 2) + " yums", 0, 192);
        text("A.N.Nausea: " + nf(averageNodeNausea, 0, 2) + " blehs", 0, 224);

        popMatrix();
    }

    void drawSkipButton() {
        fill(0);
        rect(0, windowHeight - 40, 90, 40);
        fill(255);
        textAlign(CENTER);
        textFont(font, 32);
        text("SKIP", 45, windowHeight - 8);
    }

    void drawOtherButtons() {
        fill(0);
        rect(120, windowHeight - 40, 240, 40);
        fill(255);
        textAlign(CENTER);
        textFont(font, 32);
        text("PB speed: x" + speed, 240, windowHeight - 8);
        fill(0);
        rect(windowWidth - 120, windowHeight - 40, 120, 40);
        fill(255);
        textAlign(CENTER);
        textFont(font, 32);
        text("FINISH", windowWidth - 60, windowHeight - 8);
    }

    void setGlobalVariables(Creature thisCreature) {
        n.clear();
        m.clear();
        for (int i = 0; i < thisCreature.nodes.size(); i++) {
            n.add(thisCreature.nodes.get(i).copyNode());
        }
        for (int i = 0; i < thisCreature.m.size(); i++) {
            m.add(thisCreature.m.get(i).copyMuscle());
        }
        id = thisCreature.id;
        timer = 0;
        camZoom = 0.01f;
        camX = 0;
        camY = 0;
        simulationTimer = 0;
        energy = baselineEnergy;
        totalNodeNausea = 0;
        averageNodeNausea = 0;
    }

    void setFitness(int i) {
        c[i].fitness = averageX * 0.2f; // Multiply by 0.2 because a meter is 5 units for some weird reason.
    }
}
