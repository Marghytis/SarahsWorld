package things.aiPlugins;

import things.AiPlugin;
import things.Thing;
import world.World;

public class WalkAround extends AiPlugin<Thing> {
	
	public void setup(Thing t){
		t.xDestMin = t.pos.x + World.rand.nextInt(500) - 250;
		t.xDestMax = t.xDestMin + 50;
	}

	public boolean action(Thing t, double delta) {
		if(t.where.g) {
			int acc = 0;
			if(t.waitTime > 0){
				t.waitTime -= delta;
			} else if(t.pos.x < t.xDestMin){
				acc++;
			} else if(t.pos.x > t.xDestMax){
				acc--;
			} else {
				if(World.rand.nextBoolean()){
					t.waitTime = World.rand.nextInt(9)+1;
				} else {
					setup(t);
				}
			}
			t.type.movement.setAni(t, acc);
			t.walkingForce = acc*t.accWalking;
			t.maxWalkingSpeed = t.accWalking/5;
		}
		return false;
	}

	public String save() {
		return "";
	}

	public void load(String save) {
	}

}
