package things.aiPlugins;

import main.Main;
import menu.Settings.Key;
import things.AiPlugin;
import things.Thing;
import util.math.Function;
import util.math.Vec;
import world.data.Column;



public class Movement extends AiPlugin<Thing> {
	
	public String stand, sneak, walk1, walk2, fly, plunge, swim, sneakyStand;
	public String liftOf, land, dive;

	public Movement(String stand, String walk0, String walk1, String walk2, String swim, String jump, String land, String fly, String dive, String plunge, String sneakyStand){
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
		this.sneakyStand = sneakyStand;
	}
	
	public void setBackgroundAni(Thing t) {
		t.aniPlug.setAnimation(t.backgroundAnimation, null);
	}

	public void setAni(Thing t, double acc) {
//		String aniName = t.ani.ani.name;
		String foregroundRequest = "";
		if(t.where.g){
			if(t.where.water < 0.3){
				if(acc != 0){
					if(Math.abs(acc) > 1){
						t.backgroundAnimation = walk2;
					} else if(Math.abs(acc) == 1){
						t.backgroundAnimation = walk1;
					} else {
						t.backgroundAnimation = sneak;
					}
				} else {
					if(Main.input.isKeyDown(Main.WINDOW, Key.CROUCH.key)) {
						t.backgroundAnimation = sneakyStand;
					} else {
						t.backgroundAnimation = stand;
					}
				}
			}
			else if(t.where.water < 0.3 && t.ani.ani.name != swim){
				foregroundRequest = dive;
				t.backgroundAnimation = swim;
			}
		} else {
			if(t.where.water > 0){
				if(acc != 0){
					t.backgroundAnimation = swim;
					//TODO add animation for keeping itself at the surface and possibly a transition with rotation
				} else {
					t.backgroundAnimation = fly;
				}
			} else if(!t.willLandInWater){
				if(t.reallyAir){
					t.backgroundAnimation = fly;
				}
			} else {
				t.backgroundAnimation = plunge;
			}
		}

		if(t.ani.endTask == null){
			if(acc > 0) t.dir = false;
			else if(acc < 0) t.dir = true;
			t.whereBefore.water = t.where.water;
			t.whereBefore.g = t.where.g;
			if(foregroundRequest != "") {
				t.aniPlug.setAnimation(foregroundRequest, () -> t.aniPlug.setAnimation(t.backgroundAnimation));
			} else if(t.backgroundAnimation != ""){
				t.aniPlug.setAnimation(t.backgroundAnimation);
			}
		}
	}
	
	//TRANSITIONS
	Vec topLine = new Vec();
	/**
	 * Transitions from GROUND to AIR
	 * @param t
	 */
	public void jump(Thing t){
		t.aniPlug.setAnimation( liftOf, () -> {
			if(t.where.g){
				t.willLandInWater = willLandInWater(t);
				t.type.physics.leaveGround(t, t.vel.copy().shift(0, Physics.jump));//ortho(t.vel.x > 0)
				t.reallyAir = true;
				t.where.g = false;
				if(t.willLandInWater){
					t.aniPlug.setAnimation( dive, () -> {
						t.aniPlug.setAnimation( plunge);
					});
				} else {
					t.willLandInWater = false;
					t.aniPlug.setAnimation( fly);
					t.backgroundAnimation = fly;
				}
			} else {
				t.willLandInWater = false;
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
			cursor = t.vel.x > 0 ? cursor.right() :  cursor.left();
		}
		return false;
	}
	
	/**
	 * Transitions from AIR to GROUND
	 * @param t
	 */
	public void land(Thing t) {
		t.aniPlug.setAnimation( land, () -> {
			t.aniPlug.setAnimation( stand);
			t.backgroundAnimation = stand;
		});
	}
	
	/**
	 * Transitions from GROUND to AIR
	 * @param t
	 */
	public void liftOf(Thing t) {
		t.aniPlug.setAnimation( liftOf, () -> {
			t.aniPlug.setAnimation( fly);
			t.backgroundAnimation = fly;
		});
	}
	
	/**
	 * Transitions from GROUND to WATER
	 * @param t
	 */
	public void dive(Thing t){
		t.aniPlug.setAnimation( dive, () -> {
			t.aniPlug.setAnimation( swim);
		});
	}
}