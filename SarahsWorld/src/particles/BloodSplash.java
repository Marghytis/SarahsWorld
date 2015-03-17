package particles;

import particles.Particle.ParticleType;
import render.TexFile;
import util.math.Vec;

public class BloodSplash implements ParticleEffect{

	
	public static final ParticleType BLOOD_DROP = new ParticleType(new TexFile("res/particles/Blood_drop.png"));
	
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
	public void tick(float dTime){
		blood.tick(dTime);
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
}
