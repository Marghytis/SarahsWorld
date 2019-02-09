package things.aiPlugins;

import things.AiPlugin;
import things.Thing;
import world.World;

public class MidgeAround extends AiPlugin {

	public boolean action(Thing t, double delta) {
//		if(!t.where.g){
		t.otherCooldown -= delta;
		if(t.otherCooldown < 0){
			t.otherCooldown = 1 + World.rand.nextDouble()*1;
			t.flyForce.shift((0.50 - World.rand.nextDouble())*3, (0.40 - World.rand.nextDouble())*3);
		}
		if(t.where.g && World.rand.nextInt(100)==0){
			t.pos.y++;
			t.where.g = false;
			t.flyForce.y = 3;
		}
		t.force.set(t.flyForce);//.shift((0.5f - World.rand.nextDouble())*3f, (0.49f - World.rand.nextDouble())*3f);
//		} else {
//			if(World.rand.nextInt(100) == 0){
//				pos.y++;
//				accelerateFromGround(new Vec(0, 0.0001f));
//			}
//		}
		
//		if(t.where != Where.GROUND){
//			System.out.println("Flap! " + t.ani.ani.name);
//			if(t.ani.ani.name != "flap"){//gliding
//				if(World.rand.nextInt(100) < 20){//start flapping
//					t.type.ani.setAnimation(t, "flap", () -> t.type.ani.setAnimation(t, "fly"));
//					t.flyForce.set((0.5f - World.rand.nextFloat())*100, World.rand.nextFloat()*100 + 50);
//				}
//			} else {//flapping
//				if(t.ani.pos > 1)
//				t.type.physics.applyForce(t, t.flyForce);
//			}
//			if(t.vel.x > 0){
//				t.dir = true;
//			}
//			if(t.vel.x < 0){
//				t.dir = false;
//			}
//		} else {
//			System.out.println("test");
//			if(t.ani.ani != t.type.ani.get(t, "flap")){
//
//				if(World.rand.nextInt(100) == 1){
//					t.type.ani.setAnimation(t, "flap");
//				} else if(World.rand.nextInt(100) == 1){
//					t.type.physics.leaveGround(t, new Vec(0, 200));
//				} else {
//					t.type.ani.setAnimation(t, "sit");
//				}
//			}
//		}
		return true;
	}
}
