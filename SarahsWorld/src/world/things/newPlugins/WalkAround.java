package world.things.newPlugins;

import world.World;
import world.things.AiPlugin;
import world.things.ThingProps;
import world.things.newPlugins.Physics.Where;



public class WalkAround extends AiPlugin{

	double waitTime;
	
	public void setup(ThingProps t){
		t.xDestMin = t.pos.x + World.rand.nextInt(500) - 250;
		t.xDestMax = t.xDestMin + 50;
	}

	public boolean action(ThingProps t, double delta) {
		if(t.where == Where.GROUND) {
			int acc = 0;
			if(waitTime > 0){
				waitTime -= delta;
			} else if(t.pos.x < t.xDestMin){
				acc++;
			} else if(t.pos.x > t.xDestMax){
				acc--;
			} else {
				if(World.rand.nextBoolean()){
					waitTime = World.rand.nextInt(10);
				} else {
					setup(t);
				}
			}
			t.type.ground.setAni(t, acc);
			t.walkingForce += acc*t.accWalking;
		}
		return false;
	}

	public String save() {
		return "";
	}

	public void load(String save) {
	}

}
