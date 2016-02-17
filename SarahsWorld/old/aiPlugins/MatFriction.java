package world.things.aiPlugins;

import main.Main;
import menu.Settings;
import util.math.UsefulF;
import util.math.Vec;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.things.AiPlugin;
import world.things.Thing;
import world.things.ThingProps;
import world.things.newPlugins.Physics.Where;
import effects.particles.WaterSplash;

public class MatFriction extends AiPlugin{

	public static Vec bouyancy;
	public boolean swimming;
	
	public MatFriction(Thing thing) {
		super(thing);
	}
	
	public boolean update(ThingProps t, double delta){

		
		
		return false;
	}
	
//	public Vec lastBouyancy = new Vec();
//	public double splashCooldown, splashCooldown2;

//	public boolean action(double delta) {
//		
//		Vertex vert = findEnclosingMat(t.ground.link, t.pos.y + t.ani.box.pos.y + 20);
//		
//		if(vert != null && vert.mats.read.data.solidity == 1){
//			
//			if((splashCooldown <= 0 || (t.ground.g && splashCooldown2 <= 0)) && (t.vel.v.x > 5 || t.vel.v.x < -5)){
//				Main.world.window.effects.add(new WaterSplash(new Vec(t.pos.x, vert.y)));
//				splashCooldown = 0.3;
//				splashCooldown2 = 0.2;
//			}
//			if(splashCooldown > 0) splashCooldown -= delta;
//			if(splashCooldown2 > 0) splashCooldown2 -= delta;
//			
//			double ratio = (vert.y - (t.pos.y + t.ani.box.pos.y - 10))/t.ani.box.size.y;
//			ratio = Math.min(ratio, 1);
//			lastBouyancy.set(vert.mats.read.data.bouyancy.x*ratio, vert.mats.read.data.bouyancy.y*ratio);
//			t.acc.a.shift(lastBouyancy);
//
//			swimming = ratio > 0.5;//100 + lastBouyancy.y > -t.gravity.grav.y || 
//
//			if(swimming){
//				if(t.acc.a.x < 0){
//					t.ani.dir = true;
//				}
//				if(t.acc.a.x > 0){
//					t.ani.dir = false;
//				}
//				t.ani.setTex(t.ani.texs[0]);
//				if(t.ground.g && lastBouyancy.y - 10> -t.gravity.grav.y){
//					t.ground.leaveGround(t.vel.v.x, 1);
//				}
//			}
//		} else {
//			swimming = false;
//		}
//			
//		if(!t.ground.g && Settings.FRICTION && vert != null){
//				double frictionX = vert.mats.read.data.deceleration*t.vel.v.x*delta;
//				if(t.acc.a.x == 0 && UsefulF.abs(frictionX*delta) > UsefulF.abs(t.vel.v.x)){
//					t.vel.v.set(0, t.vel.v.y);
//				} else {
//					t.acc.a.shift(-frictionX, 0);
//				}
//				double frictionY = vert.mats.read.data.deceleration*t.vel.v.y*delta;
//				if(t.acc.a.y == 0 && UsefulF.abs(frictionY*delta) > UsefulF.abs(t.vel.v.y)){
//					t.vel.v.set(t.vel.v.x, 0);
//				} else {
//					t.acc.a.shift(0, -frictionY);
//				}
//		}
//		return false;
//	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
