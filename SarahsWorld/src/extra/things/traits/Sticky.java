package extra.things.traits;

import basis.entities.Entity;
import basis.entities.Trait;
import extra.things.ThingAttribute;

public class Sticky extends Trait {

	@Override
	public StickPlugin createAttribute(Entity thing) {
		return new StickPlugin(thing);
	}
	
	public class StickPlugin extends ThingAttribute {
		
		private Entity thingToStickTo;

		public StickPlugin(Entity thing) {
			super(thing);
		}

		@Override
		public void update(double delta){
			if(thingToStickTo != null) {
				if(thingToStickTo.hasBeenRemoved()) {
					removeFromThing();
				} else {
					this.thing.pos.set(thingToStickTo.pos);
				}
			}
		}

		public void stickTo(Entity thing) {
			thingToStickTo = thing;
			this.thing.physicsPlug.disable();
		}
		
		public void removeFromThing() {
			thingToStickTo = null;
			this.thing.physicsPlug.enable();
		}
	}
	
}
