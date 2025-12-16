package car;

import aa.RoadAgent;
import city.RoadNetwork;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.List;
import java.util.Random;

public class CivilCar extends RoadAgent {

    public enum CivilState { LEGAL, ILEGAL }

    public float visionRadius = 1.0f;
    public float separationRadius = 0.6f;

    // --- estados/velocidades/cores ---
    public CivilState state = CivilState.LEGAL;

    private final int colorLegal;
    private final int colorIlegal;
    private final int colorScape;

    private final float speedLegal = 0.8f;
    private final float speedIlegal = 2.2f;

    private final Random r = new Random();
    public float chanceIllegalPerSecond = 0.03f;

    public CivilCar(RoadNetwork net, int startNodeId, int color, PApplet p, SubPlot plt) {
        super(net, startNodeId, color, 0.11f, p, plt);

        colorLegal = p.color(0, 200, 0);      // verde
        colorIlegal = p.color(240, 200, 0);   // amarelo
        colorScape = p.color(236, 126, 28);   // laranja

        setState(CivilState.LEGAL);

    }

    public boolean isIllegal() {
        return state == CivilState.ILEGAL;
    }

    public void setState(CivilState s) {
        state = s;
        if (state == CivilState.LEGAL) {
            setColor(colorLegal);
            setSpeed(speedLegal);
        } else {
            setColor(colorIlegal);
            setSpeed(speedIlegal);
        }
    }

    @Override
    public void update(float dt, List<CivilCar> civils, List<PoliceCar> polices) {
        // 1) aleatoriamente virar ilegal
        if (state == CivilState.LEGAL && !hasPoliceInRadius(polices, visionRadius) && r.nextFloat() < chanceIllegalPerSecond * dt) {
            setState(CivilState.ILEGAL);
        }

        // 2) movimento normal na rede
        super.update(dt, civils, polices);
    }

    @Override
    protected void chooseNextNode(List<CivilCar> civils, List<PoliceCar> polices) {

        // Se não houver policias -> andar aleatório
        if (polices == null || polices.isEmpty()) {
            nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
            return;
        }

        // Se não há policia perto -> comportamento normal (evita calcular nearest sempre)
        if (!hasPoliceInRadius(polices, visionRadius)) {
            if (state == CivilState.ILEGAL) setColor(colorIlegal); // ilegal “calmo”
            nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
            return;
        }

        // encontrar polícia mais próxima (já sabemos que existe pelo menos uma no raio)
        PoliceCar nearest = getNearestPolice(polices);
        float bestD = (nearest == null) ? Float.POSITIVE_INFINITY : PVector.dist(getPos(), nearest.getPos());

        // --- CIVIL LEGAL ---
        if (state == CivilState.LEGAL) {
            if (nearest != null && nearest.isPursuing() && bestD <= visionRadius) {
                // polícia perto (com sirenes) → pára
                stopFor(1.5f);
            }
            nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
            return;
        }

        // --- CIVIL ILEGAL ---
        // Se a polícia afinal já não estiver perto (redundante/seguro)
        if (nearest == null || bestD > visionRadius) {
            setColor(colorIlegal);
            nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
            return;
        }

        // polícia perto -> fugir (laranja)
        setColor(colorScape);

        // fugir: maximizar distância à polícia
        int bestNode = -1;
        float bestScore = -Float.POSITIVE_INFINITY;

        for (int nb : net.neighbors(currentNodeId)) {
            if (nb == prevNodeId) continue;

            float score = PVector.dist(net.nodes.get(nb).pos, nearest.getPos());

            // evitar congestionamento (separation simples)
            if (civils != null) {
                for (CivilCar other : civils) {
                    if (other == this) continue;
                    float d = PVector.dist(net.nodes.get(nb).pos, other.getPos());
                    if (d < separationRadius) {
                        score -= (separationRadius - d) * 2f;
                    }
                }
            }

            if (score > bestScore) {
                bestScore = score;
                bestNode = nb;
            }
        }

        nextNodeId = (bestNode != -1)
                ? bestNode
                : randomNeighborPreferLonger(currentNodeId, 0.20f);
    }


    // Verificar se há policia perto
    private boolean hasPoliceInRadius(List<PoliceCar> polices, float radius) {
        if (polices == null || polices.isEmpty()) return false;

        PVector myPos = getPos();
        for (PoliceCar pc : polices) {
            if (PVector.dist(myPos, pc.getPos()) <= radius) {
                return true;
            }
        }
        return false;
    }

    private PoliceCar getNearestPolice(List<PoliceCar> polices) {
        if (polices == null || polices.isEmpty()) return null;

        PoliceCar best = null;
        float bestD = Float.POSITIVE_INFINITY;
        PVector myPos = getPos();

        for (PoliceCar pc : polices) {
            float d = PVector.dist(myPos, pc.getPos());
            if (d < bestD) {
                bestD = d;
                best = pc;
            }
        }
        return best;
    }



}
