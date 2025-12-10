package fractals;

import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

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
    public void render(LSystem lsys, PApplet p, SubPlot plt){
        // A cada render escolhe a cor da proxima iteração
        p.stroke(strokeColor);
        float[] lenPix = plt.getDimInPixel(len,len);
        for(int i = 0; i < lsys.getSequence().length();i++){
            char c = lsys.getSequence().charAt(i);
            if(c == 'F' || c == 'G') {
                p.line(0,0,lenPix[0],0);
                p.translate(lenPix[0],0);
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
}
