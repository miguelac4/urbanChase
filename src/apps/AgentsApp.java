package apps;

import car.CivilCar;
import car.PoliceCar;
import city.Node;
import city.RoadNetwork;
import city.RoadSegment;
import city.RoadTurtle;
import fractals.LSystem;
import fractals.Rule;
import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import ui.MenuScreen;
import ui.StatsPanel;
import tools.SubPlot;

import java.util.ArrayList;

public class AgentsApp implements IProcessingApp {

    private SubPlot plt;
    private double[] window = {-2, 2, -2, 2};
    private float[] viewport = {0f, 0f, 1f, 1f};

    private LSystem lsys;

    private RoadTurtle turtle1;
    private RoadTurtle turtle2;

    private ArrayList<RoadSegment> roads;
    private boolean built = false;

    private RoadNetwork net;

    private PVector origin1;
    private PVector origin2;

    private float baseLength = 0.3f;
    private float angle = (float)(Math.PI / 2);
    private int nGenerations = 4;

    private ArrayList<CivilCar> civils;
    private ArrayList<PoliceCar> polices;

    private int numCivils;
    private int numPolices;
    private float chanceIllegalPerSecond;

    private int captures = 0; // opcional (vamos usar já)

    private double[] fullWindow;
    private float zoomFactor = 0.25f; // zoom

    // Estatisticas
    private boolean showStats = false;
    private float statsTimer = 0f;
    private float statsEvery = 0.5f; // imprimir x vezes por segundo
    private int statsNum = 0; // numero da estatistica
    private StatsPanel statsPanel;

    // Menu inicial
    private enum GameState { MENU, RUNNING }
    private GameState gameState = GameState.MENU;
    private MenuScreen menu;

    // botão voltar ao menu
    private float btnMenuX = 12;
    private float btnMenuY = 12;
    private float btnMenuW = 110;
    private float btnMenuH = 32;

    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport, p.width, p.height);

        String axiom = "F";
        Rule[] rules = new Rule[] {
                new Rule('F', "F[+F]F[-F]F")
        };
        lsys = new LSystem(axiom, rules);

        // Geração fixa
        for (int i = 0; i < nGenerations; i++) {
            lsys.nextGeneration();
        }

        roads = new ArrayList<>();

        net = new RoadNetwork(0.01f);

        turtle1 = new RoadTurtle(baseLength, angle, roads);
        turtle2 = new RoadTurtle(baseLength, angle, roads);

        origin1 = new PVector(-2.0f, 0f);
        origin2 = new PVector(-0.2f, -2.0f);

        civils = new ArrayList<>();
        polices = new ArrayList<>();

        statsPanel = new StatsPanel(300);

        menu = new MenuScreen();
    }

    @Override
    public void draw(PApplet p, float dt) {
        if (gameState == GameState.MENU) {
            menu.update(dt);
            menu.draw(p);
            return;
        }

        // construir uma vez
        if (!built) {
            roads.clear();

            // 1ª turtle
            p.pushMatrix();
            turtle1.setPose(origin1, 0f, p, plt);
            turtle1.render(lsys, p, plt);
            p.popMatrix();

            // 2ª turle
            p.pushMatrix();
            turtle2.setPose(origin2, (float)(Math.PI / 2), p, plt);
            turtle2.render(lsys, p, plt);
            p.popMatrix();

            net.buildFromSegments(roads);

            fitWindowToNetwork(p, 0.08f); // 8% margem

            float minX=1e9f, maxX=-1e9f, minY=1e9f, maxY=-1e9f;
            for (Node n : net.nodes) {
                minX = Math.min(minX, n.pos.x);
                maxX = Math.max(maxX, n.pos.x);
                minY = Math.min(minY, n.pos.y);
                maxY = Math.max(maxY, n.pos.y);
            }
            System.out.println("Node bounds: x[" + minX + "," + maxX + "] y[" + minY + "," + maxY + "]");


            civils.clear();
            polices.clear();

            // civis
            for (int i = 0; i < numCivils; i++) {
                int start = (int) p.random(net.nodes.size());
                CivilCar c = new CivilCar(net, start, p.color(0, 200, 0), chanceIllegalPerSecond, p, plt);
                // opcional: genes básicos
                civils.add(c);
            }

            // polícias
            for (int i = 0; i < numPolices; i++) {
                int start = (int) p.random(net.nodes.size());
                PoliceCar pc = new PoliceCar(net, start, p.color(0, 205, 255), p, plt);
                polices.add(pc);
            }

            captures = 0;



            System.out.println("Segmentos: " + roads.size() + " Nós: " + net.nodes.size());

            built = true;
        }

        p.background(220);
        p.stroke(30);
        p.strokeWeight(2);
        p.noFill();

        for (RoadSegment s : roads) {
            float[] a = plt.getPixelCoord(s.start.x, s.start.y);
            float[] b = plt.getPixelCoord(s.end.x, s.end.y);
            p.line(a[0], a[1], b[0], b[1]);
        }

        p.noStroke();
        p.fill(255, 0, 0);
//        for (Node n : net.nodes) {
//            float[] pp = plt.getPixelCoord(n.pos.x, n.pos.y);
//            p.circle(pp[0], pp[1], 4);
//        }

        // atualizar + desenhar civis
        for (CivilCar c : civils) {
            c.update(dt, civils, polices);
            c.display(p, plt);
        }

        // atualizar + desenhar polícias
        for (PoliceCar pc : polices) {
            pc.update(dt, civils, polices);
            pc.display(p, plt);
        }

        // ESTATISTICAS (prints)
        statsTimer += dt;
        if (statsTimer >= statsEvery) {
            statsTimer -= statsEvery;
            statsNum++;

            int civLegal = 0;
            int civIlegal = 0;
            int polNormal = 0;
            int pursuing = 0;

            for (CivilCar c : civils) {
                if (c.state == CivilCar.CivilState.LEGAL) civLegal++;
                else civIlegal++;
            }

            for (PoliceCar pc : polices) {
                if (pc.isPursuing()) pursuing++;
                else polNormal++;
            }

            System.out.println(
                    "[STATS " + statsNum + "] " +
                            "Civis LEGAL=" + civLegal +
                            " | Civis ILEGAL=" + civIlegal +
                            " | Policias NORMAL=" + polNormal +
                            " | Perseguicoes=" + pursuing
            );
            statsPanel.push(civLegal, civIlegal, polNormal, pursuing);
        }

        // apenas mostrar o painel se o showStats tiver ativo
        if (showStats) {
            int panelW = 320;
            int panelX = p.width - panelW;
            int panelY = 0;
            int panelH = p.height;

            // desenhar painel
            statsPanel.draw(p, panelX, panelY, panelW, panelH);
        }

        drawMenuButton(p);

    }

    @Override public void keyPressed(PApplet p) {
        if (gameState == GameState.MENU && (p.keyCode == PApplet.ENTER)) {

            // ir buscar as configuração escolhidas
            numCivils = menu.getNumCivils();
            numPolices = menu.getNumPolices();
            chanceIllegalPerSecond = menu.getChanceIllegalPerSecond();

            // forçar rebuild com novos valores
            built = false;
            gameState = GameState.RUNNING;
        }

        if (p.key == 's' || p.key == 'S') {
            showStats = !showStats;
            System.out.println("Stats: " + (showStats ? "ON" : "OFF"));
        }
    }

    @Override public void keyReleased(PApplet p) {}
    @Override
    public void mousePressed(PApplet p) {
        if (gameState == GameState.MENU) {
            menu.mousePressed(p);
            return;
        }

        // voltar ao menu
        if (gameState == GameState.RUNNING &&
                p.mouseX >= btnMenuX && p.mouseX <= btnMenuX + btnMenuW &&
                p.mouseY >= btnMenuY && p.mouseY <= btnMenuY + btnMenuH) {

            gameState = GameState.MENU;
            built = false;        // forçar rebuild
            showStats = false;
            return;
        }

        if (!built || plt == null || fullWindow == null) return;

        // Clique direito (afastar)
        if (p.mouseButton == PApplet.RIGHT) {
            window = fullWindow.clone();
            plt = new SubPlot(window, viewport, p.width, p.height);
            return;
        }

        // Clique esquerdo (aproximar no centro escolhido)
        if (p.mouseButton == PApplet.LEFT) {
            double[] w = plt.getWorldCoord(p.mouseX, p.mouseY);
            double cx = w[0];
            double cy = w[1];

            double wSize = (fullWindow[1] - fullWindow[0]) * zoomFactor;
            double hSize = (fullWindow[3] - fullWindow[2]) * zoomFactor;

            window = new double[]{
                    cx - wSize / 2.0, cx + wSize / 2.0,
                    cy - hSize / 2.0, cy + hSize / 2.0
            };

            plt = new SubPlot(window, viewport, p.width, p.height);
        }
    }

    @Override public void mouseReleased(PApplet p) {}
    @Override public void mouseDragged(PApplet p) {}

    public ArrayList<RoadSegment> getRoads() {
        return roads;
    }

    private void fitWindowToNetwork(PApplet p, float margin) {
        float minX=1e9f, maxX=-1e9f, minY=1e9f, maxY=-1e9f;
        for (Node n : net.nodes) {
            minX = Math.min(minX, n.pos.x);
            maxX = Math.max(maxX, n.pos.x);
            minY = Math.min(minY, n.pos.y);
            maxY = Math.max(maxY, n.pos.y);
        }

        float w = (maxX - minX);
        float h = (maxY - minY);

        // margem em percentagem
        minX -= w * margin;
        maxX += w * margin;
        minY -= h * margin;
        maxY += h * margin;

        window = new double[]{minX, maxX, minY, maxY};

        fullWindow = window.clone();

        plt = new SubPlot(window, viewport, p.width, p.height);
    }

    private void drawMenuButton(PApplet p) {
        boolean hover =
                p.mouseX >= btnMenuX && p.mouseX <= btnMenuX + btnMenuW &&
                        p.mouseY >= btnMenuY && p.mouseY <= btnMenuY + btnMenuH;

        p.stroke(hover ? 255 : 180);
        p.strokeWeight(2);
        p.fill(hover ? 245 : 225);
        p.rect(btnMenuX, btnMenuY, btnMenuW, btnMenuH, 8);

        p.fill(0);
        p.textAlign(PApplet.CENTER, PApplet.CENTER);
        p.textSize(14);
        p.text("Menu", btnMenuX + btnMenuW / 2f, btnMenuY + btnMenuH / 2f);
    }


}
