package physics;

import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

public class CelestialBody extends Mover{

    private int color;
    private static double G = 6.67e-11;

    protected CelestialBody(PVector pos, PVector vel, float mass, float radius, int color){
        super(pos, vel, mass, radius);
        this.color = color;
    }

    public PVector attraction(Mover m){
        PVector r = PVector.sub(pos, m.pos);
        float dist = r.mag();
        float strength = (float) (G * mass * m.mass / Math.pow(dist, 2));
        return r.normalize().mult(strength);
    }

    public void display(PApplet p, SubPlot plt){

        p.pushStyle();
        float[] pp = plt.getPixelCoord(pos.x, pos.y);
        float[] r = plt.getDimInPixel(radius, radius);
        p.noStroke();
        p.fill(color);
        p.circle(pp[0], pp[1], 2*r[0]);
        p.popStyle();
    }
}
