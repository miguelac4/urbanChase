package apps.tp3;

import fractals.LSystem;
import fractals.Rule;
import fractals.Turtle;
import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

public class DragonCurveApp implements IProcessingApp {

    private LSystem lsys;
    private SubPlot plt;
    private Turtle turtle;

    private double[] window = {-2, 2, -2, 2};
    private float[] viewport = {0.05f, 0.05f, 0.9f, 0.9f};
    private PVector startingPos = new PVector(0f, 0f);

    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport, p.width, p.height);

        Rule[] rules = new Rule[2];
        rules[0] = new Rule('X', "X+YF+");
        rules[1] = new Rule('Y', "-FX-Y");

        // X e Y servem apenas para controlar a geração, não desenham

        lsys = new LSystem("FX", rules);

        // comprimento inicial e ângulo 90°
        turtle = new Turtle(1.5f, PApplet.radians(90));
        turtle.setColor(p.color(0)); // começa a preto
    }

    @Override
    public void draw(PApplet p, float dt) {
        p.background(255);

        // desenhar moldura do subplot
        float[] bb = plt.getBoundingBox();
        p.noFill();
        p.stroke(0);
        p.rect(bb[0], bb[1], bb[2], bb[3]);

        // cor por geração
        int gen = lsys.getGeneration();
        int col = p.color((60 * gen) % 255,
                (180 - 20 * gen) % 255,
                (120 + 30 * gen) % 255);
        turtle.setColor(col);

        p.pushMatrix();
        turtle.setPose(startingPos, 0, p, plt);
        turtle.render(lsys, p, plt);
        p.popMatrix();
    }

    @Override
    public void mousePressed(PApplet p) {
        lsys.nextGeneration(); // próxima geração

        // fator de escala para o dragon 1/sqrt(2)
        float s = 1f / (float)Math.sqrt(2);
        turtle.scaling(s);
    }

    @Override public void keyPressed(PApplet p) {}
    @Override public void keyReleased(PApplet p) {}
    @Override public void mouseReleased(PApplet p) {}
    @Override public void mouseDragged(PApplet p) {}
}
