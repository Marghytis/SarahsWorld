package world.things.aiPlugins;

import effects.particles.WaterSplash;
import main.Main;
import main.Settings;
import util.math.UsefulF;
import util.math.Vec;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.things.AiPlugin;
import world.things.Thing;

public class MatFriction extends AiPlugin{

	public boolean swimming;
	
	public MatFriction(Thing thing) {
		super(thing);
	}
	
	public Vec lastBouyancy = new Vec();
	public double splashCooldown, splashCooldown2;

	public boolean action(double delta) {
		//which material is the thing in?
		Vertex vert = null;
		try {
			Column c = t.ground.link.parent;
			int yIndex = -1;
			double y = t.pos.p.y + t.ani.box.pos.y + 20;//+ t.ani.box.pos.y + 10
			//get the material the thing is located in
			while(c.vertices[yIndex+1].y > y) yIndex++;
			if(yIndex != -1 && !c.vertices[yIndex].mats.empty()) vert = c.vertices[yIndex];
		} catch(IndexOutOfBoundsException e){
			//not being in any material
		}
		
		if(vert != null && vert.mats.read.data.solidity == 1){
			
			if((splashCooldown <= 0 || (t.ground.g && splashCooldown2 <= 0)) && (t.vel.v.x > 5 || t.vel.v.x < -5)){
				Main.world.window.effects.add(new WaterSplash(new Vec(t.pos.p.x, vert.y)));
				splashCooldown = 0.3;
				splashCooldown2 = 0.2;
			}
			if(splashCooldown > 0) splashCooldown -= delta;
			if(splashCooldown2 > 0) splashCooldown2 -= delta;
			
			double ratio = (vert.y - (t.pos.p.y + t.ani.box.pos.y - 10))/t.ani.box.size.y;
			ratio = Math.min(ratio, 1);
			lastBouyancy.set(vert.mats.read.data.bouyancy.x*ratio, vert.mats.read.data.bouyancy.y*ratio);
			t.acc.a.shift(lastBouyancy);

			swimming = ratio > 0.5;//100 + lastBouyancy.y > -t.gravity.grav.y || 

			if(swimming){
				if(t.acc.a.x < 0){
					t.ani.dir = true;
				}
				if(t.acc.a.x > 0){
					t.ani.dir = false;
				}
				t.ani.setTex(t.ani.texs[0]);
				if(t.ground.g && lastBouyancy.y - 10> -t.gravity.grav.y){
					t.ground.leaveGround(t.vel.v.x, 1);
				}
			}
		} else {
			swimming = false;
		}
			
		if(!t.ground.g && Settings.FRICTION && vert != null){
				double frictionX = vert.mats.read.data.deceleration*t.vel.v.x*delta;
				if(t.acc.a.x == 0 && UsefulF.abs(frictionX*delta) > UsefulF.abs(t.vel.v.x)){
					t.vel.v.set(0, t.vel.v.y);
				} else {
					t.acc.a.shift(-frictionX, 0);
				}
				double frictionY = vert.mats.read.data.deceleration*t.vel.v.y*delta;
				if(t.acc.a.y == 0 && UsefulF.abs(frictionY*delta) > UsefulF.abs(t.vel.v.y)){
					t.vel.v.set(t.vel.v.x, 0);
				} else {
					t.acc.a.shift(0, -frictionY);
				}
		}
		return false;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
