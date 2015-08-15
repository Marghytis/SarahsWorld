package effects.particles;

import render.Texture;
import util.math.Vec;
import world.WorldWindow;
import effects.particles.Particle.ParticleType;

public class WaterSplash implements ParticleEffect{

	public static final ParticleType WATER_DROP = new ParticleType(new Texture("res/particles/Blood_drop.png", -0.5, -0.5));
	
	public ParticleEmitter drops = new ParticleEmitter(30, 1, WATER_DROP, 1){

		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*200f, (random.nextFloat() - 0.5f)*200f);//-0.8f
			p.col.set(WorldWindow.waterColor);
			p.rad = 0.5f;
		}

		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x *= 0.99f;
			p.vel.y -= 5f;
		}

		public void colorInterpolator(Particle p, float delta) {
			p.col.a *= 0.97f;
		}

		public void rotationInterpolator(Particle p, float delta) {}

		public void radiusInterpolator(Particle p, float delta) {
			p.rad *= 0.99f;
		}
	};

	public Vec pos;
	public float count = 1;

	public WaterSplash(Vec pos){
		this.pos = pos.copy();
		for(int i = 0; i < drops.particles.length; i++){
			drops.emittParticle(0);
		}
		drops.emitting = false;
	}
	
	public void update(double dTime){
		drops.tick((float)dTime);
		count -= dTime;
	}
	
	public void render(){
		drops.render();
	}
	
	public void finalize(){
		drops.finalize();
	}

	public boolean living() {
		return count > 0;
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		return false;
	}

	public boolean keyPressed(int key) {
		return false;
	}

	@Override
	public boolean keyReleased(int key) {
		// TODO Auto-generated method stub
		return false;
	}
}
