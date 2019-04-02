package things.aiPlugins;

import things.AiPlugin2;
import things.Entity;
import things.ThingPlugin;
import world.World;

public class WalkAround extends AiPlugin2 {
	
	@Override
	public WalkAroundPugin createAttribute(Entity thing) {
		return new WalkAroundPugin(thing);
	}
	
	public class WalkAroundPugin extends ThingPlugin {

		public WalkAroundPugin(Entity thing) {
			super(thing);
			setup();
		}
		
		public boolean walkAround(double delta) {
			if(thing.where.g) {
				int acc = 0;
				if(thing.waitTime > 0){
					thing.waitTime -= delta;
				} else if(thing.pos.x < thing.xDestMin){
					acc++;
				} else if(thing.pos.x > thing.xDestMax){
					acc--;
				} else {
					if(World.rand.nextBoolean()){
						thing.waitTime = World.rand.nextInt(9)+1;
					} else {
						setup();
					}
				}
				thing.type.movement.setAni(thing, acc);
				thing.walkingForce = acc*thing.accWalking;
				thing.maxWalkingSpeed = thing.accWalking/5;
				return true;
			}
			return false;
		}
		
		private void setup() {
			thing.xDestMin = thing.pos.x + World.rand.nextInt(500) - 250;
			thing.xDestMax = thing.xDestMin + 50;
		}
		
	}

	public String save() {
		return "";
	}

	public void load(String save) {
	}

}
