package things;

public abstract class AiPlugin2 extends AiPlugin {

	public abstract ThingPlugin plugIntoThing(Thing t);

	/**
	 * @deprecated better use plugIntoThing(Thing t)
	 */
	@Deprecated
	@Override
	public void setup(Thing t){}

	/**
	 * @deprecated only update things in the ThingPlugin
	 */
	@Deprecated
	@Override
	public void update(Thing t, double delta){}
}
