package things.aiPlugins;

import things.AiPlugin;
import things.Thing;
import things.ThingPlugin;

public abstract class Attachement extends AiPlugin<Thing> {

	public abstract void onVisibilityChange(Thing t, boolean visible);
	
	public class AttachementPlugin extends ThingPlugin {
		
		public AttachementPlugin(Thing thing) {
			super(thing);
		}

		public void onVisibilityChange(boolean visible) {
			Attachement.this.onVisibilityChange(thing, visible);
		}
	}
}
