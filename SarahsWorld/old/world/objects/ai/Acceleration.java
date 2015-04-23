package world.objects.ai;

import util.math.Vec;
import world.objects.Thing;


public class Acceleration extends AiPlugin {
	
	public Vec a;
	
	public Acceleration(Thing thing){
		super(thing);
		a = new Vec();
	}
	
	public boolean action(double delta) {
		if(!t.ground.g) t.vel.v.shift(a, delta);
		a.set(0, 0);
		return false;
	}

	public String save() {
		return a.x + s + a.y;
	}

	public void load(String data){
		String[] infos = data.split(s);
		a.x = Double.parseDouble(infos[0]);
		a.y = Double.parseDouble(infos[1]);
	}
}