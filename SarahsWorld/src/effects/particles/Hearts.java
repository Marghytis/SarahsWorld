package effects.particles;

import effects.particles.Particle.ParticleType;
import main.Res;
import util.math.Vec;

public class Hearts implements ParticleEffect{

	
	public static final ParticleType HEART = new ParticleType(Res.getTex("heartParticle"));
	
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
	public void update(double dTime){
		hearts.tick((float)dTime);
		count -= dTime;
	}
	
	@Override
	public void render(float scaleX, float scaleY){
		hearts.render(scaleX, scaleY);
	}
	
	public void terminate(){
		hearts.terminate();
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
		return false;
	}

	@Override
	public boolean charTyped(char ch) {
		return false;
	}
}
