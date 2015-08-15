package world.things.newPlugins;

import util.math.Vec;
import world.things.AiPlugin;
import world.things.ThingProps;



public class Grounding extends AiPlugin {

	public void setAni(ThingProps t, int acc) {
		String aniName = t.ani.ani.name;
		if(aniName.equals("sprint") || aniName.equals("walk") || aniName.equals("stand")){
			if(acc != 0){
				if(acc > 1){
					t.type.ani.setAnimation(t, "sprint");
				} else {
					t.type.ani.setAnimation(t, "walk");
				}
			} else {
				t.type.ani.setAnimation(t, "stand");
			}
		}
		if(acc > 0) t.dir = true;
		else if(acc < 0) t.dir = false;
	}

//	Vec finalIntersection = new Vec(), lastVec = new Vec(), currentVec = new Vec();
//	
//	public boolean action(double delta) {
//		if(g){
//			Vec pos = t.pos.copy().shift(0, yOffset);
//			if(friction){
//				double friction = link.mats.write.previous.data.deceleration*speed*delta;
//				if(acc == 0 && UsefulF.abs(friction*delta) > UsefulF.abs(speed)){
//					speed = 0;
//					t.vel.v.set(0, 0);
//				} else {
//					acc -= friction;
//					speed += acc*delta;
//				}
//			}
//			if(UsefulF.abs(speed) < 0.1){
//				speed = 0;
//				acc = 0;
//				t.vel.v.set(0, 0);
//			} else {
//				finalIntersection.set(Integer.MIN_VALUE, Integer.MIN_VALUE);
//				Vertex f = null;
//				for(Column c = Main.world.window.leftEnd; c != Main.world.window.rightEnd &&  c != null; c = c.right){
//					if(c.collisionVec == -1) continue;
//					if(c.equals(Main.world.window.leftEnd)){
//						lastVec.set(c.xReal, c.vertices[waterWalking ? c.collisionVecWater : c.collisionVec].y);
//						currentVec = lastVec.copy();
//					} else {
//						Vertex field = c.vertices[waterWalking ? c.collisionVecWater : c.collisionVec];
//						Vec[] intersections = UsefulF.circleIntersection(lastVec, currentVec.set(c.xReal, field.y), pos, UsefulF.abs(speed*delta));
//						lastVec.set(currentVec);
//						if(speed > 0){
//							if(intersections[1] != null && intersections[1].y > finalIntersection.y){
//								finalIntersection.set(intersections[1]);
//								f = field;
//							}
//						} else {
//							if(intersections[0] != null && intersections[0].y > finalIntersection.y){
//								finalIntersection.set(intersections[0]);
//								f = field;
//							}
//						}
//					}
//				};
//
//				if(f != null){
//					t.vel.v.set(finalIntersection.minus(pos).scale(1/delta));
//					f.parent.add(t);
//					link = f;
//				}
//				acc = 0;
//			}
//			if(speed < 0){
//				t.ani.dir = true;
//			}
//			if(speed > 0){
//				t.ani.dir = false;
//			}
//		} else {
//			if(link == null || (int)(t.pos.x/Column.step) != link.parent.xIndex){
//				Column below = Main.world.window.get((int)(t.pos.x/Column.step));
//				link = below.vertices[below.collisionVec];
//				link.parent.add(t);
//			}
//		}
//		return false;
//	}
	
	Vec topLine = new Vec();
	public void jump(ThingProps t){
		t.type.ani.setAnimation(t, "jump", () -> {
			t.type.physics.leaveGround(t, t.vel.copy().set(0, 1000));//ortho(t.vel.x > 0)
//			t.type.phys.leaveGround(Math.cos(Math.atan(t.link.parent.getTopLine(topLine).slope()))*t.vel.length(), 400.0);
			t.type.ani.setAnimation(t, "fly");
		});
	}
//	
//	public void leaveGround(double vx, double vy){
//		g = false;
//		t.pos.y++;
//		t.vel.v.set(vx, vy);
//		speed = 0;
//	}
	
	public void land(ThingProps t) {
		t.type.ani.setAnimation(t, "land", () -> {
			t.type.ani.setAnimation(t, "stand");
		});
	}
}