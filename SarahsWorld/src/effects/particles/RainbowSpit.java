package effects.particles;

import effects.particles.Particle.ParticleType;
import render.TexFile;
import util.Color;
import util.math.Vec;
import world.things.Thing;

public class RainbowSpit implements ParticleEffect{
	
	public static final ParticleType RAINBOW = new ParticleType(new TexFile("SarahsWorld/res/particles/Spark.png"));
	
	public ParticleEmitter rainbow = new ParticleEmitter(210, 200, RAINBOW, 1){
		
		int color = 0;
		
		Color[] colors = {
				new Color(1, 0, 0),	
				new Color(1, 0.5f, 0),	
				new Color(1, 1, 0),	
				new Color(0, 1, 0),	
				new Color(0, 0.7f, 1),	
				new Color(0.6f, 0, 1),	
		};
		
		float[] speedY = {
				60f,
				50f,
				40f,
				30f,
				20f,
				10f,
		};
		
		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y + (-color*2));
			p.vel.set(/*source.ani.dir ? -0.2f : */200f, speedY[color]);
			p.col.set(colors[color]);
			p.rot = random.nextInt(3)-1;
			color = (color+1)%6;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.y -= 2f;
//			if(World.sarah != null){
//				if(World.sarah.animator.tex.texs[0][0].box.plus(World.sarah.pos).contains(p.pos)){
//					World.sarah.hitBy(source, null);
//				}
//			}
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = (lifeSpan - p.lived) /lifeSpan;
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {
			if(p.rot > 0){
				p.rot = ((float)Math.PI/10)*(lifeSpan - p.lived);
			} else {
				p.rot = -((float)Math.PI/10)*(lifeSpan - p.lived);
			}
		}
		
		@Override
		public void radiusInterpolator(Particle p, float delta){
			p.rad = 1 + ((lifeSpan - p.lived)/lifeSpan*0.5f);
		}
	};
	
	Vec pos;
	float live = 1.5f;
	Thing source;
	
	public RainbowSpit(Vec pos, Thing source){
		this.pos = pos.copy();
		this.source = source;
	}
	
	@Override
	public void update(double delta) {
		if(live < 1){
			rainbow.emitting = false;
		}
		rainbow.tick((float)delta);
		live -= delta;
	}

	@Override
	public void render() {
		rainbow.render();
	}
	
	@Override
	public void finalize(){
		rainbow.finalize();
	}

	@Override
	public boolean living() {
		return live > 0;
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

}
