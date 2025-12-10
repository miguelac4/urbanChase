package apps.tp3;

import fractals.LSystem;
import fractals.Rule;
import fractals.Turtle;
import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

public class SierpinskiApp implements IProcessingApp {

    private LSystem lsys;
    private double[] window = {-1.5, 1.5, -0.5, 1.5};
    private float[] viewport = {0.1f, 0.1f, 0.8f, 0.8f};
    private SubPlot plt;
    private Turtle turtle;
    private PVector startingPos = new PVector(-1.5f, -0.5f); // centrar

    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport, p.width, p.height);

        // Regras
        Rule[] rules = new Rule[2];
        rules[0] = new Rule('F', "F-G+F+G-F");
        rules[1] = new Rule('G', "GG");

        lsys = new LSystem("F-G-G", rules);

        // comprimento inicial e ângulo de 120 graus
        turtle = new Turtle(3f, PApplet.radians(120));
    }

    @Override
    public void draw(PApplet p, float dt) {
        p.background(255);

        float[] bb = plt.getBoundingBox();
        p.noFill();
        p.stroke(0);
        p.rect(bb[0], bb[1], bb[2], bb[3]);

        // Alterar cor a cada geração
        int gen = lsys.getGeneration();
        int col = p.color((50 * gen) % 255,
                (120 * gen) % 255,
                (200 - 30 * gen) % 255);
        turtle.setColor(col);

        p.pushMatrix();
        turtle.setPose(startingPos, 0, p, plt);
        turtle.render(lsys, p, plt);
        p.popMatrix();
    }

    @Override
    public void mousePressed(PApplet p) {
        // próxima geração e encurtar segmentos
        lsys.nextGeneration();
        turtle.scaling(0.5f);  // Razão de escala 1/2
    }

    @Override public void keyPressed(PApplet p) {}
    @Override public void keyReleased(PApplet p) {}
    @Override public void mouseReleased(PApplet p) {}
    @Override public void mouseDragged(PApplet p) {}
}
