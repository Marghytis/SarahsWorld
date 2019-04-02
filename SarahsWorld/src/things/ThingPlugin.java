package things;

import base.entities.Attribute;

public abstract class ThingPlugin implements Attribute {

	protected Thing thing;
	
	public ThingPlugin(Entity thing) {
		this.thing = (Thing)thing;
	}
	
	public Thing getThing() {
		return thing;
	}
	
//	/**
//	 * This has to be called by client code, e.g. the update method in Species.
//	 * Enables logical connections between the plugins.
//	 * Executes the specific action of the plugin (e.g. Following follows)
//	 * This is also used to make it possible to update each plugin in a for loop (without logical connections)
//	 * @param t
//	 * @param delta
//	 * @return if it succeeded
//	 */
//	public boolean action(double delta){return false;};
	
	public void remove() {}
}
