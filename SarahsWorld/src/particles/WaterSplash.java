package particles;

import particles.Particle.ParticleType;
import render.TexFile;
import util.math.Vec;
import world.worldGeneration.WorldWindow;

public class WaterSplash implements ParticleEffect{

	public static final ParticleType WATER_DROP = new ParticleType(new TexFile("res/particles/Blood_drop.png"));
	
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
	
	@Override
	public void tick(float dTime){
		drops.tick(dTime);
		count -= dTime;
	}
	
	@Override
	public void render(){
		drops.render();
	}
	
	@Override
	public void finalize(){
		drops.finalize();
	}

	@Override
	public boolean living() {
		return count > 0;
	}
}
