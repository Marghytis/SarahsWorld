package things;

public abstract class ThingPlugin {

	protected Thing thing;
	
	public ThingPlugin(Thing thing) {
		this.thing = thing;
	}
	
	public Thing getThing() {
		return thing;
	}

	/**
	 * You may NOT move things from one column to another (see coins)
	 * @param t
	 * @param delta
	 */
	public void update(double delta){}
	
	/**
	 * that executes the specific action of the plugin (e.g. Following follows)
	 * This is also used to make it possible to update each plugin in a for loop (without logical connections)
	 * @param t
	 * @param delta
	 * @return if it succeeded
	 */
	public boolean action(double delta){return false;};
	
	public void remove() {}
}
