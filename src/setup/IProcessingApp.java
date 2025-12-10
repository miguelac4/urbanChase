    package setup;

    import processing.core.PApplet;

    public interface IProcessingApp {

        void setup(PApplet p);

        void draw(PApplet p, float dt);

        void keyPressed(PApplet p);

        void mousePressed(PApplet p);

        void keyReleased(PApplet p);

        void mouseReleased(PApplet p);

        void mouseDragged(PApplet p);
    }
