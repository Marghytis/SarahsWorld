package things.sorting;

import things.Thing;
import things.ThingPlugin;

public class ThingPluginTest extends ThingPlugin implements ThingAspect<ThingPluginTest> {

	public ThingPluginTest(Thing thing) {
		super(thing);
	}

	@Override
	public ThingPluginTest next() {
		return null;
	}

	public void onVisibilityChange(boolean b) {
		// TODO Auto-generated method stub
		
	}

}
