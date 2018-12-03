package effects.particles;

import java.util.List;

import effects.particles.Particle.ParticleType;
import item.ItemType;
import main.Res;
import things.Technique.HitEffect;
import things.Thing;
import util.Color;
import util.math.Vec;

public class RainbowSpit implements ParticleEffect{
	
	public static final ParticleType RAINBOW = new ParticleType(Res.getTex("sparkParticle"));
	
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
			p.pos.set(pos.x, pos.y - (color));
			p.vel.set(-dir*200f, speedY[color]);
			p.col.set(colors[color]);
			p.rot = random.nextInt(3)-1;
			color = (color+1)%6;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.y -= 2f;
			if(targets != null){
				for(Thing target : targets){
					if(target.box.copy().shift(target.pos).contains(p.pos)){
						effect.start(source, source.type.attacking.calculateDamage(source, target, ItemType.NOTHING, "spit"), target);
					}
				}
			}
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
			p.rad = 0.1f + (p.lived/lifeSpan*2f);
		}
	};
	
	Vec pos;
	float live = 1.5f;
	Thing source;
	List<Thing> targets;
	HitEffect effect;
	int dir;
	
	public RainbowSpit(Vec pos, int dir){
		this(pos, dir, null, null, null);
	}
	
	public RainbowSpit(Vec pos, int dir, Thing source, List<Thing> selected, HitEffect effect){
		this.pos = pos.copy();
		this.dir = dir;
		this.source = source;
		this.targets = selected;
		this.effect = effect;
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
	public void render(float scaleX, float scaleY) {
		rainbow.render(scaleX, scaleY);
	}
	
	@Override
	public void terminate(){
		rainbow.terminate();
	}

	@Override
	public boolean living() {
		return live > 0;
	}

	@Override
	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		return false;
	}

	@Override
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
