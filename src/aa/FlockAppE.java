package aa;

import processing.core.PApplet;
import setup.IProcessingApp;

public class FlockAppE implements IProcessingApp {

    private FlockE flock;

    @Override
    public void setup(PApplet app) {
        flock = new FlockE(app.width, app.height);
    }

    @Override
    public void draw(PApplet app, float dt) {
        flock.update(dt);
        flock.draw(app);
    }

    @Override
    public void keyPressed(PApplet app) {
        if (flock == null) return;

        char k = app.key;

        if (flock.leader != null && flock.leader.isAlive()) {
            // "impulso" mais forte para se notar bem
            double kick = flock.leader.maxSpeed * 0.6;

            if (k == 'w' || k == 'W') {
                flock.leader.velocity = flock.leader.velocity.add(new Vector2E(0, -kick));
            }
            if (k == 's' || k == 'S') {
                flock.leader.velocity = flock.leader.velocity.add(new Vector2E(0, kick));
            }
            if (k == 'a' || k == 'A') {
                flock.leader.velocity = flock.leader.velocity.add(new Vector2E(-kick, 0));
            }
            if (k == 'd' || k == 'D') {
                flock.leader.velocity = flock.leader.velocity.add(new Vector2E(kick, 0));
            }
        }

        if (k == '1') {
            flock.predatorOn = !flock.predatorOn;
        }
        if (k == '2') {
            flock.showVisionCircle = !flock.showVisionCircle;
        }
        if (k == '3') {
            flock.leaderOn = !flock.leaderOn;
        }
        if (k == '4') {
            flock.debugOn = !flock.debugOn;
        }
        if (k == 'r' || k == 'R') {
            flock.reset();
        }
    }

    @Override
    public void mousePressed(PApplet app) {

    }

    @Override
    public void keyReleased(PApplet p) {

    }

    @Override
    public void mouseReleased(PApplet p) {

    }

    @Override
    public void mouseDragged(PApplet p) {

    }
}
