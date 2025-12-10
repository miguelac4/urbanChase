package apps;

import fractals.LSystem;
import fractals.Rule;
import fractals.Turtle;
import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

public class CityFractalApp implements IProcessingApp {

    // Plot / coordenadas
    private SubPlot plt;
    private double[] window = {-1, 1, -1, 1};      // mundo 2D
    private float[] viewport = {0f, 0f, 1f, 1f};   // ocupa o ecrã todo

    // L-System e Turtle
    private LSystem lsys;
    private Turtle turtle;
    private PVector origin;

    private float baseLength = 0.3f;   // comprimento "F" no mundo
    private float angle = (float)(Math.PI / 2); // 90° -> cidade ortogonal
    private int nGenerations = 4;      // quantas iterações do L-system

    @Override
    public void setup(PApplet p) {
        // SubPlot para converter mundo [-1,1]x[-1,1] para pixels
        plt = new SubPlot(window, viewport, p.width, p.height);

        // Definir L-system da cidade
        String axiom = "F";
        Rule[] rules = new Rule[] {
                new Rule('F', "F[+F]F[-F]F")
        };
        lsys = new LSystem(axiom, rules);

        // Avançar algumas gerações logo no início
        for (int i = 0; i < nGenerations; i++) {
            lsys.nextGeneration();
        }

        // Criar a tartaruga
        turtle = new Turtle(baseLength, angle);

        // Posição inicial no "mundo" (0,0) → centro da janela
        origin = new PVector(0f, 0f);
    }

    @Override
    public void draw(PApplet p, float dt) {
        p.background(220);

        // desenhar "estradas":
        p.stroke(30);
        p.strokeWeight(2);
        p.noFill();

        p.pushMatrix();
        // Pose inicial da tartaruga (posição + orientação)
        // orientação 0 => para a direita; usa PI/2 se quiseres começar vertical
        turtle.setPose(origin, 0f, p, plt);

        // opcional: podes mudar a cor aqui
        // turtle.setColor(p.color(0, 0, 0));

        // desenhar o L-system como estradas
        turtle.render(lsys, p, plt);
        p.popMatrix();
    }

    // Por agora não precisas destes, mas a interface obriga:
    @Override
    public void keyPressed(PApplet p) {
        // Ex: tecla 'n' para ir para próxima geração
        if (p.key == 'n') {
            lsys.nextGeneration();
            // para não explodir o tamanho, encurta o segmento em cada geração
            turtle.scaling(0.5f);
        }
    }

    @Override
    public void keyReleased(PApplet p) {}

    @Override
    public void mousePressed(PApplet p) {}

    @Override
    public void mouseReleased(PApplet p) {}

    @Override
    public void mouseDragged(PApplet p) {}
}
