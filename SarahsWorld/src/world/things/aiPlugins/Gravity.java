package world.things.aiPlugins;

import util.math.Vec;
import world.things.AiPlugin;
import world.things.Thing;

public class Gravity extends AiPlugin{

	public Vec grav = new Vec(0, -981);
	
	/**
	 * Accelerates the thing towards the ground
	 * @param acc
	 * @param stand
	 */
	public Gravity(Thing t){
		super(t);
	}

	public boolean action(double delta) {
		t.acc.a.shift(grav);
		return false;
	}

	public String save() {return "";}
	public void load(String data){}
}
