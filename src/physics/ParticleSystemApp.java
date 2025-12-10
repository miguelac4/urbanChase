package physics;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ParticleSystemApp implements IProcessingApp {

    private List<ParticleSystem> pss;
    private double[] window = {-10, 10, -10, 10};
    private float[] viewport = {0, 0, 1, 1};
    private SubPlot plt;

    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport, p.width, p.height);
        pss = new ArrayList<ParticleSystem>();
    }

    @Override
    public void draw(PApplet p, float dt) {
        p.background(255);

        Iterator<ParticleSystem> iterator = pss.iterator();
        while(iterator.hasNext()){
            ParticleSystem ps = iterator.next();
            ps.applyForce(new PVector(0, -1));
            ps.move(dt);
            ps.display(p, plt);
        }

//        for(ParticleSystem ps : pss){
//            ps.applyForce(new PVector(0, -1));
//        }
//
//
//        for(ParticleSystem ps : pss){
//            ps.move(dt);
//            ps.display(p, plt);
//        }

    }

    @Override
    public void mousePressed(PApplet p) {
        double[] ww = plt.getWorldCoord(p.mouseX, p.mouseY);

        int cor = p.color(p.random(255), p.random(255), p.random(255));
        float vx = p.random(4, 10);
        float vy = p.random(4, 10);
        float lifespan = p.random(1, 3);

        ParticleSystem ps = new ParticleSystem(new PVector((float)(ww[0]), (float)(ww[1])), new PVector(), 1f, .2f, cor, lifespan, new PVector(vx, vy));

        pss.add(ps);
    }

    @Override
    public void keyReleased(PApplet p) {

    }

    @Override
    public void keyPressed(PApplet p) {

    }
}
