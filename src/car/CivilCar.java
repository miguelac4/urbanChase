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

    // estado do agente (cor; velocidade; comportamento)
    public CivilState state = CivilState.LEGAL;

    private final int colorLegal;
    private final int colorIlegal;
    private final int colorScape;

    private final float speedLegal = 0.8f;
    private final float speedIlegal = 2.2f;

    private final Random r = new Random();
    public float chanceIllegalPerSecond;

    // Ilegal durante x segundos
    private float illegalTimer = 0f;        // currentTime
    private float illegalDuration = 0f;     // random (60..90)
    private boolean revertPending = false;  // duracao acabou mas encontra-se em fuga

    public CivilCar(RoadNetwork net, int startNodeId, int color, float chanceIllegalPerSecond, PApplet p, SubPlot plt) {
        super(net, startNodeId, color, 0.11f, p, plt);
        this.chanceIllegalPerSecond = chanceIllegalPerSecond;

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

            illegalTimer = 0f;
            illegalDuration = 0f;
            revertPending = false;
        } else {
            setColor(colorIlegal);
            setSpeed(speedIlegal);

            illegalTimer = 0f;
            illegalDuration = 60f + r.nextFloat() * 30f; // 60–90s
            revertPending = false;
        }
    }

    @Override
    public void update(float dt, List<CivilCar> civils, List<PoliceCar> polices) {
        // aleatoriamente virar ilegal se não houver policia por perto
        if (state == CivilState.LEGAL && !hasPoliceInRadius(polices, visionRadius) && r.nextFloat() < chanceIllegalPerSecond * dt) {
            setState(CivilState.ILEGAL);
        }

        // Contar o tempo para voltar a legal
        if (state == CivilState.ILEGAL) {
            illegalTimer += dt;

            boolean policeNear = hasPoliceInRadius(polices, visionRadius);

            if (illegalTimer >= illegalDuration) {
                if (!policeNear) {
                    // volta para legal
                    setState(CivilState.LEGAL);
                } else {
                    // expirou o tempo mas encontra-se em fuga
                    revertPending = true; // marca a variavel booleana para voltar para legal quando terminar a fuga
                }
            }

            // acabou a fuga, volta a legal
            if (revertPending && !policeNear) {
                setState(CivilState.LEGAL);
            }
        }

        // derrapar sempre que o estado é ilegal
        setDriftEnabled(state == CivilState.ILEGAL);

        // movimento normal na rede
        super.update(dt, civils, polices);
    }

    // COMPORTAMENTOS
    @Override
    protected void chooseNextNode(List<CivilCar> civils, List<PoliceCar> polices) {

        // Se não houver policias na cidade -> andar aleatório
        // Usar caso seja necessario evitar erros que envolvam null de policias
        //if (polices == null || polices.isEmpty()) {
        //    nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
        //    return;
        //}

        boolean policeNear = hasPoliceInRadius(polices, visionRadius);

        // Se não há policia perto -> comportamento normal (evita calcular nearest sempre)
        if (!policeNear) {
            if (state == CivilState.ILEGAL) setColor(colorIlegal); // ilegal “calmo”
            nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
            return;
        }

        // se houver pelo menos um policia no raio -> encontrar polícia mais próxima
        PoliceCar nearest = getNearestPolice(polices);
        float bestD = (nearest == null) ? Float.POSITIVE_INFINITY : PVector.dist(getPos(), nearest.getPos());

        // CIVIL LEGAL
        if (state == CivilState.LEGAL) {
            if (nearest != null && nearest.isPursuing() && bestD <= visionRadius) {
                // polícia em perseguição perto → pára
                stopFor(1.5f);
            }
            nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
            return;
        }

        // CIVIL ILEGAL
        // Se a polícia afinal já não estiver perto (redundante/seguro)
        if (nearest == null || bestD > visionRadius) {
            setColor(colorIlegal);
            nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
            return;
        }

        // polícia perto -> fugir (laranja)
        setColor(colorScape);

        // (maximizar distância à polícia)
        int bestNode = -1;
        float bestScore = -Float.POSITIVE_INFINITY;

        for (int nb : net.neighbors(currentNodeId)) {
            if (nb == prevNodeId) continue;

            float score = PVector.dist(net.nodes.get(nb).pos, nearest.getPos());

            if (score > bestScore) {
                bestScore = score;
                bestNode = nb;
            }
        }

        nextNodeId = (bestNode != -1)
                ? bestNode
                : randomNeighborPreferLonger(currentNodeId, 0.20f);
    }


    // Verificar se há policia no raio de visão do civil
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

    // Devolve o policia mais proximo
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
