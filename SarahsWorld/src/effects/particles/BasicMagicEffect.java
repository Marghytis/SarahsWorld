package effects.particles;

import item.ItemType;

import java.util.List;

import effects.particles.Particle.ParticleType;
import render.TexFile;
import util.math.Vec;
import world.World;

public class BasicMagicEffect extends MagicEffect{
	
	public static final ParticleType LIGHT = new ParticleType(new TexFile("SarahsWorld/res/particles/Fire.png", -0.5f, -0.5f));
	
	public ParticleEmitter light = new ParticleEmitter(1, 0, LIGHT, 1000){
			
		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x, pos.y);
			p.vel.set(dir);
			p.col.set(1, 0, 0);
			p.rad = 0.3f;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			for(List<Creature> list : World.creatures) for(Creature c : list){
				if(!(c instanceof Gnat) && c.animator.box.plus(c.pos).intersects(type.tex.box.scaledBy(p.rad).plus(p.pos))){
					c.hitBy(source, ItemType.horn);
					p.lived = 0;
					WorldView.particleEffects.add(new BasicMagicDissapperance(c.pos));
					break;
				}
			}
		}
		
		@Override
		public void colorInterpolator(Particle p, float delta){
			p.col.a = (float) p.lived /lifeSpan;
		}
		
		@Override
		public void killParticle(Particle p){
			sparkle.emitting = false;
		}
	};

	public static final ParticleType SPARKLE = new ParticleType(new TexFile("particles/Sparkle"));
	
	public ParticleEmitter sparkle = new ParticleEmitter(100, 20, SPARKLE, 3000){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(light.particles[0].pos.x, light.particles[0].pos.y);
			float angle = random.nextFloat()*(float)(Math.PI*2);
			
			p.vel.set((float)Math.cos(angle)*(random.nextFloat()*0.3f), (float)Math.sin(angle)*(random.nextFloat()*0.3f) + 0.1f);//-0.8f
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
			p.col.a -= 0.01f;
		}

	};
	
	public BasicMagicEffect(Vec pos, Vec dir, Creature source){
		super(pos, new Vec(dir).scale(0.4f), source);
		light.emittParticle(0);
	}
	
	@Override
	public void update(double delta) {
		light.tick(delta);
		sparkle.tick(delta);
		live -= delta;
	}

	@Override
	public void render() {
		light.render();
		sparkle.render();
	}
	
	@Override
	public void finalize(){
		light.finalize();
		sparkle.finalize();
	}

	@Override
	public boolean living() {
		return live > 0;
	}

}
