package world.worldGeneration.objects.ai;

import util.math.Vec;

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
		if(!t.ground.g)t.acc.a.shift(grav);
		return false;
	}

	public String save() {return "";}
	public void load(String data){}
}
