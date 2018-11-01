package things.aiPlugins;

import things.AiPlugin;
import things.Thing;

public abstract class Attachement extends AiPlugin {

	public abstract void onVisibilityChange(Thing t, boolean visible);
	
}
