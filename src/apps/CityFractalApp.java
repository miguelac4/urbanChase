package apps;

import city.Node;
import city.RoadSegment;
import fractals.LSystem;
import fractals.Rule;
import fractals.Turtle;
import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

import java.util.ArrayList;

public class CityFractalApp implements IProcessingApp {

    private SubPlot plt;
    private double[] window = {-2, 2, -2, 2};
    private float[] viewport = {0f, 0f, 1f, 1f};

    private LSystem lsys;

    private Turtle turtle1;
    private Turtle turtle2;

    private PVector origin1;
    private PVector origin2;

    private float baseLength = 0.3f;
    private float angle = (float) (Math.PI / 2);
    private int nGenerations = 4;

    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport, p.width, p.height);

        String axiom = "F";
        Rule[] rules = new Rule[]{
                new Rule('F', "F[+F]F[-F]F")
        };
        lsys = new LSystem(axiom, rules);

        // Geração fixa
        for (int i = 0; i < nGenerations; i++) {
            lsys.nextGeneration();
        }

        turtle1 = new Turtle(baseLength, angle);
        turtle2 = new Turtle(baseLength, angle);

        origin1 = new PVector(-2.0f, 0f);
        origin2 = new PVector(-0.2f, -2.0f);
    }

    @Override
    public void draw(PApplet p, float dt) {
        p.background(220);
        p.stroke(30);
        p.strokeWeight(2);
        p.noFill();

        // 1a turtle (horizontal)
        p.pushMatrix();
        turtle1.setPose(origin1, 0f, p, plt);
        turtle1.render(lsys, p, plt);
        p.popMatrix();

        // 2a turtle (vertical)
        p.pushMatrix();
        turtle2.setPose(origin2, (float) (Math.PI / 2), p, plt);
        turtle2.render(lsys, p, plt);
        p.popMatrix();
    }

    @Override
    public void keyPressed(PApplet p) {
    }

    @Override
    public void keyReleased(PApplet p) {
    }

    @Override
    public void mousePressed(PApplet p) {
    }

    @Override
    public void mouseReleased(PApplet p) {
    }

    @Override
    public void mouseDragged(PApplet p) {
    }
}
