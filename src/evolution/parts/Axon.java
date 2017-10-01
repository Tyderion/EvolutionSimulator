package evolution.parts;

import processing.core.PGraphics;

import static processing.core.PApplet.atan2;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.PI;

public class Axon {
    public static void drawSingleAxon(float x1, float y1, float x2, float y2, PGraphics graphics, float scaleToFixBug, int axonColor) {
        float arrowHeadSize = 0.1f;
        float angle = atan2(y2 - y1, x2 - x1);
        graphics.stroke(axonColor);
        graphics.strokeWeight(0.03f * scaleToFixBug);
        graphics.line(x1 * scaleToFixBug, y1 * scaleToFixBug, x2 * scaleToFixBug, y2 * scaleToFixBug);
        graphics.line(x1 * scaleToFixBug, y1 * scaleToFixBug, (x1 + cos(angle + PI * 0.25f) * arrowHeadSize) * scaleToFixBug, (y1 + sin(angle + PI * 0.25f) * arrowHeadSize) * scaleToFixBug);
        graphics.line(x1 * scaleToFixBug, y1 * scaleToFixBug, (x1 + cos(angle + PI * 1.75f) * arrowHeadSize) * scaleToFixBug, (y1 + sin(angle + PI * 1.75f) * arrowHeadSize) * scaleToFixBug);
        graphics.noStroke();
    }
}
