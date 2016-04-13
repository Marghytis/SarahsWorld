package effects.particles;

import effects.particles.Particle.ParticleType;
import main.Res;
import util.math.Vec;

public class BerryEat implements ParticleEffect{

	
	public static final ParticleType STAR = new ParticleType(Res.rainbowParticle);
	
	public ParticleEmitter stars = new ParticleEmitter(30, 1, STAR, 1){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*200f, (random.nextFloat() - 0.5f)*200f);//-0.8f
			p.col.set(0.4f, 0, 0.9f, 1);
			p.rad = 0.5f + random.nextFloat();
			p.lived = lifeSpan;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.99f;
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a *= 0.95f;
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {}

		@Override
		public void radiusInterpolator(Particle p, float delta) {
			p.rad *= 0.99f;
		}
	};
	
	public static final ParticleType SPARKLE = new ParticleType(Res.sparkleParticle);
	
	public ParticleEmitter sparkle = new ParticleEmitter(stars.particles.length, 1, SPARKLE, 1){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*200f, (random.nextFloat() - 0.5f)*200f);//-0.8f
			p.col.set(0.6f, 0, 0.7f, 1);
			p.rad = 0.5f + random.nextFloat();
			p.lived = lifeSpan;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.99f;
			p.vel.y *= 0.99f;
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a *= 0.95f;
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {}

		@Override
		public void radiusInterpolator(Particle p, float delta) {
			p.rad *= 0.99f;
		}
	};

	public Vec pos;
	public float count = 1;

	public BerryEat(Vec pos){
		this.pos = pos.copy();
		for(int i = 0; i < stars.particles.length; i++){
			stars.emittParticle(0);
			sparkle.emittParticle(0);
		}
		stars.emitting = false;
		sparkle.emitting = false;
	}
	
	@Override
	public void update(double dTime){
		stars.tick((float)dTime);
		sparkle.tick((float)dTime);
		count -= dTime;
	}
	
	@Override
	public void render(){
		stars.render();
		sparkle.render();
	}
	
	@Override
	public void finalize(){
		stars.finalize();
		sparkle.finalize();
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
}
