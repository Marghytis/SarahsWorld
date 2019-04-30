package extra.things.traits;

import basis.entities.Trait;
import extra.things.ThingAttribute;
import basis.entities.Entity;
import world.World;

public class WalkAround extends Trait {
	
	@Override
	public WalkAroundPugin createAttribute(Entity thing) {
		return new WalkAroundPugin(thing);
	}
	
	public class WalkAroundPugin extends ThingAttribute {
		
		private double waitTime;//for the walk around plugin

		private double xDestMin, xDestMax;//for walking around

		public WalkAroundPugin(Entity thing) {
			super(thing);
			setup();
		}
		
		public boolean walkAround(double delta) {
			if(thing.physicsPlug.onGround()) {
				int acc = 0;
				if(waitTime > 0){
					waitTime -= delta;
				} else if(thing.pos.x < xDestMin){
					acc++;
				} else if(thing.pos.x > xDestMax){
					acc--;
				} else {
					if(World.rand.nextBoolean()){
						waitTime = World.rand.nextInt(9)+1;
					} else {
						setup();
					}
				}
				thing.movementPlug.setAni( acc);
				thing.physicsPlug.setWalkingForce( acc*thing.movementPlug.accWalking());
				thing.physicsPlug.setMaxWalkingSpeed(thing.movementPlug.accWalking()/5);
				return true;
			}
			return false;
		}
		
		private void setup() {
			xDestMin = thing.pos.x + World.rand.nextInt(500) - 250;
			xDestMax = xDestMin + 50;
		}
		
	}

	public String save() {
		return "";
	}

	public void load(String save) {
	}

}
