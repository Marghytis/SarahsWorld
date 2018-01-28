package effects.particles;

import effects.particles.Particle.ParticleType;
import main.Res;
import util.math.Vec;

public class BasicMagicDissapperance implements ParticleEffect {
	
	public static final ParticleType SPARKLE = new ParticleType(Res.sparkleParticle);
	
	public ParticleEmitter sparkle = new ParticleEmitter(100, 1, SPARKLE, 3){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			float angle = random.nextFloat()*(float)(Math.PI*2);
			
			p.vel.set((float)Math.cos(angle)*(random.nextFloat()*0.3f), (float)Math.sin(angle)*(random.nextFloat()*0.3f) + 0.1f).scale(ParticleTest.core.SIZE_HALF.w,ParticleTest.core.SIZE_HALF.h);//-0.8f
			p.col.set(1f, 0.7f, 0.0f, 0.8f);
			p.rad = 0.5f;
			p.lived = lifeSpan;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.97f;
			p.vel.y *= 0.97f;
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = (float) -p.lived /lifeSpan+1;
		}

	};

	public static final ParticleType SMOKE = new ParticleType(Res.smokeParticle);
	
	public ParticleEmitter smoke = new ParticleEmitter(60, 0, SMOKE, 1){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			float angle = random.nextFloat()*(float)(Math.PI*2);
			
			p.vel.set((float)Math.cos(angle)*(random.nextFloat()*0.3f), (float)Math.sin(angle)*(random.nextFloat()*0.3f) + 0.1f).scale(ParticleTest.core.SIZE_HALF.w,ParticleTest.core.SIZE_HALF.h);//-0.8f
			p.col.set(1, 0, 0, 0.1f);
			p.rot = random.nextBoolean() ? 0.1f : -0.1f;
			p.rad = 1;
			p.lived = lifeSpan;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.97f;
			p.vel.y *= 0.97f;
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a -= 0.001f;
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {
			if(p.rot > 0){
				p.rot = ((float)Math.PI/100)*p.lived;
			} else {
				p.rot = -((float)Math.PI/100)*p.lived;
			}
		}

	};

	public Vec pos;
	public int count = 1000;

	public BasicMagicDissapperance(Vec pos){
		this.pos = pos.copy();
		for(int i = 0; i < 60; i++){
			smoke.emittParticle(0);
		}
		for(int i = 0; i < 60; i++){
			sparkle.emittParticle(0);
		}
		smoke.emitting = false;
		sparkle.emitting = false;
	}
	
	@Override
	public void update(double dTime){
		smoke.tick((float)dTime);
		sparkle.tick((float)dTime);
		count -= dTime;
	}
	
	@Override
	public void render(float scaleX, float scaleY){
		smoke.render(scaleX, scaleY);
		sparkle.render(scaleX, scaleY);
	}
	
	@Override
	public void terminate(){
		smoke.terminate();
		sparkle.terminate();
	}

	@Override
	public boolean living() {
		return count > 0;
	}

	@Override
	public boolean pressed(int button, Vec mousePos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec mouseDelta) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyPressed(int key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyReleased(int key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean charTyped(char ch) {
		// TODO Auto-generated method stub
		return false;
	}
}
