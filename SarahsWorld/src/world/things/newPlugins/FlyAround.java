package world.things.newPlugins;

import util.math.Vec;
import world.World;
import world.things.AiPlugin;
import world.things.ThingProps;
import world.things.newPlugins.Physics.Where;

public class FlyAround extends AiPlugin{

	public boolean action(ThingProps t, double delta) {
		if(t.where != Where.GROUND){
			if(t.ani.ani != t.type.ani.get(t, "flap")){//gliding
				if(World.rand.nextInt(100) < 1){//start flapping
					t.type.ani.setAnimation(t, "flap", () -> t.type.ani.setAnimation(t, "fly"));
					t.flyForce.set((0.5f - World.rand.nextFloat())*2000f, (0.49f - World.rand.nextFloat())*2000);
				}
			} else {//flapping
				t.type.physics.applyForce(t, t.flyForce);
			}
			if(t.vel.x > 0){
				t.dir = true;
			}
			if(t.vel.x < 0){
				t.dir = false;
			}
		} else {
			if(t.ani.ani != t.type.ani.get(t, "flap")){

				if(World.rand.nextInt(100) == 0){
					t.type.ani.setAnimation(t, "flap");
				} else if(World.rand.nextInt(100) == 0){
					t.type.physics.leaveGround(t, new Vec(0, 0.01));
				} else {
					t.type.ani.setAnimation(t, "sit");
				}
			}
		}
		return true;
	}
}
