package evolution;

import processing.core.PGraphics;

public class Simulation {
    public PGraphics graphImage;
    public PGraphics screenImage;
    public PGraphics popUpImage;
    public PGraphics segBarImage;

    public Simulation(PGraphics graphImage, PGraphics screenImage, PGraphics popUpImage, PGraphics segBarImage) {
        this.graphImage = graphImage;
        this.screenImage = screenImage;
        this.popUpImage = popUpImage;
        this.segBarImage = segBarImage;
    }
}
