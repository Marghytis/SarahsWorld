package world.objects.ai;

import main.Main;
import render.Texture;
import util.math.UsefulF;
import util.math.Vec;
import world.WorldContainer.WorldField;
import world.objects.Thing;



public class Grounding extends AiPlugin {

	public boolean g;
	public WorldField link;
	public double speed;
	public boolean sprint;
	public float acc;
	public double yOffset;
	public boolean friction;
	//standing	walking	sprinting	jumping	flying	landing
	//0			1		2			3		4		5
	
	public Grounding(Thing t, boolean friction, double yOffset, Texture standing, Texture walking, Texture sprinting, Texture jumping, Texture flying, Texture landing){
		super(t, standing, walking, sprinting, jumping, flying, landing);
		this.yOffset = yOffset;
		this.friction = friction;
	}

	public Grounding(Thing t, boolean friction, double yOffset, WorldField link, Texture standing, Texture walking, Texture sprinting, Texture jumping, Texture flying, Texture landing){
		this(t, friction, yOffset, standing, walking, sprinting, jumping, flying, landing);
		g = true;
		this.link = link;
	}
	
	
	public boolean action(double delta) {
		if(g){
			Vec pos = t.pos.p.copy().shift(0, yOffset);
			if(friction){
				double friction = link.mat.deceleration*speed*delta;
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
				Vec i = new Vec(Integer.MIN_VALUE, Integer.MIN_VALUE);
				WorldField[] f = new WorldField[1];
				Main.world.columns.forEach((c) -> {
					WorldField field = c.fields[0];
					Vec[] intersections = UsefulF.circleIntersection(field.p4.p, field.p3.p, pos, UsefulF.abs(speed*delta));
					if(speed > 0){
						if(intersections[1] != null && intersections[1].y > i.y){
							i.set(intersections[1]);
							f[0] = field;
						}
					} else {
						if(intersections[0] != null && intersections[0].y > i.y){
							i.set(intersections[0]);
							f[0] = field;
						}
					}
				});

				if(f[0] != null){
					t.vel.v.set(i.minus(pos).scale(1/delta));
					link = f[0];
				}
				acc = 0;
			}
			if(speed < 0){
				t.ani.dir = true;
			}
			if(speed > 0){
				t.ani.dir = false;
			}
		}
		return false;
	}

	public void jump(){
		t.ani.setTex(texs[3], () -> {
			leaveGround(Math.cos(Math.atan(link.getTopLine().size.slope()))*speed, 400.0);
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
		t.ani.setTex(texs[5], () -> t.ani.setTex(t.ani.texs[0]));
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