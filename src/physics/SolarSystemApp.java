package physics;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SolarSystemApp implements IProcessingApp {
    private List<ParticleSystem> pss;

    // Definir todas as Massas
    private float sunMass = 1.989e30f;
    private float earthMass = 5.97e24f;
    private float mercuryMass = 0.33e24f;
    private float venusMass = 4.87e24f;
    private float marsMass = 0.642e24f;
    private float jupiterMass = 1898e24f; //1898
    private float saturnMass = 568e24f; //568
    private float uranusMass = 86.8e24f; //86
    private float neptuneMass = 102e24f; //102

    // Definir todas as Distancias ao Sol
    private float distEarthSun = 149.6e9f;
    private float distMercurySun = 57.9e9f;
    private float distVenusSun = 108.2e9f;
    private float distMarsSun = 228e9f;
    private float distJupiterSun = 778.5e9f;
    private float distSaturnSun = 1432.0e9f;
    private float distUranusSun = 2867.0e9f;
    private float distNeptuneSun = 4515.0e9f;

    // Definir todas as Velocidades
    private float earthSpeed = 2.9e4f;
    private float mercurySpeed = 4.7e4f;
    private float venusSpeed = 3.5e4f;
    private float marsSpeed = 2.41e4f;
    private float jupiterSpeed = 1.31e4f;
    private float saturnSpeed = 0.97e4f;
    private float uranusSpeed = 0.68e4f;
    private float neptuneSpeed = 0.54e4f;


    private float[] viewport = {0, 0, 1, 1};
    /*
    Para alterar o campo de visualização:
        - 0.4 para aproximar e ver apenas até marte
        - 6 para visualizar todos os planetas do sistema solar
     */

    private double changeWindow = 6;
    private double[] window = {-changeWindow * distJupiterSun, changeWindow * distJupiterSun,
            -changeWindow * distJupiterSun, changeWindow * distJupiterSun};

    private SubPlot plt;
    private CelestialBody sun;
    private CelestialBody earth;
    private CelestialBody mercury;
    private CelestialBody venus;
    private CelestialBody mars;
    private CelestialBody jupiter;
    private CelestialBody saturn;
    private CelestialBody uranus;
    private CelestialBody neptune;

    /*
    Velocidade definida: 3 anos a cada segundo

        Nota: Esta velocidade não pode ser astronomica, caso seja aumentada do valor definido (3)
        irá causar problemas com o integrador do metodo Euler.
     */
    private float speedUp = 60 * 60 * 24 * 30 * 3;
    @Override
    public void setup(PApplet p) {
        plt = new SubPlot(window, viewport, p.width, p.height);
        sun = new CelestialBody(new PVector(), new PVector(), sunMass, distEarthSun/10, p.color(255, 128, 0));
        earth = new CelestialBody(new PVector(0, distEarthSun), new PVector(earthSpeed, 0), earthMass, distEarthSun/20, p.color(0, 180, 120));
        mercury = new CelestialBody(new PVector(0, distMercurySun), new PVector(mercurySpeed, 0), mercuryMass, distMercurySun/10, p.color(183, 184, 185));
        venus = new CelestialBody(new PVector(0, distVenusSun), new PVector(venusSpeed, 0), venusMass, distVenusSun/10, p.color(248,226,176));
        mars = new CelestialBody(new PVector(0, distMarsSun), new PVector(marsSpeed, 0), marsMass, distMarsSun/10, p.color(69,24,4));
        jupiter = new CelestialBody(new PVector(0, distJupiterSun), new PVector(jupiterSpeed, 0), jupiterMass, distJupiterSun/10, p.color(227,220,203));
        saturn = new CelestialBody(new PVector(0, distSaturnSun), new PVector(saturnSpeed, 0), saturnMass, distSaturnSun/10, p.color(226,191,125));
        uranus = new CelestialBody(new PVector(0, distUranusSun), new PVector(uranusSpeed, 0), uranusMass, distUranusSun/10, p.color(225,238,238));
        neptune = new CelestialBody(new PVector(0, distNeptuneSun), new PVector(neptuneSpeed, 0), neptuneMass, distNeptuneSun/10, p.color(91,93,223));
        pss = new ArrayList<ParticleSystem>();
    }

    @Override
    public void draw(PApplet p, float dt) {
        float[] pp = plt.getBoundingBox();

        p.fill(0, 16);
        p.rect(pp[0], pp[1], pp[2], pp[3]);

        sun.display(p, plt);

        // Aplicar fisicas dos planetas ao sol (CelestialBody)
        PVector fToEarth = sun.attraction(earth);
        PVector fToMercury = sun.attraction(mercury);
        PVector fToVenus = sun.attraction(venus);
        PVector fToMars = sun.attraction(mars);
        PVector fToJupiter = sun.attraction(jupiter);
        PVector fToSaturn = sun.attraction(saturn);
        PVector fToUranus = sun.attraction(uranus);
        PVector fToNeptune = sun.attraction(neptune);

        // Aplicar movimentação (Mover)
        earth.applyForce(fToEarth);
        mercury.applyForce(fToMercury);
        venus.applyForce(fToVenus);
        mars.applyForce(fToMars);
        jupiter.applyForce(fToJupiter);
        saturn.applyForce(fToSaturn);
        uranus.applyForce(fToUranus);
        neptune.applyForce(fToNeptune);

        earth.move(dt*speedUp);
        mercury.move(dt*speedUp);
        venus.move(dt*speedUp);
        mars.move(dt*speedUp);
        jupiter.move(dt*speedUp);
        saturn.move(dt*speedUp);
        uranus.move(dt*speedUp);
        neptune.move(dt*speedUp);

        earth.display(p, plt);
        mercury.display(p, plt);
        venus.display(p, plt);
        mars.display(p, plt);
        jupiter.display(p, plt);
        saturn.display(p, plt);
        uranus.display(p, plt);
        neptune.display(p, plt);

        // Correr a lista de particulas e aplicar força e movimentacao
        Iterator<ParticleSystem> iterator = pss.iterator();
        while(iterator.hasNext()) {
            ParticleSystem ps = iterator.next();
            ps.applyForce(new PVector(distJupiterSun, -distVenusSun));
            ps.move(dt);
            ps.display(p, plt);
        }

//        for(ParticleSystem ps : pss){
//            ps.applyForce(new PVector(distJupiterSun, -distVenusSun));
//        }

//        for(ParticleSystem ps : pss){
//            ps.move(dt);
//            ps.display(p, plt);
//        }
    }

    @Override
    public void mousePressed(PApplet p) {
        double[] ww = plt.getWorldCoord(p.mouseX, p.mouseY);

        // Converter as coordenadas do rato do ecra para o mundo fisico
        int cor = p.color(p.random(255), p.random(130), p.random(40, 60));
        float vx = p.random(distEarthSun,distEarthSun);
        float vy = p.random(distEarthSun,distEarthSun);
        float lifespan = p.random(0, 1);

        ParticleSystem ps = new ParticleSystem(new PVector((float)(ww[0]), (float)(ww[1])), new PVector(), 1f, distEarthSun/40, cor, lifespan, new PVector(vx, vy));

        pss.add(ps);
    }

    @Override
    public void keyReleased(PApplet p) {

    }

    @Override
    public void keyPressed(PApplet p) {

    }
}
