package extra.effects.particleEffects;

import basis.effects.particleEffects.Particle;
import basis.effects.particleEffects.ParticleEffect;
import basis.effects.particleEffects.ParticleEmitter;
import basis.effects.particleEffects.Particle.ParticleType;
import extra.Res;
import util.math.Vec;

public class DeathDust implements ParticleEffect {
	
public static final ParticleType SMOKE = new ParticleType(Res.getTex("smokeParticle"));
	
	public ParticleEmitter smoke = new ParticleEmitter(40, 1, SMOKE, 3){

		int spawnRadius = 10;
		@Override
		public void makeParticle(Particle p) {
			float angle = random.nextFloat()*(float)(Math.PI*2);
			float xMod = (float)Math.cos(angle), yMod = (float)Math.sin(angle);
			p.pos.set(pos.x+(xMod*spawnRadius), pos.y+(yMod*spawnRadius));
			
			p.vel.set(xMod*(random.nextFloat()*100f), yMod*(random.nextFloat()*100f));//-0.8f
			p.col.set(0.7f, 0.7f, 0.5f, 0.1f);
			p.rot = random.nextBoolean() ? 0.1f : -0.1f;
			p.rad = 2;
		}
		
		public float radiusAim = 1;
		
		public void radiusInterpolator(Particle p, float delta){
			p.rad = radiusAim*p.lived + 2;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x -= p.vel.x*0.02f*delta;
			p.vel.y -= p.vel.y*0.02f*delta;
			p.vel.shift(ParticleEffect.wind, 5*delta);
		}

		public void colorInterpolator(Particle p, float delta) {
			p.col.a -= 0.08f*delta;
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {
			if(p.rot > 0){
				p.rot = (1f/50)*p.lived;
			} else {
				p.rot = -(1f/50)*p.lived;
			}
		}
	};

	public Vec pos;
	public float count = 1;

	public DeathDust(Vec pos){
		this.pos = pos.copy();
		for(int i = 0; i < smoke.particles.length; i++){
			smoke.emittParticle(0);
		}
		smoke.emitting = false;
	}
	
	@Override
	public void update(double dTime){
		smoke.tick((float)dTime);
		count -= dTime;
	}
	
	@Override
	public void render(float scaleX, float scaleY){
		smoke.render(scaleX, scaleY);
	}
	
	@Override
	public void terminate(){
		smoke.terminate();
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
