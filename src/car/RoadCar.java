package car;

import aa.Boid;
import aa.DNA;
import city.Node;
import city.RoadNetwork;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.List;
import java.util.Random;

public class RoadCar extends Boid {

    private final RoadNetwork net;
    private final Random rng = new Random();

    private int currentNodeId;
    private int nextNodeId;

    private float t;                 // progresso no segmento [0..1]
    private float speed = 1.2f;      // unidades do mundo por segundo (ajusta)

    public RoadCar(RoadNetwork net, int startNodeId, int color, PApplet p, SubPlot plt) {
        super(
                net.nodes.get(startNodeId).pos.copy(),
                new PVector(1, 0),       // vel dummy, vamos controlar manualmente
                1f,
                0.08f,                   // radius maior para veres bem
                color,
                new DNA(new float[]{4f, 4f}),
                p,
                plt
        );
        this.net = net;
        this.currentNodeId = startNodeId;
        pickNextNode();
        t = 0;
        updatePoseFromSegment(); // garante pos/vel consistentes logo no início
    }

    private void pickNextNode() {
        List<Integer> nbs = net.neighbors(currentNodeId);
        if (nbs.isEmpty()) {
            nextNodeId = currentNodeId;
        } else {
            nextNodeId = nbs.get(rng.nextInt(nbs.size()));
        }
    }

    public void update(float dt) {
        Node a = net.nodes.get(currentNodeId);
        Node b = net.nodes.get(nextNodeId);

        float segLen = PVector.dist(a.pos, b.pos);
        if (segLen < 1e-6f) return;

        // avança no segmento: dt * (speed / length)
        t += (dt * speed) / segLen;

        if (t >= 1f) {
            // chegou ao nó B
            currentNodeId = nextNodeId;
            pickNextNode();
            t = 0f;
        }

        updatePoseFromSegment();
    }

    private void updatePoseFromSegment() {
        Node a = net.nodes.get(currentNodeId);
        Node b = net.nodes.get(nextNodeId);

        // posição interpolada
        PVector posNow = PVector.lerp(a.pos, b.pos, t);
        this.setPos(posNow);

        // velocidade = direção do segmento (para orientar o triângulo)
        PVector dir = PVector.sub(b.pos, a.pos);
        if (dir.mag() > 1e-6f) dir.normalize().mult(speed);
        this.setVel(dir);
    }
}
