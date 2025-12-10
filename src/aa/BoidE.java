package aa;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;

enum BoidTypeE {
    PREY,
    PREDATOR
}

enum BoidStateE {
    NORMAL,
    EXPLODING,
    DEAD
}

public class BoidE {
    private static final Random rand = new Random();

    public Vector2E position;
    public Vector2E velocity;
    public Vector2E acceleration;

    public BoidTypeE type;
    public BoidStateE state;

    public double maxSpeed;
    public double maxForce;

    public double visionRadius = 80;
    public double separationRadius = 30;

    public double explosionTimer;
    public double explosionTimeLeft;
    public double explosionDuration = 0.6;

    public Color baseColor = Color.WHITE;

    public BoidE(double x, double y, BoidTypeE type) {
        this.position = new Vector2E(x, y);

        this.velocity = new Vector2E(
                rand.nextDouble() * 2 - 1,
                rand.nextDouble() * 2 - 1
        ).normalized().mul(50);

        this.acceleration = new Vector2E(0, 0);
        this.type = type;
        this.state = BoidStateE.NORMAL;

        if (type == BoidTypeE.PREDATOR) {
            maxSpeed = 140;
            maxForce = 120;
            baseColor = Color.ORANGE;
            visionRadius = 150;
        } else {
            maxSpeed = 100;
            maxForce = 80;
            baseColor = Color.WHITE;
        }

        explosionTimer = 5 + rand.nextDouble() * 10;
        explosionTimeLeft = 0;
    }

    public boolean isAlive() {
        return state != BoidStateE.DEAD;
    }

    public void applyForce(Vector2E f) {
        acceleration = acceleration.add(f);
    }

    public void update(float dt, int width, int height) {
        if (state == BoidStateE.DEAD) return;

        if (state == BoidStateE.NORMAL) {
            explosionTimer -= dt;
            if (explosionTimer <= 0) {
                startExplosion();
            }
        } else if (state == BoidStateE.EXPLODING) {
            explosionTimeLeft -= dt;
            if (explosionTimeLeft <= 0) {
                state = BoidStateE.DEAD;
                return;
            }
        }

        velocity = velocity.add(acceleration.mul(dt));
        velocity = velocity.limit(maxSpeed);
        position = position.add(velocity.mul(dt));
        acceleration = new Vector2E(0, 0);

        if (position.x < 0) position.x += width;
        if (position.x > width) position.x -= width;
        if (position.y < 0) position.y += height;
        if (position.y > height) position.y -= height;
    }

    public void startExplosion() {
        if (state == BoidStateE.NORMAL) {
            state = BoidStateE.EXPLODING;
            explosionTimeLeft = explosionDuration;
        }
    }

    public void hunt(List<BoidE> preyList) {
        if (type != BoidTypeE.PREDATOR || state != BoidStateE.NORMAL) return;

        BoidE closest = null;
        double closestDist = Double.MAX_VALUE;

        for (BoidE b : preyList) {
            if (!b.isAlive()) continue;
            double d = position.distance(b.position);
            if (d < closestDist) {
                closestDist = d;
                closest = b;
            }
        }

        if (closest != null) {
            Vector2E desired = closest.position.sub(position).normalized().mul(maxSpeed);
            Vector2E steering = desired.sub(velocity).limit(maxForce);
            applyForce(steering);
        }
    }

    public void flock(List<BoidE> allPrey,
                      BoidE predator,
                      BoidE leader,
                      boolean avoidPredator,
                      boolean followLeader) {

        if (type != BoidTypeE.PREY || state != BoidStateE.NORMAL) return;

        Vector2E sep = separation(allPrey).mul(1.5);
        Vector2E ali = alignment(allPrey).mul(1.0);
        Vector2E coh = cohesion(allPrey).mul(1.0);

        applyForce(sep);
        applyForce(ali);
        applyForce(coh);

        if (avoidPredator && predator != null && predator.isAlive()) {
            double d = position.distance(predator.position);
            double dangerRadius = 150;
            if (d < dangerRadius && d > 0) {
                Vector2E flee = position.sub(predator.position).normalized().mul(maxSpeed);
                Vector2E steering = flee.sub(velocity).limit(maxForce * 1.5);
                applyForce(steering);
            }
        }

        if (followLeader && leader != null && leader != this && leader.isAlive()) {
            Vector2E toLeader = leader.position.sub(position);
            Vector2E desired = toLeader.normalized().mul(maxSpeed);
            Vector2E steering = desired.sub(velocity).limit(maxForce * 0.7);
            applyForce(steering);
        }
    }

    private Vector2E separation(List<BoidE> boids) {
        Vector2E steering = new Vector2E(0, 0);
        int count = 0;

        for (BoidE other : boids) {
            if (other == this || !other.isAlive()) continue;
            double d = position.distance(other.position);
            if (d < separationRadius && d > 0) {
                Vector2E diff = position.sub(other.position).normalized().mul(1.0 / d);
                steering = steering.add(diff);
                count++;
            }
        }

        if (count > 0) steering = steering.mul(1.0 / count);

        if (steering.length() > 0) {
            steering = steering.normalized().mul(maxSpeed);
            steering = steering.sub(velocity).limit(maxForce);
        }

        return steering;
    }

    private Vector2E alignment(List<BoidE> boids) {
        Vector2E avgVel = new Vector2E(0, 0);
        int count = 0;

        for (BoidE other : boids) {
            if (other == this || !other.isAlive()) continue;
            double d = position.distance(other.position);
            if (d < visionRadius) {
                avgVel = avgVel.add(other.velocity);
                count++;
            }
        }

        if (count == 0) return new Vector2E(0, 0);

        avgVel = avgVel.mul(1.0 / count);
        avgVel = avgVel.normalized().mul(maxSpeed);
        Vector2E steering = avgVel.sub(velocity).limit(maxForce);
        return steering;
    }

    private Vector2E cohesion(List<BoidE> boids) {
        Vector2E center = new Vector2E(0, 0);
        int count = 0;

        for (BoidE other : boids) {
            if (other == this || !other.isAlive()) continue;
            double d = position.distance(other.position);
            if (d < visionRadius) {
                center = center.add(other.position);
                count++;
            }
        }

        if (count == 0) return new Vector2E(0, 0);

        center = center.mul(1.0 / count);
        Vector2E desired = center.sub(position).normalized().mul(maxSpeed);
        Vector2E steering = desired.sub(velocity).limit(maxForce);
        return steering;
    }

    public void draw(PApplet app,
                     boolean isLeader,
                     boolean isDebug,
                     boolean highlightedByDebug,
                     boolean showVisionCircle) {

        if (state == BoidStateE.DEAD) return;

        int size = 8;
        if (type == BoidTypeE.PREDATOR) size = 12;
        if (state == BoidStateE.EXPLODING) size = 20;

        Color c = baseColor;
        if (type == BoidTypeE.PREDATOR) c = Color.ORANGE;
        if (isLeader) c = Color.GREEN;
        if (highlightedByDebug) c = Color.RED;
        if (state == BoidStateE.EXPLODING) c = Color.YELLOW;

        app.pushStyle();
        app.noStroke();
        app.fill(c.getRed(), c.getGreen(), c.getBlue());

        double angle = Math.atan2(velocity.y, velocity.x);

        app.pushMatrix();
        app.translate((float) position.x, (float) position.y);
        app.rotate((float) angle);

        int half = size / 2;
        app.triangle(size, 0, -half, half, -half, -half);

        app.popMatrix();

        if (isDebug) {
            app.stroke(0, 255, 255, 150);
            app.noFill();
            app.line((float) position.x,
                    (float) position.y,
                    (float) (position.x + velocity.x),
                    (float) (position.y + velocity.y));

            if (showVisionCircle) {
                app.ellipse((float) position.x,
                        (float) position.y,
                        (float) (visionRadius * 2),
                        (float) (visionRadius * 2));
            }
        }

        app.popStyle();
    }
}
