package particles;

import particles.Particle.ParticleType;
import render.TexFile;
import util.math.Vec;

public class RainEffect implements ParticleEffect{
	
	public static final ParticleType RAINDROP = new ParticleType(new TexFile("res/particles/Raindrop.png"));
	
	public ParticleEmitter drops = new ParticleEmitter(100, 50, RAINDROP, 1.5f){

		public void makeParticle(Particle p) {
			p.pos.set(pos.x + random.nextInt((int)size.x), pos.y + random.nextInt((int)size.y));
			p.vel.set((random.nextFloat() - 0.5f)*0.1f, -150);//-0.8f
			p.col.set(0.8f, 0.8f, 0.8f, 0.4f);
			p.rad = 1;
		}

		public void velocityInterpolator(Particle p, float delta) {
			p.vel.y -= 1000f*delta;
			collision(p, delta);
		}

		public void colorInterpolator(Particle p, float delta) {}

		public void rotationInterpolator(Particle p, float delta) {}

		public void radiusInterpolator(Particle p, float delta) {}
		
		public void collision(Particle p, float delta){
//			for(Material mat : Material.values()){//	iterate materials
//				if(mat.solid){
//					for(Node c : WorldWindow.sectors[1].areas[mat.ordinal()].cycles){//	iterate lines
//						Node n = c;
//						 do {
//							n = n.next;
//							if(Line2D.linesIntersect(p.pos.x, p.pos.y, pos.plus(p.vel).x, pos.plus(p.vel).y, n.last.p.x, n.last.p.y, n.p.x, n.p.y)){
//								p.live = 0;//TODO destroy drop with other particles/little animation
//								p.vel.scale(-2f);
//								break;
//							}
//						} while (n != c);
//					}
//				}
//			}
		}
	};

	public Vec pos;
	public Vec size = new Vec();
	
	public RainEffect(Vec vec, float width, float height){
		this.pos = vec.copy();
		size.set(width, height);
	}
	
	@Override
	public void tick(float dTime){
		drops.tick(dTime);
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
		return false;
	}
	
}
