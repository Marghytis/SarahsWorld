package effects.particleEffects;

import effects.particleEffects.Particle.ParticleType;
import main.Res;
import util.math.Vec;

public class Fog implements ParticleEffect {



	public static final ParticleType FOG = new ParticleType(Res.getTex("fogParticle"));
	public ParticleEmitter fog = new ParticleEmitter(100, 0, FOG, Float.MAX_VALUE){

		float radius = 30, T = 10;
		
		public void makeParticle(Particle p) {
			
			double r = random.nextInt(size.xInt()),
					phi = random.nextDouble()*Math.PI*2,
					x = size.y*r*Math.cos(phi),
					y = r*Math.sin(phi);
			
			p.someVec = new Vec(pos.x + x, pos.y + y);
			p.someFloat = random.nextFloat()*10;// + radius*UsefulF.cos100[100*(int)(p.irgendenFloat/T)%1]  |||| + radius*UsefulF.cos100[100*(int)(p.irgendenFloat/T)%1]
			p.pos.set(p.someVec);
			p.col.set(0.75f, 0.75f, 0.75f,0.25f);
			p.rad = random.nextFloat()*5+1;
		}
		
		public void velocityInterpolator(Particle p, float delta){
			double angle = Math.PI*2*(p.someFloat+p.lived)/T;
			p.pos.set(p.someVec).shift(radius*Math.cos(angle), radius*Math.sin(angle));
			p.lived %= T;
		};
	};
	
	public Vec pos = new Vec(), size = new Vec();
	
	public Fog(int x, int y, int r, int xScale, int particles){
		pos.set(x, y);
		size.set(r, xScale);
		for(int i = 0; i < particles; i++){
			fog.emittParticle(0);
		}
		fog.emitting = false;
	}
	public void update(double delta) {
		fog.tick((float)delta);
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
	public boolean keyReleased(int key) {
		return false;
	}
	public void render(float scaleX, float scaleY) {
		fog.render(scaleX, scaleY);
	}
	public boolean living() {
		return true;
	}
	public void terminate() {
		
	}

	@Override
	public boolean charTyped(char ch) {
		return false;
	}
}
