package things.aiPlugins;

import effects.Effect;
import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;

public abstract class Attachement extends AiPlugin2 {

	public abstract void onVisibilityChange(Thing t, boolean visible);

	@Override
	public AttachementPlugin createAttribute(Entity thing) {
		return new AttachementPlugin(thing);
	}
	
	public class AttachementPlugin extends ThingPlugin {
		
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
