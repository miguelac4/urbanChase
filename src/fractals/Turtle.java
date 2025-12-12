package fractals;

import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;
import processing.core.PMatrix2D;

public class Turtle {
    private float len;
    private float angle;
    private int strokeColor = 0; // Cor inicial a preto
    public Turtle(float len, float angle) {
        this.len = len;
        this.angle = angle;

    }

    // Função para alterar a cor
    public void setColor(int c) {
        strokeColor = c;
    }

    public void setPose(PVector position, float orientation, PApplet p, SubPlot plt){
        float[] pp = plt.getPixelCoord(position.x,position.y);
        p.translate(pp[0],pp[1]);
        p.rotate(-orientation);
    }

    public void scaling(float s) {
        len *= s;
    }

    // agora avisa subclasses de cada segmento desenhado com coordenadas em mundo
    public void render(LSystem lsys, PApplet p, SubPlot plt){
        p.stroke(strokeColor);
        float[] lenPix = plt.getDimInPixel(len, len);

        PMatrix2D m = new PMatrix2D();

        for(int i = 0; i < lsys.getSequence().length(); i++){
            char c = lsys.getSequence().charAt(i);

            if(c == 'F' || c == 'G') {

                // posição atual em pixeis
                p.getMatrix(m);
                float sxPix = m.m02;
                float syPix = m.m12;

                // desenha e avança
                p.line(0, 0, lenPix[0], 0);
                p.translate(lenPix[0], 0);

                // posição final em pixeis
                p.getMatrix(m);
                float exPix = m.m02;
                float eyPix = m.m12;

                // converter pixeis para coordenadas do mundo
                double[] sW = plt.getWorldCoord(sxPix, syPix);
                double[] eW = plt.getWorldCoord(exPix, eyPix);

                onSegment(new PVector((float)sW[0], (float)sW[1]),
                        new PVector((float)eW[0], (float)eW[1]));
            }
            else if(c == 'f') p.translate(len,0);
            else if(c == '+') p.rotate(angle);
            else if(c == '-') p.rotate(-angle);
            else if(c == '[') p.pushMatrix();
            else if(c == ']') p.popMatrix();
        }
    }


    public void setLength(float length) {
        len = length;
    }

    protected float getLen() { return len; }
    protected float getAngle() { return angle; }

    // Subclasses podem guardar segmentos aqui (ex. RoadTurtle.java)
    protected void onSegment(PVector startWorld, PVector endWorld) {
    }
}
