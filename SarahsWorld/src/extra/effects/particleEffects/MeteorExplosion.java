package extra.effects.particleEffects;

import basis.effects.particleEffects.Particle;
import basis.effects.particleEffects.ParticleEffect;
import basis.effects.particleEffects.ParticleEmitter;
import basis.effects.particleEffects.Particle.ParticleType;
import extra.Main;
import extra.Res;
import util.math.Vec;

public class MeteorExplosion implements ParticleEffect {
	
	public static final ParticleType FIRE_BALL = new ParticleType(Res.getTex("fireBall"));
	
	public ParticleEmitter fire = new ParticleEmitter(120, 0, FIRE_BALL, 3){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			float angle = random.nextFloat()*(float)(Math.PI*2);
			double speed = 500*random.nextFloat();
			
			p.vel.set((float)Math.cos(angle)*speed, (float)Math.sin(angle)*speed + 0.1f);//-0.8f
//			p.col.set(0.69f, 0.15f, 0.10f, 0.5f);
			p.rad = random.nextFloat()*3+0.7f;
			p.lived = lifeSpan;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.scale(0.97f);
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = 0.5f*(1 - (p.lived/lifeSpan));
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {
		}

	};
	

	public static final ParticleType LIGHT = new ParticleType(Res.getTex("fogParticle"));

	
	public ParticleEmitter smoke = new ParticleEmitter(120, 0, LIGHT, 10){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			float angle = random.nextFloat()*(float)(Math.PI*2);
			double speed = 1200*random.nextFloat();
			
			p.vel.set((float)Math.cos(angle)*speed, (float)Math.sin(angle)*speed + 0.1f);//-0.8f
			p.col.set(0.01f, 0.01f, 0.01f, 0.5f);
			p.rad = random.nextFloat()*10+2;
			p.lived = lifeSpan;
			p.someFloat = random.nextFloat();//less life so they don't all dissappear at the same time...
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.scale(0.97f);
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = 0.5f*(1 - (p.lived/(lifeSpan - p.someFloat)));
			p.col.a = Math.max(0, p.col.a);
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {
		}

	};

	public Vec pos;
	public int count = 1000;

	public MeteorExplosion(Vec pos){
		this.pos = pos.copy();
		for(int i = 0; i < 120; i++){
			smoke.emittParticle(0);
		}
		for(int i = 0; i < 120; i++){
			fire.emittParticle(0);
		}
		smoke.emitting = false;
		fire.emitting = false;
	}
	
	@Override
	public void update(double dTime){
		smoke.tick((float)dTime);
		fire.tick((float)dTime);
		count -= dTime;
		if(!living())
			Main.game().world.window.removeEffect(this);
	}
	
	@Override
	public void render(float scaleX, float scaleY){
		smoke.render(scaleX, scaleY);
		fire.render(scaleX, scaleY);
	}
	
	@Override
	public void terminate(){
		smoke.terminate();
		fire.terminate();
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
	public boolean released(int button, Vec mousePos, Vec mouseDelta) {
		return false;
	}

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
