package aa;

import city.Node;
import city.RoadNetwork;
import physics.ParticleSystem;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.List;
import java.util.Random;

public class ParticleWanderAgent extends Boid {

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

    private ParticleSystem driftParticles;

    // deteção de derrapagem
    private PVector prevVel;
    private float driftAngleTR = PApplet.radians(15); // limiar para considerar derrapagem

    public ParticleWanderAgent(RoadNetwork net, int startNodeId, int color, PApplet p, SubPlot plt) {
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

        prevVel = getVel().copy();

        // Particulas
        driftParticles = new ParticleSystem(
                getPos().copy(), // posicao no carro inicial
                new PVector(0, 0),
                1f,
                0.02f, // raio
                p.color(120), // cor
                0.6f, // lifespan (segundos)
                new PVector(0.20f, 0.25f) // velocidade das partículas
        );
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

        PVector v = getVel().copy();

        boolean drifting = false;
        if (v.mag() > 1e-4f && prevVel.mag() > 1e-4f) {
            float angle = PVector.angleBetween(prevVel, v);
            drifting = angle > driftAngleTR;
        }

        if (v.mag() > 1e-4f) {
            PVector backDir = v.copy().normalize().mult(-1);

            // emissor atrás do carro
            PVector emitterPos = PVector.add(getPos(), backDir.copy().mult(radius * 1.8f));
            driftParticles.setPos(emitterPos);

            // mandar o jato para trás
            driftParticles.setEmitDir(backDir);
        }

        // liga/desliga emissão
        driftParticles.setEmitting(drifting);

        // atualizar
        driftParticles.move(dt);

        prevVel = v.copy();

        // chegou ao nó
        float dToB = PVector.dist(getPos(), b.pos);
        if (dToB <= arriveRadius) {
            currentNodeId = nextNodeId;
            pickNextNode();
        }
    }

    @Override
    public void display(PApplet p, SubPlot plt) {
        // desenhar as particulas primeiro para o carro ficar por cima
        if (driftParticles != null) driftParticles.display(p, plt);
        super.display(p, plt);
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
