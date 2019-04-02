package things.aiPlugins;

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
		
		public AttachementPlugin(Entity thing) {
			super(thing);
		}

		public void onVisibilityChange(boolean visible) {
			Attachement.this.onVisibilityChange(thing, visible);
		}
	}
}
