package aa;

import physics.Body;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;
import tools.SubPlot;

public class Boid extends Body {

    private SubPlot plt;
    private PShape shape;
    private float maxSpeed = 4.0f;
    boolean isStopped = false;
    private int col;  // cor atual do boid

    protected Boid(PVector pos, PVector vel, float mass, float radius, int color, DNA dna, PApplet p, SubPlot plt) {
        super(pos, vel, mass, radius, color);
        this.plt = plt;
        this.col = color;
        setShape(p, plt);
    }

    public void setColor(int c){ this.col = c; }

    public void setStopped(boolean b){
        this.isStopped = b;
    }

    public void setShape(PApplet p, SubPlot plt){
        float[] rr = plt.getDimInPixel(radius, radius);
        shape = p.createShape();
        shape.beginShape();
        shape.vertex(-rr[0], rr[0]/2);
        shape.vertex(rr[0], 0);
        shape.vertex(-rr[0], -rr[0]/2);
        shape.vertex(-rr[0]/2, 0);
        shape.endShape(PConstants.CLOSE);
        shape.disableStyle(); // Para possibilitar mudar a cor
    }

    public PVector seek(PVector target){
        PVector vd = PVector.sub(target, pos);
        vd.normalize().mult(maxSpeed);
        return PVector.sub(vd, vel);
    }

    public void accelerate() {
        float speed = 2;
        if (!isStopped && (speed < maxSpeed)) {
            this.vel = this.vel.mult(speed);
        }

//            vel.add(PVector.fromAngle(vel.heading()).mult(acceleration));
//            vel.limit(maxSpeed);
    }

    public void brake() {
        float speed = 2;
        if (!isStopped) {
            this.vel = this.vel.div(speed);
        }
//        vel.sub(PVector.fromAngle(vel.heading()).mult(brake));
//        vel.limit(maxSpeed);

    }

    public void stop() {
        isStopped = true;
        vel.set(0, 0);
        acc.set(0, 0);

    }

    public void resume() {
        isStopped = false;
        if (vel.mag() < 1e-6f) vel = PVector.random2D().mult(0.2f); // arranque suave

    }

    @Override
    public void move(float dt){
        if (isStopped) return;
        super.move(dt);
        vel.limit(maxSpeed); // Mantem a velocidade limit
    }


    @Override
    public void display(PApplet p , SubPlot plt){
        p.pushMatrix();
        p.pushStyle();
        float[] pp = plt.getPixelCoord(pos.x, pos.y);
        p.translate(pp[0], pp[1]);
        p.rotate(-vel.heading());
        p.fill(col);
        p.stroke(0);
        p.shape(shape);
        p.popStyle();
        p.popMatrix();
    }

    public PVector getPos(){ return pos.copy(); }
    public PVector getVel(){ return vel.copy(); }
    public void setPos(PVector p){ this.pos.set(p); }
    public void setVel(PVector v){ this.vel.set(v); }
}
