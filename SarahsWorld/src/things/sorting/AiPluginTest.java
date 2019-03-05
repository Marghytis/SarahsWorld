package things.sorting;

import things.AiPlugin2;
import things.Thing;
import things.ThingPlugin;

public abstract class AiPluginTest extends AiPlugin2 implements AiAspect<ThingPluginTest>{

	@Override
	public ThingPlugin plugIntoThing(Thing t) {
		return null;
	}

}
