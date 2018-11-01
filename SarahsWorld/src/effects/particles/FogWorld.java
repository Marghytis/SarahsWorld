package effects.particles;

import effects.WorldEffect;
import effects.particles.Particle.ParticleType;
import main.Res;
import util.math.Vec;
import world.data.Column;
import world.data.Dir;
import world.render.LandscapeWindow;

public class FogWorld implements ParticleEffect, WorldEffect {

	public static final ParticleType FOG = new ParticleType(Res.getTex("fogParticle"));
	public ParticleEmitter fog = new ParticleEmitter(500, 0, FOG, Float.MAX_VALUE, true){

		float radius = 30, T = 10;
		
		public void makeParticle(Particle p) {

			p.someVec = new Vec(pos.x, pos.y + random.nextInt(height));
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
	
	public Vec pos = new Vec();
	public int height;
	
	public FogWorld(int height){
		this.height = height;
	}
	public int spawn(double x, double y){
		pos.set(x, y);
		return fog.emittParticle(0);
	}
	public void despawn(int ticket) {
		if(ticket != -1)
		fog.destroy(fog.particles[ticket]);
	}
	public void update(double delta) {
		fog.tick((float)delta);
	}
	public void checkInside(LandscapeWindow lw) {
		for(int i = 0; i < fog.particles.length; i++){
			if(fog.particles[i].lived < fog.lifeSpan && (fog.particles[i].pos.x < lw.getEnd(Dir.l).xReal - 2*Column.COLUMN_WIDTH || fog.particles[i].pos.x > lw.getEnd(Dir.r).xReal + 2*Column.COLUMN_WIDTH)){
				fog.destroy(fog.particles[i]);
			}
		}
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
