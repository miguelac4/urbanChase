package aa;

import city.RoadNetwork;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.List;

public class PoliceCar extends RoadAgent {

    public float visionRadius = 3.0f;

    public PoliceCar(RoadNetwork net, int startNodeId, int color, PApplet p, SubPlot plt) {
        super(net, startNodeId, color, 0.13f, p, plt);
        this.speed = 2.0f;
    }

    @Override
    protected void chooseNextNode(List<CivilCar> civils, List<PoliceCar> polices) {
        if (civils == null || civils.isEmpty()) {
            nextNodeId = randomNeighbor(currentNodeId);
            return;
        }

        // alvo = civil mais próximo (dentro da visão)
        CivilCar target = null;
        float bestD = Float.POSITIVE_INFINITY;
        PVector myPos = getPos();

        for (CivilCar c : civils) {
            float d = PVector.dist(myPos, c.getPos());
            if (d < bestD) { bestD = d; target = c; }
        }

        if (target == null || bestD > visionRadius) {
            // patrulha
            nextNodeId = randomNeighbor(currentNodeId);
            return;
        }

        // perseguir: escolher vizinho que MINIMIZA distância ao alvo
        int bestNode = -1;
        float bestScore = Float.POSITIVE_INFINITY;

        for (int nb : net.neighbors(currentNodeId)) {
            float score = PVector.dist(net.nodes.get(nb).pos, target.getPos());
            if (score < bestScore) {
                bestScore = score;
                bestNode = nb;
            }
        }

        nextNodeId = (bestNode != -1) ? bestNode : randomNeighbor(currentNodeId);
    }
}
