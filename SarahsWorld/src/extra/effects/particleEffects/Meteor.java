package extra.effects.particleEffects;

import org.lwjgl.opengl.GL11;

import basis.effects.particleEffects.Particle;
import basis.effects.particleEffects.Particle.ParticleType;
import basis.effects.particleEffects.ParticleEffect;
import basis.effects.particleEffects.ParticleEmitter;
import extra.Res;
import util.math.Vec;

public class Meteor implements ParticleEffect {
	
	double vel = 400;
	
	public static final ParticleType METEOR = new ParticleType(Res.getAtlas("meteor").tex(0, 0));
	
	public ParticleEmitter meteor = new ParticleEmitter(1, 0, METEOR, 100000){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			p.rot = 0.375f;
			
			p.rad = 3f;
			p.lived = lifeSpan;
		}
	};
	public static final ParticleType SMOKE = new ParticleType(Res.getTex("smokeParticle"));
	public ParticleEmitter smoke = new ParticleEmitter(1000, 400, SMOKE, 2){
		
		public void makeParticle(Particle p) {
			double r = random.nextInt(particleRadius)*1.5;
			double phi = random.nextDouble()*2*Math.PI; 
			p.pos.set(pos.x + (r*Math.cos(phi)), pos.y + (r*Math.sin(phi)));
			p.vel.set(-vel, (random.nextFloat() - 0.5f)*vel*0.5);
			p.col.set(0.05f, 0.05f, 0.05f, 1f);
			p.lived = 0;
			p.rad = 3;
		}
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.shift(ParticleEffect.wind, delta);
		}
		public void colorInterpolator(Particle p, float delta) {
			if(p.lived/lifeSpan < 0.1) {
				p.col.a = (float) 10*p.lived/lifeSpan;
			} else {
				p.col.a = (float) 1*(0.1f + (lifeSpan-p.lived) /lifeSpan);
			}
		}
		public void radiusInterpolator(Particle p, float delta){
//			p.rad = 0.1f + (2.5f*(p.lived)/lifeSpan);
		}
	};
	public static final ParticleType FIRE = new ParticleType(Res.getTex("smokeParticle"));
	public ParticleEmitter fire = new ParticleEmitter(500, 200, SMOKE, 2){
		
		public void makeParticle(Particle p) {
			double r = random.nextInt(particleRadius);
			double phi = random.nextDouble()*2*Math.PI; 
			p.pos.set(pos.x + (r*Math.cos(phi)), pos.y + (r*Math.sin(phi)));
			p.vel.set(-vel, (random.nextFloat() - 0.5f)*vel*0.5);
			p.col.set(0.69f, 0.15f, 0.10f, 1f);
			p.lived = 0;
			p.rad = 3;
			p.rot = random.nextFloat();
		}
		public void rotationInterpolator(Particle p, float delta) {
			if(p.rot > 0.5){
				p.rot = 1 - (1f/5)*(lifeSpan - p.lived);
			} else {
				p.rot = (1f/5)*(lifeSpan - p.lived);
			}
		}
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.shift(ParticleEffect.wind, delta);
		}
		public void colorInterpolator(Particle p, float delta) {
			if(p.lived/lifeSpan < 0.1) {
				p.col.a = (float) 10*p.lived/lifeSpan;
			} else {
				p.col.a = (float) 1*(0.1f + (lifeSpan-p.lived) /lifeSpan);
			}
		}
		public void radiusInterpolator(Particle p, float delta){
//			p.rad = 0.1f + (2.5f*(p.lived)/lifeSpan);
		}
	};
	
	private Vec pos;
	private int particleRadius = 100;
	
	public Meteor(Vec pos) {
		this.pos = pos.copy();
		meteor.emittParticle(0);
		meteor.emitting = false;
	}

	@Override
	public void update(double delta) {
		meteor.tick((float)delta);
		smoke.tick((float)delta);
		fire.tick((float)delta);
	}

	@Override
	public void render(float scaleX, float scaleY) {
		meteor.render(scaleX, scaleY);
//		GL11.glBlendFunc(GL11.GL_, dfactor);
		fire.render(scaleX, scaleY);
		smoke.render(scaleX, scaleY);
	}

	@Override
	public void terminate() {
		meteor.terminate();
		smoke.terminate();
		fire.terminate();
	}

	@Override
	public boolean living() {
		return true;
	}

	@Override
	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec mouseDelta) {
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
