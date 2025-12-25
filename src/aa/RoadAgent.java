package aa;

import car.CivilCar;
import car.PoliceCar;
import city.Node;
import city.RoadNetwork;
import physics.ParticleSystem;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class RoadAgent extends Boid {

    protected final RoadNetwork net;
    protected final Random rng = new Random();

    protected int currentNodeId;
    protected int nextNodeId;
    protected int prevNodeId = -1;

    // velocidade (mundo/unidade por segundo)
    protected float speed = 1.5f;

    // steering (Reynolds)
    protected float maxForce = 4.0f;        // força
    protected float arriveRadius = 0.12f;   // distância de chegada
    protected float slowRadius = 0.60f;     // abrandar

    // snap para colar à estrada
    protected float roadSnapStrength = 1.0f;

    protected float stopTimer = 0f;

    // particles
    protected ParticleSystem driftParticles;
    protected boolean driftEnabled = false;

    // deteção de derrapagem
    protected PVector prevVel;
    protected float driftAngleTR = PApplet.radians(5);

    public RoadAgent(RoadNetwork net, int startNodeId, int color, float radiusWorld, PApplet p, SubPlot plt) {
        super(
                net.nodes.get(startNodeId).pos.copy(),
                new PVector(1, 0),
                1f,
                radiusWorld,
                color,
                new DNA(new float[]{4f, 4f}),
                p,
                plt
        );

        this.net = net;
        this.currentNodeId = startNodeId;

        setMaxSpeed(speed);

        // escolher o 1o destino
        this.nextNodeId = startNodeId;
        chooseNextNode();

        prevVel = getVel().copy();

        driftParticles = new ParticleSystem(
                getPos().copy(),
                new PVector(0, 0),
                1f,
                0.02f,
                p.color(120),
                0.4f,
                new PVector(0.06f, 0.10f)
        );
        driftParticles.setEmitting(false);

    }

    public void setSpeed(float s) {
        this.speed = s;
        setMaxSpeed(s); // steering usa maxSpeed do Boid
    }

    public void stopFor(float seconds) {
        stopTimer = Math.max(stopTimer, seconds);
        setVel(new PVector(0, 0));
    }

    public boolean isStopped() {
        return stopTimer > 0f;
    }

    public void update(float dt, List<CivilCar> civils, List<PoliceCar> polices) {

        // parado
        if (stopTimer > 0f) {
            stopTimer -= dt;
            if (stopTimer < 0f) stopTimer = 0f;
            setVel(new PVector(0, 0));

            // particulas nao param quando o carro para
            if (driftParticles != null) {
                driftParticles.setEmitting(false);
                driftParticles.move(dt);
            }
            return;
        }

        Node a = net.nodes.get(currentNodeId);
        Node b = net.nodes.get(nextNodeId);

        // steering para o nó destino
        PVector steer = arrive(b.pos);
        applyForce(steer);

        // mover
        move(dt);

        // derrapagem
        if (driftParticles != null) {

            PVector v = getVel().copy();
            boolean drifting = false;

            if (driftEnabled) {
                if (v.mag() > 1e-4f && prevVel.mag() > 1e-4f) {
                    float angle = PVector.angleBetween(prevVel, v);
                    drifting = angle > driftAngleTR;
                }

                if (v.mag() > 1e-4f) {
                    PVector backDir = v.copy().normalize().mult(-1);
                    PVector emitterPos = PVector.add(getPos(), backDir.copy().mult(radius * 1.1f));
                    driftParticles.setPos(emitterPos);
                    driftParticles.setEmitDir(backDir);
                }
            }

            driftParticles.setEmitting(drifting);
            driftParticles.move(dt);
            prevVel = v.copy();
        }


        // força adicional para o segmento da estrada
        PVector snapped = closestPointOnSegment(getPos(), a.pos, b.pos);
        PVector offset = PVector.sub(snapped, getPos());
        PVector roadForce = offset.mult(maxForce * roadSnapStrength);
        applyForce(roadForce);

        // quando chegou ao nó destino, troca de segmento
        float dToB = PVector.dist(getPos(), b.pos);
        if (dToB <= arriveRadius) {
            prevNodeId = currentNodeId;
            currentNodeId = nextNodeId;
            chooseNextNode(civils, polices);
        }
    }

    @Override
    public void display(PApplet p, SubPlot plt) {
        if (driftParticles != null) driftParticles.display(p, plt);
        super.display(p, plt);
    }


    // default (patrulhamento aleatorio)
    protected void chooseNextNode() {
        nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
    }

    // funcao de decisao dos agentes
    protected abstract void chooseNextNode(List<CivilCar> civils, List<PoliceCar> polices);

    protected PVector arrive(PVector target) {
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

    protected int randomNeighbor(int nodeId) {
        var nbs = net.neighbors(nodeId);
        if (nbs.isEmpty()) return nodeId;
        if (nbs.size() == 1) return nbs.get(0);

        int pick;
        int tries = 0;
        do {
            pick = nbs.get(rng.nextInt(nbs.size()));
            tries++;
        } while (pick == prevNodeId && tries < 10);

        return pick;
    }

    protected int randomNeighborPreferLonger(int nodeId, float minLen) {
        var nbs = net.neighbors(nodeId);
        if (nbs.isEmpty()) return nodeId;

        Node a = net.nodes.get(nodeId);

        ArrayList<Integer> candidates = new ArrayList<>();
        for (int nb : nbs) {
            if (nb == prevNodeId) continue;
            float len = PVector.dist(a.pos, net.nodes.get(nb).pos);
            if (len >= minLen) candidates.add(nb);
        }

        if (candidates.isEmpty()) return randomNeighbor(nodeId);
        return candidates.get(rng.nextInt(candidates.size()));
    }

    // retorna ponto mais próximo de P num segmento AB
    private PVector closestPointOnSegment(PVector p, PVector a, PVector b) {
        PVector ab = PVector.sub(b, a);
        float ab2 = ab.dot(ab);
        if (ab2 < 1e-8f) return a.copy();

        float t = PVector.sub(p, a).dot(ab) / ab2;
        t = PApplet.constrain(t, 0f, 1f);

        return PVector.add(a, PVector.mult(ab, t));
    }

    // Função para ativar ou desativar a derrapagem
    public void setDriftEnabled(boolean enabled) {
        this.driftEnabled = enabled;
        if (!enabled && driftParticles != null) {
            driftParticles.setEmitting(false);
        }
    }

}
