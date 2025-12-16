package aa;

import car.CivilCar;
import car.PoliceCar;
import city.Node;
import city.RoadNetwork;
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

    protected float t;            // progresso no segmento [0..1]
    protected float speed = 1.5f; // unidades do mundo por segundo

    protected int prevNodeId = -1; // Guardar ultimo nó em memoria

    public RoadAgent(RoadNetwork net, int startNodeId, int color, float radiusWorld, PApplet p, SubPlot plt) {
        super(
                net.nodes.get(startNodeId).pos.copy(),
                new PVector(1, 0),
                1f,
                radiusWorld,
                color,
                new DNA(new float[]{4f, 4f}), // não estamos a usar DNA aqui ainda
                p,
                plt
        );
        this.net = net;
        this.currentNodeId = startNodeId;

        // escolhe 1º destino
        this.nextNodeId = startNodeId;
        chooseNextNode();
        this.t = 0f;
        updatePoseFromSegment();
    }

    public void setSpeed(float s) { this.speed = s; }

    public void update(float dt, List<CivilCar> civils, List<PoliceCar> polices) {
        Node a = net.nodes.get(currentNodeId);
        Node b = net.nodes.get(nextNodeId);

        float segLen = PVector.dist(a.pos, b.pos);
        if (segLen < 1e-6f) return;

        t += (dt * speed) / segLen;

        if (t >= 1f) {
            // chegou ao nó destino
            t = 0f;
            prevNodeId = currentNodeId;
            currentNodeId = nextNodeId;
            chooseNextNode(civils, polices);
        }

        updatePoseFromSegment();
    }

    protected void chooseNextNode() {
        nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
    }

    // cada agente implementa esta decisão
    protected abstract void chooseNextNode(List<CivilCar> civils, List<PoliceCar> polices);

    protected void updatePoseFromSegment() {
        Node a = net.nodes.get(currentNodeId);
        Node b = net.nodes.get(nextNodeId);

        PVector posNow = PVector.lerp(a.pos, b.pos, t);
        setPos(posNow);

        PVector dir = PVector.sub(b.pos, a.pos);
        if (dir.mag() > 1e-6f) dir.normalize().mult(speed);
        setVel(dir);
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
        } while (pick == prevNodeId && tries < 10); // Não deixa que o mesmo nó seja escolhido de forma seguida

        return pick;
    }

    protected float distToNode(int nodeId, PVector pos) {
        PVector npos = net.nodes.get(nodeId).pos;
        return PVector.dist(npos, pos);
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

}
