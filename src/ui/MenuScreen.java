package ui;

import processing.core.PApplet;

import static processing.core.PApplet.nf;

public class MenuScreen {

    private float t = 0f;

    private boolean configOpen = false;

    // defaults
    private int numCivils = 20;
    private int numPolices = 5;
    private float chanceIllegalPerSecond = 0.03f;

    // Butoes
    private float btnCfgX, btnCfgY, btnCfgW, btnCfgH;
    private float btnCivM_X, btnCivM_Y, btnCivM_W, btnCivM_H;
    private float btnCivP_X, btnCivP_Y, btnCivP_W, btnCivP_H;
    private float btnPolM_X, btnPolM_Y, btnPolM_W, btnPolM_H;
    private float btnPolP_X, btnPolP_Y, btnPolP_W, btnPolP_H;
    private float btnChM_X, btnChM_Y, btnChM_W, btnChM_H;
    private float btnChP_X, btnChP_Y, btnChP_W, btnChP_H;

    public void update(float dt) { t += dt; }

    public void draw(PApplet p) {
        float w = p.width, h = p.height;

        // fundo
        float blue = 0.5f + 0.5f * PApplet.sin(t * 3.2f);
        float red  = 0.5f + 0.5f * PApplet.sin(t * 3.2f + PApplet.PI);

        p.background(10);
        p.noStroke();
        p.fill(0, 120, 255, 80 + 140 * blue); p.rect(0, 0, w * 0.5f, h);
        p.fill(255, 30, 30, 80 + 140 * red);  p.rect(w * 0.5f, 0, w, h);

        // cartão
        float cw = p.min(780, w * 0.78f), ch = p.min(360, h * 0.55f);
        float cx = (w - cw) / 2f, cy = (h - ch) / 2f - 120;

        p.fill(0, 0, 0, 80); p.rect(cx + 8, cy + 10, cw, ch, 18);
        p.fill(255, 255, 255, 235); p.rect(cx, cy, cw, ch, 18);

        // texto
        p.fill(10);
        p.textAlign(PApplet.CENTER, PApplet.TOP);
        p.textSize(54);
        p.text("Urban Chase", w / 2f, cy + 26);

        p.fill(30);
        p.textAlign(PApplet.LEFT, PApplet.TOP);
        p.textSize(18);

        p.text(
                "A cidade enfrenta uma onda de carros de corrida ilegais.\n" +
                        "A polícia reforça patrulhas inteligentes com técnicas avançadas de perseguição.\n" +
                        "Cada civil tenta sobreviver o máximo possível evitando ser apanhado.\n" +
                        "Com o tempo, só os mais bem adaptados permanecem a cometer infrações.",
                cx + 36, cy + 110, cw - 72, ch - 140
        );

        p.fill(0, 0, 0, 120);
        p.textAlign(PApplet.CENTER, PApplet.BOTTOM);
        p.textSize(14);
        p.text("Pressiona ENTER para iniciar", w / 2f, cy + ch - 18);

        // botão "Escolher configuração"
        btnCfgW = 260;
        btnCfgH = 44;
        btnCfgX = w / 2f - btnCfgW / 2f;
        btnCfgY = cy + ch + 30;

        drawButton(p, btnCfgX, btnCfgY, btnCfgW, btnCfgH,
                configOpen ? "Fechar configuração" : "Escolher configuração");

        if (configOpen) {
            drawConfigPanel(p, w, h, cx, cy, cw, ch);
        }
    }

    public void mousePressed(PApplet p) {
        // abrir / fechar configuração
        if (hit(p, btnCfgX, btnCfgY, btnCfgW, btnCfgH)) {
            configOpen = !configOpen;
        }

        if (!configOpen) return;

        // Civis
        if (hit(p, btnCivM_X, btnCivM_Y, btnCivM_W, btnCivM_H)) {
            numCivils = Math.max(0, numCivils - 1);
        }
        if (hit(p, btnCivP_X, btnCivP_Y, btnCivP_W, btnCivP_H)) {
            numCivils++;
        }

        // Polícias
        if (hit(p, btnPolM_X, btnPolM_Y, btnPolM_W, btnPolM_H)) {
            numPolices = Math.max(0, numPolices - 1);
        }
        if (hit(p, btnPolP_X, btnPolP_Y, btnPolP_W, btnPolP_H)) {
            numPolices++;
        }

        // Chance ilegal
        if (hit(p, btnChM_X, btnChM_Y, btnChM_W, btnChM_H)) {
            chanceIllegalPerSecond = Math.max(0f, chanceIllegalPerSecond - 0.01f);
        }
        if (hit(p, btnChP_X, btnChP_Y, btnChP_W, btnChP_H)) {
            chanceIllegalPerSecond += 0.01f;
        }

    }

    private boolean hit(PApplet p, float x, float y, float w, float h) {
        return p.mouseX >= x && p.mouseX <= x + w &&
                p.mouseY >= y && p.mouseY <= y + h;
    }


    private void drawButton(PApplet p, float x, float y, float w, float h, String label) {
        boolean hover = p.mouseX >= x && p.mouseX <= x + w &&
                p.mouseY >= y && p.mouseY <= y + h;

        p.stroke(hover ? 255 : 180);
        p.strokeWeight(2);
        p.fill(hover ? 255 : 230);
        p.rect(x, y, w, h, 10);

        p.fill(0);
        p.textAlign(PApplet.CENTER, PApplet.CENTER);
        p.textSize(16);
        p.text(label, x + w / 2f, y + h / 2f);
    }

    private void drawConfigPanel(PApplet p, float w, float h,
                                 float cx, float cy, float cw, float ch) {

        float panelW = 420;
        float panelH = 200;
        float px = w / 2f - panelW / 2f;
        float py = cy + ch + 100;

        // fundo do painel
        p.noStroke();
        p.fill(255, 255, 255, 245);
        p.rect(px, py, panelW, panelH, 14);

        p.fill(0);
        p.textAlign(PApplet.LEFT, PApplet.TOP);
        p.textSize(18);
        p.text("Configuração", px + 16, py + 14);

        p.textSize(16);

        // civis
        float yCiv = py + 60;

        p.text("Civis:", px + 16, yCiv);

        // botão -
        btnCivM_X = px + 120;
        btnCivM_Y = yCiv - 14;
        btnCivM_W = 28;
        btnCivM_H = 24;
        drawButton(p, btnCivM_X, btnCivM_Y, btnCivM_W, btnCivM_H, "-");

        // valor
        p.textAlign(PApplet.CENTER, PApplet.TOP);
        p.text(numCivils, px + 170, yCiv);

        // botão +
        btnCivP_X = px + 200;
        btnCivP_Y = yCiv - 14;
        btnCivP_W = 28;
        btnCivP_H = 24;
        drawButton(p, btnCivP_X, btnCivP_Y, btnCivP_W, btnCivP_H, "+");

        float yPol = py + 95;
        p.textAlign(PApplet.LEFT, PApplet.TOP);
        p.text("Polícias:", px + 16, yPol);

        btnPolM_X = px + 120;
        btnPolM_Y = yPol - 14;
        btnPolM_W = 28;
        btnPolM_H = 24;
        drawButton(p, btnPolM_X, btnPolM_Y, btnPolM_W, btnPolM_H, "-");

        p.textAlign(PApplet.CENTER, PApplet.TOP);
        p.text(numPolices, px + 170, yPol);

        btnPolP_X = px + 200;
        btnPolP_Y = yPol - 14;
        btnPolP_W = 28;
        btnPolP_H = 24;
        drawButton(p, btnPolP_X, btnPolP_Y, btnPolP_W, btnPolP_H, "+");

        float yCh = py + 130;
        p.textAlign(PApplet.LEFT, PApplet.TOP);
        p.text("Chance ilegal/s:", px + 16, yCh);

        btnChM_X = px + 160;
        btnChM_Y = yCh - 14;
        btnChM_W = 28;
        btnChM_H = 24;
        drawButton(p, btnChM_X, btnChM_Y, btnChM_W, btnChM_H, "-");

        p.textAlign(PApplet.CENTER, PApplet.TOP);
        p.text(nf(chanceIllegalPerSecond, 1, 2), px + 215, yCh);

        btnChP_X = px + 245;
        btnChP_Y = yCh - 14;
        btnChP_W = 28;
        btnChP_H = 24;
        drawButton(p, btnChP_X, btnChP_Y, btnChP_W, btnChP_H, "+");

    }


    public int getNumCivils() {
        return numCivils;
    }

    public int getNumPolices() {
        return numPolices;
    }

    public float getChanceIllegalPerSecond() {
        return chanceIllegalPerSecond;
    }

}
