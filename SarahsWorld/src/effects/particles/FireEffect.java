package effects.particles;

import org.lwjgl.opengl.GL11;

import render.Texture;
import util.math.Vec;
import core.Window;
import effects.particles.Particle.ParticleType;

public class FireEffect implements ParticleEffect{

	public static final ParticleType SMOKE = new ParticleType(new Texture("res/particles/Smoke.png", -0.5, -0.5));

	public ParticleEmitter smoke = new ParticleEmitter(100, 50, SMOKE, 2){
		
		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt(size), pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*50f, 100f);
			p.col.set(0.4f, 0.4f, 0.4f, 1f);
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x += 1f;
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = (float) (lifeSpan-p.lived) /lifeSpan;
		}
		
		@Override
		public void radiusInterpolator(Particle p, float delta){
			p.rad = 0.1f + (2.5f*(p.lived)/lifeSpan);
		}
		
	};
	
	public static final ParticleType FLAME = new ParticleType(new Texture("res/particles/Flame.png", -0.5, -0.5));
	
	public ParticleEmitter flame = new ParticleEmitter(50, 50, FLAME, 1){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt(size), pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*10f, 100f);
			p.col.set(0.5f, 0.5f, 0.1f, 0.5f);
			p.rot = random.nextInt(3)-1;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x += 1f;
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
				p.rot = ((float)Math.PI/10)*(lifeSpan - p.lived);
			} else {
				p.rot = -((float)Math.PI/10)*(lifeSpan - p.lived);
			}
		}
		
		@Override
		public void radiusInterpolator(Particle p, float delta){
			p.rad = 2*((lifeSpan - p.lived)/lifeSpan);
		}

//		public void renderParticles(){
//			type.tex.bind();
//				for(Particle p : particles){
//					if(p.live > 0){
//						renderParticle(p);
//					}
//				}
//			TexRegion.bindNone();
//		}

		@Override
		public void renderParticle(Particle p) {
			GL11.glColor4f(p.col.r, p.col.g, p.col.b, p.col.a);
			GL11.glPushMatrix();
				GL11.glTranslated(p.pos.x, p.pos.y, 0);
				GL11.glRotatef(p.rot, 0, 0, 1);
				GL11.glScalef(p.rad, p.rad, 0);
					GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
			GL11.glPopMatrix();
		}
		
	};
	
	public static final ParticleType SPARK = new ParticleType(new Texture("res/particles/Spark.png", -0.5, -0.5));
	
	public ParticleEmitter spark = new ParticleEmitter(10, 5, SPARK, 2){

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt(size), pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*10, 100);
			p.col.set(1f, 0.6f, 0.1f, 1f);
			p.rot = random.nextInt(3)-1;
			p.rad = 0.5f;
		}

		@Override
		public void velocityInterpolator(Particle p, float delta) {
			p.vel.x += (random.nextFloat() - 0.5f)*5f + 1f;
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
//			p.col.a = (float) (p.live*0.8f) /startLife;
//			p.col.r = 0.5f + p.live*0.4f/startLife;
//			p.col.g = 0.5f - p.live*0.2f/startLife;//0.9f, 0.3f, 0.1f, 0.5f
			if(p.lived > lifeSpan/2){
				p.col.set(0.1f, 0.1f, 0.1f, 1);
			}
		}

		@Override
		public void rotationInterpolator(Particle p, float delta) {
			if(p.rot > 0){
				p.rot = ((float)Math.PI/10)*(lifeSpan - p.lived);
			} else {
				p.rot = -((float)Math.PI/10)*(lifeSpan - p.lived);
			}
		}
	};
	
	public static final ParticleType LIGHT = new ParticleType(new Texture("res/particles/Fire.png", -0.5, -0.5));
	
	public ParticleEmitter light = new ParticleEmitter(5, 5, LIGHT, 1){
		
		@Override
		public void renderParticles(){
//			type.tex.bind();
//				lightmap.bind();
//					for(Particle p : particles){
//						if(p.lived > 0){
//							renderParticle(p);
//						}
//					}
//				lightmap.release();
//			type.tex.release();
		}
		
		@Override
		public void renderParticle(Particle p){
//			ARBShaderObjects.glUniform4fARB(glGetUniformLocationARB(Shader.Test.handle, "particleColor"), p.col.r, p.col.g, p.col.b, p.col.a);
//			GL11.glPushMatrix();
//				GL11.glTranslated(p.pos.x, Window.HEIGHT - p.pos.y, 0);
//				GL11.glRotatef(p.rot, 0, 0, 1);
//				GL11.glScalef(p.rad, p.rad, 0);
//					GL11.glDrawArrays(GL11.GL_RectS, 0, 4);
//			GL11.glPopMatrix();
		}

		@Override
		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt(size), pos.y);
			p.vel.set((random.nextFloat() - 0.5f)*10, 100);
			p.col.set(1f, 1f, 1f, 1f);
			p.rad = 1 + (random.nextFloat()*2);
			
		}

		@Override
		public void colorInterpolator(Particle p, float delta) {
			p.col.a = (lifeSpan - p.lived)*1.0f /lifeSpan;
		}
	};

	
	public Vec pos = new Vec(Window.WIDTH/4, Window.HEIGHT/2);
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
	public void render(){
		smoke.render();
		flame.render();
		spark.render();
		light.render();
	}
	
	@Override
	public void finalize(){
		smoke.finalize();
		flame.finalize();
		spark.finalize();
		light.finalize();
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
	
}
