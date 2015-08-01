package world.things.aiPlugins;

import main.Main;
import render.Texture;
import util.math.UsefulF;
import util.math.Vec;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.things.AiPlugin;
import world.things.Thing;



public class Grounding extends AiPlugin {

	public boolean g;
	public Vertex link;
	public double speed;
	public boolean sprint;
	public float acc;
	public double yOffset;
	public boolean friction;
	public boolean waterWalking;
	//standing	walking	sprinting	jumping	flying	landing
	//0			1		2			3		4		5
	
	public Grounding(Thing t, boolean friction, double yOffset, Texture standing, Texture walking, Texture sprinting, Texture jumping, Texture flying, Texture landing){
		super(t, standing, walking, sprinting, jumping, flying, landing);
		this.yOffset = yOffset;
		this.friction = friction;
	}

	public Grounding(Thing t, boolean friction, double yOffset, boolean waterWalking, Vertex link, Texture standing, Texture walking, Texture sprinting, Texture jumping, Texture flying, Texture landing){
		this(t, friction, yOffset, standing, walking, sprinting, jumping, flying, landing);
		g = true;
		this.link = link;
		this.waterWalking = waterWalking;
	}
	
	
	Vec finalIntersection = new Vec(), lastVec = new Vec(), currentVec = new Vec();
	
	public boolean action(double delta) {
		if(g){
			Vec pos = t.pos.p.copy().shift(0, yOffset);
			if(friction){
				double friction = link.mats.write.previous.data.deceleration*speed*delta;
				if(acc == 0 && UsefulF.abs(friction*delta) > UsefulF.abs(speed)){
					speed = 0;
					t.vel.v.set(0, 0);
				} else {
					acc -= friction;
					speed += acc*delta;
				}
			}
			if(UsefulF.abs(speed) < 0.1){
				speed = 0;
				acc = 0;
				t.vel.v.set(0, 0);
			} else {
				finalIntersection.set(Integer.MIN_VALUE, Integer.MIN_VALUE);
				Vertex f = null;
				for(Column c = Main.world.window.leftEnd; c != Main.world.window.rightEnd &&  c != null; c = c.right){
					if(c.collisionVec == -1) continue;
					if(c.equals(Main.world.window.leftEnd)){
						lastVec.set(c.xReal, c.vertices[waterWalking ? c.collisionVecWater : c.collisionVec].y);
						currentVec = lastVec.copy();
					} else {
						Vertex field = c.vertices[waterWalking ? c.collisionVecWater : c.collisionVec];
						Vec[] intersections = UsefulF.circleIntersection(lastVec, currentVec.set(c.xReal, field.y), pos, UsefulF.abs(speed*delta));
						lastVec.set(currentVec);
						if(speed > 0){
							if(intersections[1] != null && intersections[1].y > finalIntersection.y){
								finalIntersection.set(intersections[1]);
								f = field;
							}
						} else {
							if(intersections[0] != null && intersections[0].y > finalIntersection.y){
								finalIntersection.set(intersections[0]);
								f = field;
							}
						}
					}
				};

				if(f != null){
					t.vel.v.set(finalIntersection.minus(pos).scale(1/delta));
					f.parent.add(t);
					link = f;
				}
				acc = 0;
			}
			if(speed < 0){
				t.ani.dir = true;
			}
			if(speed > 0){
				t.ani.dir = false;
			}
		} else {
			if(link == null || (int)(t.pos.p.x/Column.step) != link.parent.xIndex){
				Column below = Main.world.window.get((int)(t.pos.p.x/Column.step));
				link = below.vertices[below.collisionVec];
				link.parent.add(t);
			}
		}
		return false;
	}
	
	Vec topLine = new Vec();

	public void jump(){
		t.ani.setTex(texs[3], () -> {
			leaveGround(Math.cos(Math.atan(link.parent.getTopLine(topLine).slope()))*speed, 400.0);
			t.ani.setTex(texs[4]);
		});
	}
	
	public void leaveGround(double vx, double vy){
		g = false;
		t.pos.p.y++;
		t.vel.v.set(vx, vy);
		speed = 0;
	}
	
	public void land() {
		t.ani.setTex(texs[5], () -> {
			t.ani.setTex(t.ani.texs[0]);
		});
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

	public void setAni(double acc) {
		if(t.ani.animator.getAnimation().equals(texs[0]) || t.ani.animator.getAnimation().equals(texs[1]) || t.ani.animator.getAnimation().equals(texs[2])){
			if(acc != 0){
				if(sprint){
					t.ani.setTex(texs[2]);
				} else {
					t.ani.setTex(texs[1]);
				}
			} else {
				t.ani.setTex(texs[0]);
			}
		}		
	}
}