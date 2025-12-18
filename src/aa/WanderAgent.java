package aa;

import city.Node;
import city.RoadNetwork;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.List;
import java.util.Random;

public class WanderAgent extends Boid {

    private final RoadNetwork net;
    private final Random rng = new Random();

    private int currentNodeId;
    private int nextNodeId;

    private float speed = 1.2f;

    // steering Reynolds
    private float maxForce = 6.0f;
    private float arriveRadius = 0.12f;
    private float slowRadius = 0.60f;

    // snap à estrada
    private float roadSnapStrength = 1.0f;

    public WanderAgent(RoadNetwork net, int startNodeId, int color, PApplet p, SubPlot plt) {
        super(
                net.nodes.get(startNodeId).pos.copy(),
                new PVector(1, 0),
                1f,
                0.08f,
                color,
                new DNA(new float[]{4f, 4f}),
                p,
                plt
        );

        this.net = net;
        this.currentNodeId = startNodeId;

        // steering usa o maxSpeed do Boid
        setSpeed(speed);

        pickNextNode();
    }

    public void setSpeed(float s) {
        this.speed = s;
        setMaxSpeed(s);
    }

    private void pickNextNode() {
        List<Integer> nbs = net.neighbors(currentNodeId);
        if (nbs == null || nbs.isEmpty()) {
            nextNodeId = currentNodeId;
        } else {
            nextNodeId = nbs.get(rng.nextInt(nbs.size()));
        }
    }

    public void update(float dt) {
        Node a = net.nodes.get(currentNodeId);
        Node b = net.nodes.get(nextNodeId);

        // steering para o nó destino (arrive)
        PVector steer = arrive(b.pos);
        applyForce(steer);

        // mover com a fisica
        move(dt);

        // força adicional para o segmento da estrada
        PVector snapped = closestPointOnSegment(getPos(), a.pos, b.pos);
        PVector offset = PVector.sub(snapped, getPos());
        PVector roadForce = offset.mult(maxForce * roadSnapStrength);
        applyForce(roadForce);

        // chegou ao nó
        float dToB = PVector.dist(getPos(), b.pos);
        if (dToB <= arriveRadius) {
            currentNodeId = nextNodeId;
            pickNextNode();
        }
    }

    private PVector arrive(PVector target) {
        PVector desired = PVector.sub(target, getPos());
        float d = desired.mag();
        if (d < 1e-6f) return new PVector(0, 0);

        desired.normalize();

        float targetSpeed = getMaxSpeed();
        if (d < slowRadius) {
            targetSpeed = PApplet.map(d, 0, slowRadius, 0, getMaxSpeed());
        }
        desired.mult(targetSpeed);

        PVector steer = PVector.sub(desired, getVel());
        steer.limit(maxForce);
        return steer;
    }

    private PVector closestPointOnSegment(PVector p, PVector a, PVector b) {
        PVector ab = PVector.sub(b, a);
        float ab2 = ab.dot(ab);
        if (ab2 < 1e-8f) return a.copy();

        float t = PVector.sub(p, a).dot(ab) / ab2;
        t = PApplet.constrain(t, 0f, 1f);

        return PVector.add(a, PVector.mult(ab, t));
    }
}
