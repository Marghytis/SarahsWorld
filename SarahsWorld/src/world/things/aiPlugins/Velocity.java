package world.things.aiPlugins;

import util.math.Vec;
import world.things.AiPlugin;
import world.things.Thing;
public class Velocity extends AiPlugin{

	public Vec v;
	
	public Velocity(Thing t){
		super(t);
		v = new Vec();
	}
	
	public void partRender(){
		
	}
	
	public boolean action(double delta) {
		
		t.pos.p.shift(v, delta);
		return false;
	}

	public String save() {
		return v.x + s + v.y;
	}

	public void load(String data){
		String[] infos = data.split(s);
		v.x = Double.parseDouble(infos[0]);
		v.y = Double.parseDouble(infos[1]);
	}
}