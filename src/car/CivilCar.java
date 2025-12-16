package car;

import aa.RoadAgent;
import city.RoadNetwork;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.List;

public class CivilCar extends RoadAgent {

    // “genes” simples (por agora como parâmetros)
    public float visionRadius = 2.0f;      // em unidades de mundo
    public float separationRadius = 0.6f;  // evita ir para nós com carros muito perto
    public float courageDelay = 0.3f;      // segundos até começar a fugir (opcional)

    private float dangerTimer = 0f;        // acumula enquanto há polícia perto

    public CivilCar(RoadNetwork net, int startNodeId, int color, PApplet p, SubPlot plt) {
        super(net, startNodeId, color, 0.11f, p, plt);
        this.speed = 1.6f;
    }

    @Override
    protected void chooseNextNode(List<CivilCar> civils, List<PoliceCar> polices) {
        // regra base: se não houver polícia = random walk
        if (polices == null || polices.isEmpty()) {
            nextNodeId = randomNeighbor(currentNodeId);
            return;
        }

        // encontra polícia mais próxima
        PoliceCar nearest = null;
        float bestD = Float.POSITIVE_INFINITY;
        PVector myPos = getPos();

        for (PoliceCar pc : polices) {
            PVector pp = pc.getPos();
            float d = PVector.dist(myPos, pp);
            if (d < bestD) { bestD = d; nearest = pc; }
        }

        // se não está em perigo -> random
        if (nearest == null || bestD > visionRadius) {
            dangerTimer = 0f;
            nextNodeId = randomNeighbor(currentNodeId);
            return;
        }

        // está em perigo -> aplica “coragem” (atraso)
        dangerTimer += 1f; // (não temos dt aqui; simplificação)
        if (courageDelay > 0 && dangerTimer < courageDelay * 60f) { // ~60fps
            nextNodeId = randomNeighbor(currentNodeId);
            return;
        }

        // fugir: escolher o vizinho que MAXIMIZA distância à polícia
        int bestNode = -1;
        float bestScore = -Float.POSITIVE_INFINITY;

        for (int nb : net.neighbors(currentNodeId)) {
            if (nb == prevNodeId) continue;
            float score = PVector.dist(net.nodes.get(nb).pos, nearest.getPos());

            // penalizar “congestionamento” (separation simples): evitar nós com civis próximos
            if (civils != null) {
                for (CivilCar other : civils) {
                    if (other == this) continue;
                    float d = PVector.dist(net.nodes.get(nb).pos, other.getPos());
                    if (d < separationRadius) score -= (separationRadius - d) * 2.0f;
                }
            }

            if (score > bestScore) {
                bestScore = score;
                bestNode = nb;
            }
        }

        nextNodeId = (bestNode != -1) ? bestNode : randomNeighbor(currentNodeId);
    }
}
