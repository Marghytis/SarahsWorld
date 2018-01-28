package effects.particles;

import effects.particles.Particle.ParticleType;
import main.Res;
import util.math.Vec;

public class BasicMagicEffect extends MovingEffect{
	
	public static final ParticleType LIGHT = new ParticleType(Res.fireParticle);
	
	public ParticleEmitter light = new ParticleEmitter(1, 0, LIGHT, 1000){
			
		@Override
		public void makeParticle(Particle p) {
			p.pos.set(lastPos);
			p.col.set(1, 0, 0);
			p.rad = 0.3f;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.pos.set(lastPos);
		}
		
		@Override
		public void colorInterpolator(Particle p, float delta){
			p.col.a = (float) (-lived /life)+1;
		}
		
	};

	public static final ParticleType SPARKLE = new ParticleType(Res.sparkleParticle);
	
	public ParticleEmitter sparkle = new ParticleEmitter(300, 200, SPARKLE, 1){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(lastPos);
			float angle = random.nextFloat()*(float)(Math.PI*2);
			
			p.vel.set((float)Math.cos(angle)*(random.nextFloat()*100), (float)Math.sin(angle)*(random.nextFloat()*100));//-0.8f
			p.col.set(1f, 0.7f, 0.0f, light.particles[0].col.a);
			p.rad = 0.3f;
			p.lived = lifeSpan;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.97f;
			p.vel.y *= 0.97f;
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a *= 0.97f;
		}

	};
	
	public static interface Factory<T> {
		public T produce();
	}
	
	double life, lived;
	
	public BasicMagicEffect(Factory<Vec> source, double duration){
		super(source);
		life = duration;
		lived = 0;
		light.emittParticle(0);
	}
	
	@Override
	public void update(double delta) {
		super.update(delta);
		light.tick((float)delta);
		sparkle.tick((float)delta);
		lived += delta;
	}

	@Override
	public void render(float scaleX, float scaleY) {
		light.render(scaleX, scaleY);
		sparkle.render(scaleX, scaleY);
	}
	
	@Override
	public void terminate(){
		light.terminate();
		sparkle.terminate();
	}

	public boolean living() {
		return lived <= life;
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec mouseDelta) {
		return false;
	}

	public boolean keyPressed(int key) {
		return false;
	}

	public boolean keyReleased(int key) {
		return false;
	}

	public boolean charTyped(char ch) {
		return false;
	}

}
