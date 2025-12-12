package city;

import processing.core.PVector;

import java.util.*;

public class RoadNetwork {

    private final float eps; // “snap grid” para juntar pontos próximos

    public final ArrayList<Node> nodes = new ArrayList<>();
    public final HashMap<Integer, ArrayList<Integer>> adj = new HashMap<>();

    // mapeia chave do ponto “quantizado” -> id do nó
    private final HashMap<String, Integer> nodeIndex = new HashMap<>();

    public RoadNetwork(float eps) {
        this.eps = eps;
    }

    public void buildFromSegments(List<RoadSegment> segments) {
        nodes.clear();
        adj.clear();
        nodeIndex.clear();

        for (RoadSegment s : segments) {
            int a = getOrCreateNode(s.start);
            int b = getOrCreateNode(s.end);
            addUndirectedEdge(a, b);
        }
    }

    public List<Integer> neighbors(int nodeId) {
        return adj.getOrDefault(nodeId, new ArrayList<>());
    }

    public int getRandomNeighbor(int nodeId, Random rng) {
        List<Integer> nbs = neighbors(nodeId);
        if (nbs.isEmpty()) return nodeId;
        return nbs.get(rng.nextInt(nbs.size()));
    }

    public int findNearestNode(PVector p) {
        int best = -1;
        float bestD2 = Float.POSITIVE_INFINITY;

        for (Node n : nodes) {
            float dx = n.pos.x - p.x;
            float dy = n.pos.y - p.y;
            float d2 = dx*dx + dy*dy;
            if (d2 < bestD2) {
                bestD2 = d2;
                best = n.id;
            }
        }
        return best;
    }

    public int degree(int nodeId) {
        return neighbors(nodeId).size();
    }

    // ------------------ Internos ------------------

    private int getOrCreateNode(PVector p) {
        String key = keyPoint(p);
        Integer id = nodeIndex.get(key);
        if (id != null) return id;

        int newId = nodes.size();
        PVector snapped = snappedPoint(p);

        nodes.add(new Node(newId, snapped));
        nodeIndex.put(key, newId);
        adj.put(newId, new ArrayList<>());

        return newId;
    }

    private void addUndirectedEdge(int a, int b) {
        if (a == b) return;

        ArrayList<Integer> la = adj.get(a);
        if (!la.contains(b)) la.add(b);

        ArrayList<Integer> lb = adj.get(b);
        if (!lb.contains(a)) lb.add(a);
    }

    private String keyPoint(PVector p) {
        long x = Math.round(p.x / eps);
        long y = Math.round(p.y / eps);
        return x + "," + y;
    }

    private PVector snappedPoint(PVector p) {
        float x = Math.round(p.x / eps) * eps;
        float y = Math.round(p.y / eps) * eps;
        return new PVector(x, y);
    }
}
