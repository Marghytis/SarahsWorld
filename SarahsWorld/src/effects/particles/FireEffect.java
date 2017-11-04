package effects.particles;

import effects.particles.Particle.ParticleType;
import main.*;
import util.math.Vec;

public class FireEffect implements ParticleEffect{

	public static final ParticleType SMOKE = new ParticleType(Res.smokeParticle);
	public ParticleEmitter smoke = new ParticleEmitter(100, 50, SMOKE, 2){
		
		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt(size), pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*50f, 100f);
			p.col.set(0.4f, 0.4f, 0.4f, 1f);
			p.lived = random.nextFloat() - 0.5f;
		}
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.shift(ParticleEffect.wind, delta);
		}
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = (float) (lifeSpan-p.lived) /lifeSpan;
		}
		public void radiusInterpolator(Particle p, float delta){
			p.rad = 0.1f + (2.5f*(p.lived)/lifeSpan);
		}
	};
	public static final ParticleType FLAME = new ParticleType(Res.flameParticle);
	
	public ParticleEmitter flame = new ParticleEmitter(50, 50, FLAME, 1){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt(size), pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*10f, 100f);
			p.col.set(0.5f, 0.5f, 0.1f, 0.5f);
			p.rot = random.nextFloat();
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.shift(ParticleEffect.wind, delta);
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = (lifeSpan - p.lived)*0.8f /lifeSpan;
			p.col.r = 0.5f + (lifeSpan - p.lived)*0.4f/lifeSpan;
			p.col.g = 0.5f - (lifeSpan - p.lived)*0.2f/lifeSpan;//0.9f, 0.3f, 0.1f, 0.5f
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {
			if(p.rot > 0){
				p.rot = (1f/5)*(lifeSpan - p.lived);
			} else {
				p.rot = -(1f/5)*(lifeSpan - p.lived);
			}
		}
		
		@Override
		public void radiusInterpolator(Particle p, float delta){
			p.rad = 2*((lifeSpan - p.lived)/lifeSpan);
		}

	};
	
	public static final ParticleType SPARK = new ParticleType(Res.sparkParticle);
	
	public ParticleEmitter spark = new ParticleEmitter(40, 20, SPARK, 1.5f){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt(size), pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*10, 100);
			p.col.set(1f, 0.6f, 0.1f, 1f);
			p.rot = random.nextFloat();
			p.rad = 0.1f;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x += delta*(ParticleEffect.wind.x + (random.nextFloat() - 0.5f)*5f);
			p.vel.y += ParticleEffect.wind.y*delta;
		}
	};
	
	public static final ParticleType LIGHT = new ParticleType(Res.fireParticle);
	
	public ParticleEmitter light = new ParticleEmitter(5, 5, LIGHT, 1){
		
		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt(size), pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*10, 100);
			p.col.set(1f, 1f, 1f, 1f);
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = (lifeSpan - p.lived)*1.0f /lifeSpan;
		}
	};

	
	public Vec pos = new Vec();
	public int size = 20;
//	public Lightmap lightmap;
	
	public FireEffect(Vec pos){
		this.pos.set(pos);
//		this.lightmap = lightmap;
	}
	
	@Override
	public void update(double dTime){
		smoke.tick((float)dTime);
		flame.tick((float)dTime);
		spark.tick((float)dTime);
		light.tick((float)dTime);
	}
	
	@Override
	public void render(float scaleX, float scaleY){
		smoke.render(scaleX, scaleY);
		flame.render(scaleX, scaleY);
		spark.render(scaleX, scaleY);
		light.render(scaleX, scaleY);
	}
	
	@Override
	public void terminate(){
		smoke.terminate();
		flame.terminate();
		spark.terminate();
		light.terminate();
	}

	@Override
	public boolean living() {
		return true;
	}

	@Override
	public boolean pressed(int button, Vec mousePos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
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
