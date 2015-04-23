package world.objects.ai;

import world.Material;
import world.objects.Thing;

public class FlyAround extends AiPlugin{

	public FlyAround(Thing thing) {
		super(thing);
	}

	public boolean action(double delta) {
		if(!t.ground.g){
			t.acc.a.shift((0.5f - t.rand.nextFloat())*2000f, (0.49f - t.rand.nextFloat())*2000);
			t.acc.a.shift(-Material.AIR.deceleration*t.vel.v.x*delta, -Material.AIR.deceleration*t.vel.v.y*delta);
			if(t.vel.v.x > 0){
				t.ani.dir = true;
			}
			if(t.vel.v.x < 0){
				t.ani.dir = false;
			}
		} else {
			if(t.rand.nextInt(100) == 0){
				t.ground.leaveGround(0, 0.01);
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
