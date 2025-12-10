package apps.tp3;

import fractals.MaldelbrotSet;
import processing.core.PApplet;
import setup.IProcessingApp;
import tools.SubPlot;

public class MandelbrotZoomApp implements IProcessingApp {
    private double[] window = {-2,2,-2,2};
    private float[] viewport = {0,0,1,1};
    private SubPlot plt;
    private MaldelbrotSet ms;
    private int mx0, mx1,my0,my1;

    // Fomos manipulando estes valores para encontrar padrões interessantes
    private double centerX = -0.761574;
    private double centerY = -0.0847596;

    private double zoom = 2.0;
    private double zoomFactor = 0.95; // velocidade do zoom


    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport,p.width,p.height);
        ms = new MaldelbrotSet(300,plt);
        ms.display(p,plt);
    }

    @Override
    public void draw(PApplet parent, float dt) {

        // atualizar zoom
        zoom *= zoomFactor;

        // calcular nova janela centrada no ponto escolhido
        double xmin = centerX - zoom;
        double xmax = centerX + zoom;
        double ymin = centerY - zoom;
        double ymax = centerY + zoom;

        double[] newWindow = {xmin, xmax, ymin, ymax};

        // atualizar subplot
        plt = new SubPlot(newWindow, viewport, parent.width, parent.height);


        ms.display(parent,plt);
        displayNewWindow(parent);
    }

    @Override
    public void keyPressed(PApplet parent) {

    }

    @Override
    public void mousePressed(PApplet p) {
        mx0 = mx1 = p.mouseX;
        my0 = my1 = p.mouseY;
    }

    @Override
    public void keyReleased(PApplet p) {

    }

    @Override
    public void mouseReleased(PApplet p) {
        double[] xy0 = plt.getWorldCoord(mx0,my0);
        double[] xy1 = plt.getWorldCoord(p.mouseX,p.mouseY);
        double xmin = Math.min(xy0[0],xy1[0]);
        double xmax = Math.max(xy0[0],xy1[0]);
        double ymin = Math.min(xy0[1],xy1[1]);
        double ymax = Math.max(xy0[1],xy1[1]);
        double[] window = {xmin,xmax,ymin,ymax};
        plt = new SubPlot(window,viewport,p.width,p.height);
        mx0 = mx1 = my0 = my1 = 0;
    }

    private void displayNewWindow(PApplet p) {
        p.pushStyle();
        p.noFill();
        p.strokeWeight(3);
        p.stroke(0,50,255);
        p.rect(mx0,my0,mx1-mx0,my1-my0);
        p.popStyle();

    }
    @Override
    public void mouseDragged(PApplet p) {
        mx1 = p.mouseX;
        my1 = p.mouseY;
    }
}
