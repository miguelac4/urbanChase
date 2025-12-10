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

    protected ParticleSystem(PVector pos, PVector vel, float mass, float radius, int particleColor, float lifetime, PVector particleSpeed) {
        super(pos, vel, mass, radius);
        this.particleColor = particleColor;
        this.lifetime = lifetime;
        this.particleSpeed = particleSpeed;
        this.particles = new ArrayList<Particle>();
    }

    public void move(float dt){
        super.move(dt);
        addParticle();
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
        float vx = (float) (particleSpeed.x * (Math.random() - 0.5));
        float vy = (float) (particleSpeed.y * (Math.random() - 0.5));
        Particle particle = new Particle(pos, new PVector(vx, vy), radius, particleColor, lifetime);
        particles.add(particle);
    }

    public void display(PApplet p, SubPlot plt){

        Iterator<Particle> iterator = particles.iterator();
        while(iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.display(p, plt);
        }


//            for(Particle particle : particles){
//            particle.display(p, plt);
//        }
    }
}
