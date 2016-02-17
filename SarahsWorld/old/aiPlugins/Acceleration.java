package world.things.aiPlugins;

import util.math.Vec;
import world.things.AiPlugin;
import world.things.Thing;


public class Acceleration extends AiPlugin {
	
	public static final String recon = "Acc";

	public Acceleration(Thing thing){
		super(thing);
		super.recon = recon;
	}
	
	public class Acc extends AiPart {
		
		public Vec a = new Vec();
		
		public boolean action(double delta) {
			if(!t.ground.g){
				t.vel.v.shift(a, delta);
			}
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
}