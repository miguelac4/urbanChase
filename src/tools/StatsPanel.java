package tools;

import processing.core.PApplet;

public class StatsPanel {
    private final int maxPoints;
    private final float[] civLegal;
    private final float[] civIlegal;
    private final float[] polNormal;
    private final float[] pursuits;
    private int size = 0;

    public StatsPanel(int maxPoints) {
        this.maxPoints = maxPoints;
        civLegal = new float[maxPoints];
        civIlegal = new float[maxPoints];
        polNormal = new float[maxPoints];
        pursuits = new float[maxPoints];
    }

    public void push(int civL, int civI, int polN, int purs) {
        pushOne(civLegal, civL);
        pushOne(civIlegal, civI);
        pushOne(polNormal, polN);
        pushOne(pursuits, purs);

        if (size < maxPoints) size++;
    }

    private void pushOne(float[] arr, float v) {
        // shift para a esquerda (simples e suficiente p/ 300 pontos)
        for (int i = 0; i < maxPoints - 1; i++) arr[i] = arr[i + 1];
        arr[maxPoints - 1] = v;
    }

    public void draw(PApplet p, int x, int y, int w, int h) {
        // fundo do painel
        p.pushStyle();
        p.noStroke();
        p.fill(230);
        p.rect(x, y, w, h);
        p.popStyle();

        int pad = 10;
        int gap = 10;

        int plotH = (h - pad * 2 - gap * 3) / 4;
        int plotW = w - pad * 2;

        int px = x + pad;
        int py = y + pad;

        drawPlot(p, px, py + (plotH + gap) * 0, plotW, plotH, civLegal, "Civis LEGAL");
        drawPlot(p, px, py + (plotH + gap) * 1, plotW, plotH, civIlegal, "Civis ILEGAL");
        drawPlot(p, px, py + (plotH + gap) * 2, plotW, plotH, polNormal, "Policias NORMAL");
        drawPlot(p, px, py + (plotH + gap) * 3, plotW, plotH, pursuits, "Perseguicoes");
    }

    private void drawPlot(PApplet p, int x, int y, int w, int h, float[] series, String title) {
        p.pushStyle();
        p.fill(255);
        p.stroke(120);
        p.rect(x, y, w, h);

        p.noStroke();
        p.fill(0);
        p.textSize(12);
        p.text(title, x + 8, y + 16);

        if (size < 2) {
            p.popStyle();
            return;
        }

        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;

        int start = maxPoints - size;
        for (int i = start; i < maxPoints; i++) {
            float v = series[i];
            if (v < min) min = v;
            if (v > max) max = v;
        }
        // para nao possibilitar dividir por 0
        if (max - min < 1e-6f) {
            max = min + 1f;
        }

        p.noFill();
        p.strokeWeight(2);
        p.stroke(220, 0, 0);

        float left = x + 6;
        float right = x + w - 6;
        float top = y + 22;
        float bottom = y + h - 8;

        p.beginShape();
        for (int i = start; i < maxPoints; i++) {
            float t = (i - start) / (float)(size - 1);
            float xx = PApplet.lerp(left, right, t);

            float v = series[i];
            float yn = (v - min) / (max - min);
            float yy = PApplet.lerp(bottom, top, yn);

            p.vertex(xx, yy);
        }
        p.endShape();

        p.noStroke();
        p.fill(0);
        float last = series[maxPoints - 1];
        p.text(String.format("%.0f", last), x + w - 30, y + 16);

        p.popStyle();
    }
}
