package aa;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

public abstract class BoidApp implements IProcessingApp {

    private Boid b;
    private double[] window = {-10, 10, -10, 10};
    private float[] viewport = {0, 0, 1, 1};
    private SubPlot plt;
    private DNA dna;
    private float[] maxSpeed = {4, 4};
    private PVector target;

    private boolean wDown, sDown = false;
    private int baseColor, fasterColor, slowerColor;

    @Override
    public void setup(PApplet parent) {
        plt = new SubPlot(window, viewport, parent.width, parent.height);
        dna = new DNA(maxSpeed);
        baseColor  = parent.color(0);
        fasterColor = parent.color(255, 0, 0);
        slowerColor = parent.color(0, 255, 0);
        b = new Boid(new PVector(), new PVector(), 1, 0.5f, baseColor, dna, parent, plt);
        target = new PVector();


        System.out.println(
                "COMANDOS:\n" +
                        "Espaço (Avança / Pára o Boid) \n" +
                        "W      (Acelera o Boid) \n" +
                        "S      (Desacelera o Boid) \n");
    }

    @Override
    public void draw(PApplet p, float dt) {
        p.background(255);

        if (wDown) {
            b.setColor(fasterColor);
        } else if (sDown) {
            b.setColor(slowerColor);
        } else {
            b.setColor(baseColor);
        }

        PVector f = b.seek(target);
        b.applyForce(f);
        b.move(dt);

        b.display(p, plt);
//        System.out.println(b.getVel());
    }

    @Override
    public void mousePressed(PApplet p) {

        double[] ww = plt.getWorldCoord(p.mouseX, p.mouseY);
        target.x = (float) ww[0];
        target.y = (float) ww[1];
//        System.out.println(p.mouseX);
//        System.out.println(p.mouseY);
    }

    @Override
    public void keyPressed(PApplet p) {
        if (p.key == 'w') {
            b.accelerate();
            wDown = true;
        } else if (p.key == 's') {
            b.brake();
            sDown = true;
        } else if (p.key == ' ') {
            if (b.isStopped) b.resume();
            else b.stop();
        }
    }

    @Override
    public void keyReleased(PApplet p) {
        if (p.key == 'w') {
            wDown = false;
        }
        if (p.key == 's') {
            sDown = false;
        }
    }

    @Override
    public void mouseReleased(PApplet p) {

    }

    @Override
    public void mouseDragged(PApplet p) {

    }
}
