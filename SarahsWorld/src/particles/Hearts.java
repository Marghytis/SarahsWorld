package particles;

import particles.Particle.ParticleType;
import render.TexFile;
import util.math.Vec;

public class Hearts implements ParticleEffect{

	
	public static final ParticleType HEART = new ParticleType(new TexFile("res/particles/Heart.png"));
	
	public ParticleEmitter hearts = new ParticleEmitter(14, 1, HEART, 2){

		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*200f, (random.nextFloat() - 0.5f)*200f);//-0.8f
			p.col.set(0.8f, 0, 0, 1);
			p.rad = 0.5f + random.nextFloat();
		}

		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.99f;
		}

		public void colorInterpolator(Particle p, float delta) {
			p.col.a *= 0.96f;
		}

		public void rotationInterpolator(Particle p, float delta) {}

		public void radiusInterpolator(Particle p, float delta) {
			p.rad *= 0.999f;
		}
	};

	public Vec pos;
	public float count = 1;

	public Hearts(Vec pos){
		this.pos = pos.copy();
		for(int i = 0; i < hearts.particles.length; i++){
			hearts.emittParticle(0);
		}
		hearts.emitting = false;
	}
	
	@Override
	public void tick(float dTime){
		hearts.tick(dTime);
		count -= dTime;
	}
	
	@Override
	public void render(){
		hearts.render();
	}
	
	@Override
	public void finalize(){
		hearts.finalize();
	}

	@Override
	public boolean living() {
		return count > 0;
	}
}
