package things.aiPlugins;

import main.Main;
import things.AiPlugin;
import things.Thing;
import things.ThingType;
import util.math.Vec;

public class PhysicsExtension extends AiPlugin {

	static double forceBase = 1000;
	static double friction = 10;
	
	public ThingType[] typesToRepell;
	double radius;
	double forceFactor;
	
	/**
	 * Add typesToRepell later!!!
	 */
	public PhysicsExtension(){
	}
	
	public void update(Thing t1, double delta){
		for(ThingType tt : typesToRepell){
			Main.world.window.forEach(tt, (t) -> {
				Vec conn = t.pos.minus(t1.pos);
				if(conn.lengthSquare() < (tt.physEx.radius + radius)*(tt.physEx.radius + radius)){
					t.force.shift(conn, forceBase*forceFactor*tt.physEx.forceFactor);
					t.force.shift(t.vel, -10);
				}
			});
		}
	}
	
}
