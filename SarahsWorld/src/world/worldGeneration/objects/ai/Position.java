package world.worldGeneration.objects.ai;

import util.math.Vec;

public class Position extends AiPlugin {

	public Vec p;
	
	public Position(Thing t, Vec pos){
		super(t);
		this.p = pos;
	}
	
	public boolean action(double delta) {
		return false;
	}

	public String save() {
		return p.x + s + p.y;
	}

	public void load(String data) {
		String[] coords = data.split(s);
		p.x = Double.parseDouble(coords[0]);
		p.y = Double.parseDouble(coords[1]);
	}

}
