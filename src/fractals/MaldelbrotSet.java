package fractals;

import processing.core.PApplet;
import tools.Complex;
import tools.SubPlot;

public class MaldelbrotSet {
    private int niter;
    private int x0, y0;
    private int dimx, dimy;

    public MaldelbrotSet(int niter, SubPlot plt){
        this.niter = niter;
        float[] bb = plt.getBoundingBox();
        x0 = (int)bb[0];
        y0 = (int)bb[1];
        dimx = (int)bb[2];
        dimy = (int)bb[3];
    }

    public void display(PApplet p, SubPlot plt){
        float t = p.millis();
        p.loadPixels();
        for(int xx=x0; xx<x0+dimx;xx++) {
            for(int yy=y0; yy<y0+dimy; yy++) {
                double[] cc = plt.getWorldCoord(xx,yy);
                Complex c = new Complex(cc);
                Complex x = new Complex();
                int i;
                for(i=0;i<niter;i++) {
                    x.mult(x).add(c);
                    if(x.norm() > 2) {
                        break;
                    }

                }
//                int color = (i == niter) ? p.color(0) : p.color((i%16)*16);
//                p.pixels[yy*p.width+xx] = color;

                if(i == niter) {
                    // Pontos dentro do Mandelbrot = preto
                    p.pixels[yy * p.width + xx] = p.color(0);
                } else {
                    float tNorm = (float)i / niter;   // valor normalizado entre 0 e 1

                    // Fases diferentes para cada cor
                    float phaseR = 0.0f;
                    float phaseG = 0.33f;
                    float phaseB = 0.66f;

                    // fórmula com cosseno para gradiente suave
                    int r = (int)(128 + 127 * Math.cos(6.28318 * (tNorm + phaseR)));
                    int g = (int)(128 + 127 * Math.cos(6.28318 * (tNorm + phaseG)));
                    int b = (int)(128 + 127 * Math.cos(6.28318 * (tNorm + phaseB)));

                    p.pixels[yy * p.width + xx] = p.color(r, g, b);
                }
            }
        }
        p.updatePixels();
        System.out.println(p.millis()-t);

    }

}
