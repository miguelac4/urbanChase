package setup;

import apps.CarWanderApp;
import apps.CityFractalApp;
import apps.RoadNodesApp;
import processing.core.PApplet;

public class ProcessingSetup extends PApplet {
    public static IProcessingApp app;
    private int lastUpdateTime;

    public static void main(String[] args) {
        /*

        Definir aplicação para correr:
        - CityFractalApp()
        - RoadNodesApp()
        - CarWanderApp()

        */
        app = new CarWanderApp();
        PApplet.main(ProcessingSetup.class.getName());
    }

    @Override
    public void settings() {
        size(700, 700);
    }

    @Override
    public void setup() {
        app.setup(this);
        lastUpdateTime = millis();
    }

    @Override
    public void draw() {
        int now = millis();
        float dt = (now - lastUpdateTime)/1000f;
        app.draw(this, dt);
        lastUpdateTime = now;
    }

    @Override
    public void keyPressed() {
        app.keyPressed(this);
    }

    @Override
    public void keyReleased() {
        app.keyReleased(this);
    }

    @Override
    public void mousePressed() {
        app.mousePressed(this);
    }

    @Override
    public void mouseDragged() {app.mouseDragged(this);}

    @Override
    public void mouseReleased() {app.mouseReleased(this);}
}

