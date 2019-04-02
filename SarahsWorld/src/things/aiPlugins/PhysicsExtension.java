package things.aiPlugins;

import main.Main;
import things.AiPlugin2;
import things.Entity;
import things.ThingPlugin;
import things.ThingType;
import util.math.Vec;

public class PhysicsExtension extends AiPlugin2 {

	static double forceBase = 1;
	static double friction = 10;
	
	public ThingType[] typesToRepell;
	double radius;
	double forceFactor = 1;
	
	/**
	 * Add typesToRepell later!!!
	 */
	public PhysicsExtension(double radius){
		this.radius = radius;
	}
	
	@Override
	public PhysExPlugin createAttribute(Entity thing) {
		return new PhysExPlugin(thing);
	}
	
	public class PhysExPlugin extends ThingPlugin {

		public PhysExPlugin(Entity thing) {
			super(thing);
		}
		
		@Override
		public void update(double delta){
			for(ThingType tt : typesToRepell){
				Main.world.thingWindow.forEach(tt.ordinal, (t) -> {
					Vec conn = t.pos.minus(thing.pos);
					double action = tt.physEx.radius + radius;
					double distSq = conn.lengthSquare();
					if(distSq != 0 && distSq < action*action){
						double dist = Math.sqrt(distSq);
						conn.scale(1/dist);
						double force = 0;
						if(dist < action*0.9){
							force = 100 - (dist/action)*50;
						} else {
							force = (action - dist)*(action - dist)*100;
						}
						thing.force.shift(conn, -force);
					}
				});
			}
		}
		
	}
	
}
