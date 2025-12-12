package apps;

import city.Node;
import city.RoadNetwork;
import city.RoadSegment;
import city.RoadTurtle;
import fractals.LSystem;
import fractals.Rule;
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

    private RoadTurtle turtle1;
    private RoadTurtle turtle2;

    private ArrayList<RoadSegment> roads;
    private boolean built = false;

    private RoadNetwork net;

    private PVector origin1;
    private PVector origin2;

    private float baseLength = 0.3f;
    private float angle = (float)(Math.PI / 2);
    private int nGenerations = 4;

    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport, p.width, p.height);

        String axiom = "F";
        Rule[] rules = new Rule[] {
                new Rule('F', "F[+F]F[-F]F")
        };
        lsys = new LSystem(axiom, rules);

        // Geração fixa
        for (int i = 0; i < nGenerations; i++) {
            lsys.nextGeneration();
        }

        roads = new ArrayList<>();

        net = new RoadNetwork(0.01f);

        turtle1 = new RoadTurtle(baseLength, angle, roads);
        turtle2 = new RoadTurtle(baseLength, angle, roads);

        origin1 = new PVector(-2.0f, 0f);
        origin2 = new PVector(-0.2f, -2.0f);
    }

    @Override
    public void draw(PApplet p, float dt) {
        // construir uma vez
        if (!built) {
            roads.clear();

            // render apenas para gerar segmentos
            // 1ª turtle
            p.pushMatrix();
            turtle1.setPose(origin1, 0f, p, plt);
            turtle1.render(lsys, p, plt);
            p.popMatrix();

            // 2ª turle
            p.pushMatrix();
            turtle2.setPose(origin2, (float)(Math.PI / 2), p, plt);
            turtle2.render(lsys, p, plt);
            p.popMatrix();

            net.buildFromSegments(roads);

            System.out.println("Segmentos: " + roads.size() + " Nós: " + net.nodes.size());

            built = true;
        }

        p.background(220);
        p.stroke(30);
        p.strokeWeight(2);
        p.noFill();

        for (RoadSegment s : roads) {
            float[] a = plt.getPixelCoord(s.start.x, s.start.y);
            float[] b = plt.getPixelCoord(s.end.x, s.end.y);
            p.line(a[0], a[1], b[0], b[1]);
        }

        p.noStroke();
        p.fill(255, 0, 0);
        for (Node n : net.nodes) {
            float[] pp = plt.getPixelCoord(n.pos.x, n.pos.y);
            p.circle(pp[0], pp[1], 4);
        }
    }

    @Override public void keyPressed(PApplet p) {}
    @Override public void keyReleased(PApplet p) {}
    @Override public void mousePressed(PApplet p) {}
    @Override public void mouseReleased(PApplet p) {}
    @Override public void mouseDragged(PApplet p) {}

    public ArrayList<RoadSegment> getRoads() {
        return roads;
    }
}
