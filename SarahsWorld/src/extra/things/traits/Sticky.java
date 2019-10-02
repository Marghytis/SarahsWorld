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

		public boolean action(double delta){
			this.thing.pos.set(thingToStickTo.pos);
			return true;
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
