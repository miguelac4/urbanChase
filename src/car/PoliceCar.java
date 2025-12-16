package car;

import aa.RoadAgent;
import city.RoadNetwork;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.List;

public class PoliceCar extends RoadAgent {

    public float visionRadius = 3.0f;

    private boolean pursuing = false;
    private float sirenTimer = 0f;
    private final float sirenPeriod = 0.4f; // segundos

    private final int baseColor;   // cor normal
    private final int sirenBlue;   // azul sirene
    private final int sirenRed;    // vermelho sirene
    private boolean sirenBlueOn = true;

    public PoliceCar(RoadNetwork net, int startNodeId, int color, PApplet p, SubPlot plt) {
        super(net, startNodeId, color, 0.13f, p, plt);
        this.speed = 2.0f;

        this.baseColor = color;
        this.sirenBlue = p.color(0, 80, 255);
        this.sirenRed  = p.color(220, 0, 0);
    }

    @Override
    public void update(float dt, List<CivilCar> civils, List<PoliceCar> polices) {
        // 1) atualiza estado de perseguição (tem alvo?)
        pursuing = hasTargetInRange(civils);

        // 2) atualiza sirene (cor)
        updateSiren(dt);

        // 3) movimento normal on-rails
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
            if (nb == prevNodeId) continue;
            float score = PVector.dist(net.nodes.get(nb).pos, target.getPos());
            if (score < bestScore) {
                bestScore = score;
                bestNode = nb;
            }
        }

        nextNodeId = (bestNode != -1) ? bestNode : randomNeighbor(currentNodeId);
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

}
