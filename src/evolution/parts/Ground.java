package evolution.parts;

import evolution.Config;
import processing.core.PGraphics;

import java.util.ArrayList;

import static processing.core.PApplet.max;

public class Ground {

    private Config config;
    private final float scaleToFixBug;
    private final ArrayList<Rectangle> rects;

    public Ground(Config config, ArrayList<Rectangle> rects) {
        this.config = config;
        this.scaleToFixBug = config.getScaleToFixBug();
        this.rects = rects;
    }

    public void draw(float averageX, float averageY, float camFactorA, float camFactorC, float camFactorD, float camX, float camZoom, PGraphics graphics) {
        int stairDrawStart = max(1, (int) (-averageY / config.getHazelStairs()) - 10);
        graphics.noStroke();
        graphics.fill(0, 130, 0);
        if (config.hasGround())
            graphics.rect((camX - camZoom * camFactorA) * scaleToFixBug, 0 * scaleToFixBug, (camZoom * camFactorC) * scaleToFixBug, (camZoom * camFactorD) * scaleToFixBug);
        float ww = 450;
        float wh = 450;
        for (int i = 0; i < rects.size(); i++) {
            Rectangle r = rects.get(i);
            graphics.rect(r.x1 * scaleToFixBug, r.y1 * scaleToFixBug, (r.x2 - r.x1) * scaleToFixBug, (r.y2 - r.y1) * scaleToFixBug);
        }
        if (config.getHazelStairs() > 0) {
            for (int i = stairDrawStart; i < stairDrawStart + 20; i++) {
                graphics.fill(255, 255, 255, 128);
                graphics.rect((averageX - 20) * scaleToFixBug, -config.getHazelStairs() * i * scaleToFixBug, 40 * scaleToFixBug, config.getHazelStairs() * 0.3f * scaleToFixBug);
                graphics.fill(255, 255, 255, 255);
                graphics.rect((averageX - 20) * scaleToFixBug, -config.getHazelStairs() * i * scaleToFixBug, 40 * scaleToFixBug, config.getHazelStairs() * 0.15f * scaleToFixBug);
            }
        }
    }

}
