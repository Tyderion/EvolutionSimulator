package evolution.parts;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

public interface Drawable {
    void draw(ArrayList<Node> n, float x, float y, PGraphics graphics);

    void drawAxons(ArrayList<Node> n, float x, float y, PGraphics graphics);
}
