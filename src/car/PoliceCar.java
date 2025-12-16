package car;

import aa.RoadAgent;
import city.RoadNetwork;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.List;

public class PoliceCar extends RoadAgent {

    public float visionRadius = 2.0f;

    private boolean pursuing = false;
    private float sirenTimer = 0f;
    private final float sirenPeriod = 0.4f; // segundos

    private final int baseColor;   // cor normal
    private final int sirenBlue;   // azul sirene
    private final int sirenRed;    // vermelho sirene
    private boolean sirenBlueOn = true;

    private CivilCar currentTarget = null;
    public float captureRadius = 0.25f; // distância em unidades do mundo (ajusta)

    public float normalSpeed = 0.8f;
    public float pursuingSpeed = 2.0f;

    public PoliceCar(RoadNetwork net, int startNodeId, int color, PApplet p, SubPlot plt) {
        super(net, startNodeId, color, 0.13f, p, plt);
        this.speed = normalSpeed;

        this.baseColor = color;
        this.sirenBlue = p.color(0, 80, 255);
        this.sirenRed  = p.color(220, 0, 0);
    }

    @Override
    public void update(float dt, List<CivilCar> civils, List<PoliceCar> polices) {

        // se estiver parado (ex.: captura), deixa o RoadAgent tratar do timer
        // mas queremos manter cor normal quando parado
        if (isStopped()) {
            setColor(baseColor);
            super.update(dt, civils, polices);
            return;
        }

        // escolhe alvo ilegal mais próximo dentro da visão
        currentTarget = findNearestIllegalInRange(civils);

        pursuing = (currentTarget != null);

        // ajusta velocidade conforme o estado
        this.speed = pursuing ? pursuingSpeed : normalSpeed;


        // sirene só em perseguição
        updateSiren(dt);

        // se estiver perto o suficiente -> captura
        if (currentTarget != null) {
            float d = PVector.dist(getPos(), currentTarget.getPos());
            if (d <= captureRadius) {
                // ambos param 4s e o civil volta a legal
                this.stopFor(4f);
                currentTarget.stopFor(4f);
                currentTarget.setState(CivilCar.CivilState.LEGAL);
                currentTarget = null;
                pursuing = false;
            }
        }

        // movimento na rede
        super.update(dt, civils, polices);
    }


    private boolean hasTargetInRange(List<CivilCar> civils) {
        if (civils == null || civils.isEmpty()) return false;

        PVector myPos = getPos();
        float bestD = Float.POSITIVE_INFINITY;

        for (CivilCar c : civils) {
            float d = PVector.dist(myPos, c.getPos());
            if (d < bestD) bestD = d;
        }
        return bestD <= visionRadius;
    }

    private void updateSiren(float dt) {
        if (!pursuing) {
            // modo patrulha: cor fixa
            setColor(baseColor);
            sirenTimer = 0f;
            sirenBlueOn = true;
            return;
        }

        // modo perseguição: alternar azul/vermelho
        sirenTimer += dt;
        if (sirenTimer >= sirenPeriod) {
            sirenTimer -= sirenPeriod;
            sirenBlueOn = !sirenBlueOn;
        }
        setColor(sirenBlueOn ? sirenBlue : sirenRed);
    }

    @Override
    protected void chooseNextNode(List<CivilCar> civils, List<PoliceCar> polices) {

        // se não há alvo ilegal → patrulha
        if (currentTarget == null) {
            nextNodeId = randomNeighborPreferLonger(currentNodeId, 0.20f);
            return;
        }

        // perseguir: escolher vizinho que minimiza distância ao alvo
        int bestNode = -1;
        float bestScore = Float.POSITIVE_INFINITY;

        for (int nb : net.neighbors(currentNodeId)) {
            if (nb == prevNodeId) continue;

            float score = PVector.dist(net.nodes.get(nb).pos, currentTarget.getPos());
            if (score < bestScore) {
                bestScore = score;
                bestNode = nb;
            }
        }

        nextNodeId = (bestNode != -1)
                ? bestNode
                : randomNeighborPreferLonger(currentNodeId, 0.20f);
    }


    @Override
    public void display(PApplet p, SubPlot plt) {
        // desenhar o carro (triângulo)
        super.display(p, plt);

        // desenhar círculo do raio de visão (em unidades do mundo -> pixels)
        p.pushStyle();
        p.noFill();

        float[] pp = plt.getPixelCoord(getPos().x, getPos().y);
        float[] rr = plt.getDimInPixel(visionRadius, visionRadius);

        // opcional: só mostrar quando está em perseguição
        // if (!pursuing) { p.popStyle(); return; }

        p.stroke(0, 0, 0, 90);
        p.strokeWeight(1);
        p.circle(pp[0], pp[1], 2 * rr[0]);

        p.popStyle();
    }
    private CivilCar findNearestIllegalInRange(List<CivilCar> civils) {
        if (civils == null || civils.isEmpty()) return null;

        CivilCar best = null;
        float bestD = Float.POSITIVE_INFINITY;
        PVector myPos = getPos();

        for (CivilCar c : civils) {
            if (!c.isIllegal()) continue; // 👈 só ilegais
            float d = PVector.dist(myPos, c.getPos());
            if (d < bestD) {
                bestD = d;
                best = c;
            }
        }

        if (best == null) return null;
        return (bestD <= visionRadius) ? best : null;
    }

    public boolean isPursuing() {
        return pursuing;
    }



}
