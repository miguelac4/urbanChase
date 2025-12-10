package aa;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

import java.util.Random;

public abstract class BoidWanderApp implements IProcessingApp {

    private Boid b;
    private double[] window = {-10, 10, -10, 10};
    private float[] viewport = {0, 0, 1, 1};
    private SubPlot plt;
    private DNA dna;
    private float[] maxSpeed = {4, 4};
    private PVector target;
    private float wanderAngle = 0f;
    private float wanderJitter = 0.3f;   // variação pequena do ângulo
    private float wanderRadius = 2.0f;   // raio do “círculo de wander”
    private float wanderDistance = 4.0f; // distância do círculo à frente
    private float wanderInterval = 0.2f; // segundos entre updates
    private float wanderTimer = 0f;
    private final Random rng = new Random();

    private boolean showWander = false;
    private PVector dbgCircleCenter = new PVector(); // em coords de MUNDO
    private PVector dbgTarget = new PVector();       // idem

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
                        "S      (Desacelera o Boid) \n" +
                        "R      (Mostra Circulo de Reynolds)");
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

        PVector pw = b.getPos();
        if (pw.x <  window[0]) pw.x = (float) window[1];
        if (pw.x >  window[1]) pw.x = (float) window[0];
        if (pw.y <  window[2]) pw.y = (float) window[3];
        if (pw.y >  window[3]) pw.y = (float) window[2];
        b.setPos(pw);

        b.display(p, plt);

        if (showWander) {
            drawWanderOverlay(p);
        }

        wanderTimer += dt;
        if (wanderTimer >= wanderInterval) {
            wanderTimer = 0f;
            Wander(); // agora faz Reynolds
        }
    }

    @Override
    public void mousePressed(PApplet p) {

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
        } else if (p.key == 'r') {
        showWander = !showWander;   // ligar/desligar overlay
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

    public void Wander(){
        // 1) jitter no ângulo
        wanderAngle += (rng.nextFloat() * 2f - 1f) * wanderJitter; // [-jitter, +jitter]

        // 2) direção atual do boid
        PVector dir = b.getVel().copy();
        if (dir.mag() < 1e-6f) dir = new PVector(1, 0); // fallback se parado
        dir.normalize();

        // 3) centro do círculo + offset girado pelo heading
        PVector circleCenter = PVector.mult(dir, wanderDistance);
        float heading = (float)Math.atan2(dir.y, dir.x);
        PVector circleOffset = new PVector(
                (float)Math.cos(heading + wanderAngle),
                (float)Math.sin(heading + wanderAngle)
        ).mult(wanderRadius);

        // 4) novo target em coords de MUNDO
        PVector worldPos = b.getPos();
        dbgCircleCenter = PVector.add(worldPos, circleCenter); // guardar centro p/ desenhar
        dbgTarget = PVector.add(worldPos, PVector.add(circleCenter, circleOffset));


        target = dbgTarget.copy();
    }

    private void drawWanderOverlay(PApplet p){
        // converter mundo->pixel
        float[] ppBoid = plt.getPixelCoord(b.getPos().x, b.getPos().y);
        float[] ppCenter = plt.getPixelCoord(dbgCircleCenter.x, dbgCircleCenter.y);
        float[] ppTarget = plt.getPixelCoord(dbgTarget.x, dbgTarget.y);

        // raio do círculo em píxeis (usar escala do SubPlot)
        float[] rr = plt.getDimInPixel(wanderRadius, wanderRadius);
        float rpx = rr[0];

        p.pushStyle();
        p.noFill();
        p.stroke(0, 150);       // cinza
        p.ellipseMode(PApplet.CENTER);

        // linha do boid até ao centro do círculo
        p.line(ppBoid[0], ppBoid[1], ppCenter[0], ppCenter[1]);

        // círculo de wander
        p.ellipse(ppCenter[0], ppCenter[1], 2*rpx, 2*rpx);

        // alvo (ponto no círculo)
        p.stroke(0);
        p.fill(0);
        p.circle(ppTarget[0], ppTarget[1], 6);

        p.popStyle();
    }


}
