package apps.tp3;
import fractals.LSystem;
import fractals.Rule;
import fractals.Turtle;
import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

public class LSystemApp implements IProcessingApp{
    private LSystem lsys;
    private double[] window = {-15,15,0,30};
    private float[] viewport = {0.1f, 0.1f, 0.8f,0.8f};
    private PVector startingPos = new PVector();
    private SubPlot plt;
    private Turtle turtle;

    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport, p.width, p.height);
        Rule[] rules = new Rule[1];
        //rules[0] = new Rule('X',"F+[[X]-X]-F[-FX]+X");
        //rules[1] = new Rule('F',"FF");
        rules[0] = new Rule('F',"FF+[+F-F-F]-[-F+F+F]");

        lsys = new LSystem("F",rules);
        turtle = new Turtle(5f,PApplet.radians(25));
    }

    @Override
    public void draw(PApplet p, float dt) {
        float[] bb = plt.getBoundingBox();
        p.rect(bb[0],bb[1],bb[2],bb[3]);
        turtle.setPose(startingPos,PApplet.radians(90),p,plt);
        turtle.render(lsys,p,plt);
    }

    @Override
    public void keyPressed(PApplet parent) {

    }

    @Override
    public void mousePressed(PApplet parent) {
        //System.out.println(lsys.getSequence());
        lsys.nextGeneration();
        turtle.scaling(0.5f);
    }

    @Override
    public void keyReleased(PApplet p) {

    }

    @Override
    public void mouseReleased(PApplet parent) {

    }

    @Override
    public void mouseDragged(PApplet parent) {

    }
}
