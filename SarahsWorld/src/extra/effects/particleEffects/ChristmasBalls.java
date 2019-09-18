package extra.effects.particleEffects;

import basis.effects.particleEffects.Particle;
import basis.effects.particleEffects.ParticleEffect;
import basis.effects.particleEffects.ParticleEmitter;
import basis.effects.particleEffects.Particle.ParticleType;
import extra.Res;
import util.math.Vec;

public class ChristmasBalls implements ParticleEffect {

	
	public static final ParticleType CHRISTMAS_BALL = new ParticleType(Res.getTex("christmasBallPartile"));
	
	public ParticleEmitter christmasBall = new ParticleEmitter(30, 1, CHRISTMAS_BALL, 2){

		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*200f, (random.nextFloat() - 0.5f)*200f);//-0.8f
			p.col.set(0.63f, 0.10f, 0.10f, 1f);
			p.rad = 0.5f;
		}

		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.99f;
			p.vel.y -= 1f;
		}

		public void colorInterpolator(Particle p, float delta) {
		}

		public void rotationInterpolator(Particle p, float delta) {}

		public void radiusInterpolator(Particle p, float delta) {
		}
	};

	public Vec pos;
	public float count = 1;

	public ChristmasBalls(Vec pos){
		this.pos = pos.copy();
		for(int i = 0; i < christmasBall.particles.length; i++){
			christmasBall.emittParticle(0);
		}
		christmasBall.emitting = false;
	}
	
	@Override
	public void update(double dTime){
		christmasBall.tick((float)dTime);
		count -= dTime;
	}
	
	@Override
	public void render(float scaleX, float scaleY){
		christmasBall.render(scaleX, scaleY);
	}
	
	@Override
	public void terminate(){
		christmasBall.terminate();
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
