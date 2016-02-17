package world.things.aiPlugins;

import util.math.Vec;
import world.things.AiPlugin;
import world.things.Thing;

public class Position extends AiPlugin {
	
	public static final String recon = "Pos";
	
	public Position(Thing t, Vec pos){
		super(t);
		super.recon = recon;
	}
	
	public class Pos extends AiPart {
		public Vec p;
		
		public Pos(Vec pos){
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
}
