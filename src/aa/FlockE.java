package aa;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import processing.core.PApplet;

public class FlockE {
    private final Random rand = new Random();

    public List<BoidE> prey = new ArrayList<>();
    public BoidE predator;
    public BoidE leader;
    public BoidE debugBoid;

    public int width;
    public int height;

    // toggles
    public boolean predatorOn = true;
    public boolean leaderOn = true;
    public boolean debugOn = true;
    public boolean showVisionCircle = true;


    private Set<BoidE> debugVisible = new HashSet<>();

    public FlockE(int width, int height) {
        this.width = width;
        this.height = height;
        reset();
    }

    public void reset() {
        prey.clear();
        predator = null;
        leader = null;
        debugBoid = null;
        debugVisible.clear();

        predatorOn = true;
        leaderOn = true;
        debugOn = true;
        showVisionCircle = true;

        int numPrey = 40;
        for (int i = 0; i < numPrey; i++) {
            double x = rand.nextDouble() * width;
            double y = rand.nextDouble() * height;
            BoidE b = new BoidE(x, y, BoidTypeE.PREY);
            prey.add(b);
        }

        if (!prey.isEmpty()) {
            leader = prey.get(0);
            leader.baseColor = Color.GREEN;
        }


        if (prey.size() > 1) {
            debugBoid = prey.get(1);
        }


        predator = new BoidE(width / 2.0, height / 2.0, BoidTypeE.PREDATOR);
    }

    public void update(float dt) {
        if (predatorOn && predator != null && predator.isAlive()) {
            predator.hunt(prey);
        }

        for (BoidE b : prey) {
            if (!b.isAlive()) continue;

            b.flock(
                    prey,
                    predatorOn ? predator : null,
                    leaderOn ? leader : null,
                    predatorOn,
                    leaderOn
            );
        }

        for (BoidE b : prey) {
            b.update(dt, width, height);
        }
        if (predator != null) {
            predator.update(dt, width, height);
        }

        updateDebugVisibility();
    }

    private void updateDebugVisibility() {
        debugVisible.clear();
        if (!debugOn || debugBoid == null || !debugBoid.isAlive()) return;

        double visionRadius = debugBoid.visionRadius;
        Vector2E dir = debugBoid.velocity;
        if (dir.length() == 0) dir = new Vector2E(1, 0);

        double maxAngle = Math.toRadians(90);

        for (BoidE other : prey) {
            if (other == debugBoid || !other.isAlive()) continue;
            double d = debugBoid.position.distance(other.position);
            if (d > visionRadius) continue;

            Vector2E toOther = other.position.sub(debugBoid.position);
            double angle = Vector2E.angleBetween(dir, toOther);
            if (angle < maxAngle / 2.0) {
                debugVisible.add(other);
            }
        }
    }

    public void draw(PApplet app) {
        app.background(0);

        for (BoidE b : prey) {
            boolean isLeader = (b == leader && leaderOn);
            boolean isDebug = (b == debugBoid && debugOn);
            boolean highlighted = debugVisible.contains(b);
            b.draw(app, isLeader, isDebug, highlighted, showVisionCircle);
        }

        if (predatorOn && predator != null && predator.isAlive()) {
            predator.draw(app, false, false, false, false); // predador sem círculo de visão
        }

        app.fill(255);
        app.noStroke();
        app.textAlign(PApplet.LEFT, PApplet.TOP);
        app.text("1: Predador " + (predatorOn ? "ON" : "OFF"), 10, 10);
        app.text("2: Circulo de visao (debug) " + (showVisionCircle ? "ON" : "OFF"), 10, 25);
        app.text("3: Lider (WASD) " + (leaderOn ? "ON" : "OFF"), 10, 40);
        app.text("4: Debug " + (debugOn ? "ON" : "OFF"), 10, 55);
        app.text("R: Reset", 10, 70);
        app.text("Boids vivos: " + countAlivePrey(), 10, 85);

    }

    private int countAlivePrey() {
        int c = 0;
        for (BoidE b : prey) {
            if (b.isAlive()) c++;
        }
        return c;
    }
}
