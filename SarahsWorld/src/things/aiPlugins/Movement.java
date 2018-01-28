package things.aiPlugins;

import main.Main;
import things.*;
import util.math.*;
import world.data.Column;



public class Movement extends AiPlugin {
	
	public String stand, sneak, walk1, walk2, fly, plunge, swim;
	public String liftOf, land, dive;

	public Movement(String stand, String walk0, String walk1, String walk2, String swim, String jump, String land, String fly, String dive, String plunge){
		this.stand = stand;
		this.sneak = walk0;
		this.walk1 = walk1;
		this.walk2 = walk2;
		this.swim = swim;
		this.liftOf = jump;
		this.land = land;
		this.fly = fly;
		this.dive = dive;
		this.plunge = plunge;
	}

	public void setAni(Thing t, int acc) {
		if(t.ani.endTask != null){
			return;
		}
		if(acc > 0) t.dir = false;
		else if(acc < 0) t.dir = true;
//		String aniName = t.ani.ani.name;
		if(t.where.g){
			if(t.where.water < 0.3){
				if(acc != 0){
					if(Math.abs(acc) > 1){
						t.type.ani.setAnimation(t, walk2);
					} else {
						t.type.ani.setAnimation(t, walk1);
					}
				} else {
					t.type.ani.setAnimation(t, stand);
				}
			}
			else if(t.where.water < 0.3 && t.ani.ani.name != swim){
				t.type.ani.setAnimation(t, dive, () -> t.type.ani.setAnimation(t, swim));
			}
		} else {
			if(t.where.water > 0){
				if(acc != 0){
					t.type.ani.setAnimation(t, swim);
					//TODO add animation for keeping itself at the surface and possibly a transition with rotation
				} else {
					t.type.ani.setAnimation(t, fly);
				}
			} else if(!t.willLandInWater){
				if(t.reallyAir){
					t.type.ani.setAnimation(t, fly);
				}
			} else {
				t.type.ani.setAnimation(t, plunge);
			}
		}
		t.whereBefore.water = t.where.water;
		t.whereBefore.g = t.where.g;
	}
	
	//TRANSITIONS
	Vec topLine = new Vec();
	/**
	 * Transitions from GROUND to AIR
	 * @param t
	 */
	public void jump(Thing t){
		if(t.willLandInWater = willLandInWater(t)){
			t.type.ani.setAnimation(t,  liftOf, () -> {
				if(Main.world.avatar.where.g){
					t.type.physics.leaveGround(t, t.vel.copy().shift(0, Physics.jump));
					t.reallyAir = true;
					t.where.g = false;
					t.type.ani.setAnimation(t,  dive, () -> {
						t.type.ani.setAnimation(t, plunge);
					});
				} else {
					t.willLandInWater = false;
				}
			});
		} else 
		t.type.ani.setAnimation(t,  liftOf, () -> {
			if(Main.world.avatar.where.g){
				t.type.physics.leaveGround(t, t.vel.copy().shift(0, Physics.jump));//ortho(t.vel.x > 0)
	//			t.type.phys.leaveGround(Math.cos(Math.atan(t.link.parent.getTopLine(topLine).slope()))*t.vel.length(), 400.0);
				t.reallyAir = true;
				t.type.ani.setAnimation(t, fly);
				t.where.g = false;
			}
		});
	}
	
	public boolean willLandInWater(Thing t){
		double vX = t.vel.x, vY = t.vel.y + Physics.jump;
//		double xPeak = (vX*vY)/Physics.gravity.y + t.pos.x;
		double a = vY/vX, b = 0.5*Physics.gravity.y/(t.type.physics.mass*vX*vX);
		Function f = (x) -> t.pos.y + (a*x) + (b*x*x);
		Column cursor = t.link;
		boolean lastOne = false;
		while(cursor != null){
			if(/*cursor.xReal > xPeak && */cursor.getCollisionY() != cursor.getCollisionYFluid()){
				double y = f.f(cursor.xReal - t.pos.x);
				//if one cursor is below the graph and the other above, that's where she'll collide
				if(cursor.getTopFluidVertex().y < y){
					lastOne = true;
				} else {
					if(lastOne){
						return true;
					} else {
						return false;
					}
				}
			} else {
				lastOne = false;
			}
			cursor = t.vel.x > 0 ? cursor.right :  cursor.left;
		}
		return false;
	}
	
	/**
	 * Transitions from AIR to GROUND
	 * @param t
	 */
	public void land(Thing t) {
		t.type.ani.setAnimation(t, land, () -> {
			t.type.ani.setAnimation(t, stand);
		});
	}
	
	/**
	 * Transitions from GROUND to AIR
	 * @param t
	 */
	public void liftOf(Thing t) {
		t.type.ani.setAnimation(t, liftOf, () -> {
			t.type.ani.setAnimation(t, fly);
		});
	}
	
	/**
	 * Transitions from GROUND to WATER
	 * @param t
	 */
	public void dive(Thing t){
		t.type.ani.setAnimation(t, "dive", () -> {
			t.type.ani.setAnimation(t, "swim");
		});
	}
}