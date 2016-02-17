package world.things.aiPlugins;

import world.things.AiPlugin;
import world.things.Thing;



public class WalkAround extends AiPlugin{

	double acc;
	double xDestMin, xDestMax;
	double waitTime;
	
	public WalkAround(Thing thing, double acc) {
		super(thing);
		this.acc = acc;
		xDestMin = t.pos.x + t.rand.nextInt(500) - 250;
		xDestMax = xDestMin + 20;
	}

	public boolean action(double delta) {
		if(t.ground.g) {
			double acc = 0;
			if(waitTime > 0){
				waitTime -= delta;
			} else if(t.pos.x < xDestMin){
				t.ground.acc += acc;
			} else if(t.pos.x > xDestMax){
				t.ground.acc -= acc;
			} else {
				if(t.rand.nextBoolean()){
					waitTime = t.rand.nextInt(10);
				} else {
					xDestMin = t.pos.x + t.rand.nextInt(500) - 250;
					xDestMax = xDestMin + 20;
				}
			}
			t.ground.setAni(acc);
			t.ground.acc += acc;
		}
		return false;
	}

	public String save() {
		return "";
	}

	public void load(String save) {
	}

}
