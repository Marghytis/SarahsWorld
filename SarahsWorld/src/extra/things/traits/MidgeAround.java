package extra.things.traits;

import basis.entities.Trait;
import extra.things.ThingAttribute;
import basis.entities.Entity;
import util.math.Vec;
import world.World;

public class MidgeAround extends Trait {
	
	@Override
	public MidgePlugin createAttribute(Entity thing) {
		return new MidgePlugin(thing);
	}
	
	public class MidgePlugin extends ThingAttribute {
		
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
			if(thing.physicsPlug.onGround() && World.rand.nextInt(100)==0){
				thing.pos.y++;
				thing.physicsPlug.setNotOnGround();
				flyForce.y = 3;
			}
			thing.physicsPlug.applyForce(flyForce);//.shift((0.5f - World.rand.nextDouble())*3f, (0.49f - World.rand.nextDouble())*3f);
			return true;
		}
	}
}
