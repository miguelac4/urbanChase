package ui;

import processing.core.PApplet;

public class MenuScreen {

    private float t = 0f;

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
        float cx = (w - cw) / 2f, cy = (h - ch) / 2f;

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
    }
}
