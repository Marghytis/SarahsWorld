package things.aiPlugins;

import things.AiPlugin;
import things.Thing;
import world.World;

public class FlyAround extends AiPlugin<Thing> {

	public boolean action(Thing t, double delta) {
//		if(t.where != Where.GROUND){
//			System.out.println("Flap! " + t.ani.ani.name);
			if(t.ani.ani.name != "flap"){//gliding
				if(World.rand.nextInt(100) < 20){//start flapping
					t.aniPlug.setAnimation("flap", () -> t.aniPlug.setAnimation("fly"));
					t.flyForce.set((0.5f - World.rand.nextFloat())*100, World.rand.nextFloat()*100 + 50);
				}
			} else {//flapping
				if(t.ani.pos > 1)
				t.type.physics.applyForce(t, t.flyForce);
			}
			if(t.vel.x > 0){
				t.dir = true;
			}
			if(t.vel.x < 0){
				t.dir = false;
			}
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
