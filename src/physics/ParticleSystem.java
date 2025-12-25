package physics;

import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleSystem extends Mover{

    private List<Particle> particles;
    private int particleColor;
    private float lifetime;
    private PVector particleSpeed;

    private PVector emitDir = new PVector(1, 0); // default
    private boolean emitting = true;

    public ParticleSystem(PVector pos, PVector vel, float mass, float radius, int particleColor, float lifetime, PVector particleSpeed) {
        super(pos, vel, mass, radius);
        this.particleColor = particleColor;
        this.lifetime = lifetime;
        this.particleSpeed = particleSpeed;
        this.particles = new ArrayList<>();
    }

    public void setEmitting(boolean emitting) {
        this.emitting = emitting;
    }

    public void setEmitDir(PVector dir) {
        if (dir != null && dir.mag() > 1e-6f) this.emitDir = dir.copy().normalize();
    }

    @Override
    public void move(float dt){
        super.move(dt);

        if (emitting) addParticle();

        Iterator<Particle> iterator = particles.iterator();
        while(iterator.hasNext()){
            Particle p = iterator.next();
            p.move(dt);

            if(p.isDead()){
                iterator.remove();
            }
        }

//        for(int i = particles.size()-1; i >= 0; i--){
//            Particle p = particles.get(i);
//            p.move(dt);
//            if(p.isDead()){
//                particles.remove(i);
//            }
//        }
    }

    private void addParticle(){
        // vetor de direção para as particulas
        PVector dir = emitDir.copy();

        // vetor perpendicular para dar spray
        PVector perp = new PVector(-dir.y, dir.x);

        // intensidade do envio de particulas
        float jet = (float)(0.6 + 0.4 * Math.random()) * particleSpeed.x;

        // espalhamento lateral
        float spread = (float)((Math.random() - 0.5) * particleSpeed.y);

        PVector v = PVector.add(PVector.mult(dir, jet), PVector.mult(perp, spread));

        Particle particle = new Particle(pos.copy(), v, radius, particleColor, lifetime);
        particles.add(particle);
    }

    public void display(PApplet p, SubPlot plt){

        for (Particle particle : particles) particle.display(p, plt);


//            for(Particle particle : particles){
//            particle.display(p, plt);
//        }
    }
}
