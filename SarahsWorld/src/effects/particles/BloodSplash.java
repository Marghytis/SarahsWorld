package effects.particles;

import render.Texture;
import util.math.Vec;
import effects.particles.Particle.ParticleType;

public class BloodSplash implements ParticleEffect {

	
	public static final ParticleType BLOOD_DROP = new ParticleType(new Texture("res/particles/Blood_drop.png", -0.5, -0.5));
	
	public ParticleEmitter blood = new ParticleEmitter(30, 1, BLOOD_DROP, 1){

		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*200f, (random.nextFloat() - 0.5f)*200f);//-0.8f
			p.col.set(0.6f, 0, 0f, 1f);
			p.rad = 0.5f;
		}

		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.99f;
			p.vel.y -= 5f;
		}

		public void colorInterpolator(Particle p, float delta) {
			p.col.a *= 0.97f;
		}

		public void rotationInterpolator(Particle p, float delta) {}

		public void radiusInterpolator(Particle p, float delta) {
			p.rad *= 0.99f;
		}
	};

	public Vec pos;
	public float count = 1;

	public BloodSplash(Vec pos){
		this.pos = pos.copy();
		for(int i = 0; i < blood.particles.length; i++){
			blood.emittParticle(0);
		}
		blood.emitting = false;
	}
	
	@Override
	public void update(double dTime){
		blood.tick((float)dTime);
		count -= dTime;
	}
	
	@Override
	public void render(){
		blood.render();
	}
	
	@Override
	public void finalize(){
		blood.finalize();
	}

	@Override
	public boolean living() {
		return count > 0;
	}

	@Override
	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		return false;
	}

	@Override
	public boolean keyPressed(int key) {
		return false;
	}

	@Override
	public boolean keyReleased(int key) {
		// TODO Auto-generated method stub
		return false;
	}
}
