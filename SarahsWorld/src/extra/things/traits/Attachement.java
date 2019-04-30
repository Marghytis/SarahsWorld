package extra.things.traits;

import basis.entities.Trait;
import basis.entities.Entity;
import effects.Effect;
import extra.things.Thing;
import extra.things.ThingAttribute;

public abstract class Attachement extends Trait {

	public abstract void onVisibilityChange(Thing t, boolean visible);

	@Override
	public AttachementPlugin createAttribute(Entity thing) {
		return new AttachementPlugin(thing);
	}
	
	public class AttachementPlugin extends ThingAttribute {
		
		private Effect effect;
		private int effectTicket;
		private boolean active = false;
		
		public AttachementPlugin(Entity thing) {
			super(thing);
		}

		public void setEffect(Effect effect) {			this.effect = effect; }
		public void setEffectTicket(int ticket) {			this.effectTicket = ticket; }
		public void setActive(boolean active) {			this.active = active; }

		public Effect getEffect() {						return effect; }
		public int getEffectTicket() {						return effectTicket; }
		public boolean active() {						return active; }

		public void onVisibilityChange(boolean visible) {
			Attachement.this.onVisibilityChange(thing, visible);
		}
	}
}
