package things.aiPlugins;

import things.AiPlugin2;
import things.Entity;
import things.ThingPlugin;
import util.math.Vec;
import world.World;

public class MidgeAround extends AiPlugin2 {
	
	@Override
	public MidgePlugin createAttribute(Entity thing) {
		return new MidgePlugin(thing);
	}
	
	public class MidgePlugin extends ThingPlugin {
		
		private double otherCooldown;
		private Vec flyForce;
		
		public MidgePlugin(Entity thing) {
			super(thing);
			flyForce = new Vec();
		}

		public boolean action(double delta) {
			otherCooldown -= delta;
			if(otherCooldown < 0){
				otherCooldown = 1 + World.rand.nextDouble()*1;
				//TODO the random walk below might walk away... i.e. don't allow arbitrarily high force.
				flyForce.shift((0.50 - World.rand.nextDouble())*3, (0.40 - World.rand.nextDouble())*3);
			}
			if(thing.where.g && World.rand.nextInt(100)==0){
				thing.pos.y++;
				thing.where.g = false;
				flyForce.y = 3;
			}
			thing.force.set(flyForce);//.shift((0.5f - World.rand.nextDouble())*3f, (0.49f - World.rand.nextDouble())*3f);
			return true;
		}
	}
}
