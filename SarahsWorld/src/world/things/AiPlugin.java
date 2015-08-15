package world.things;



public abstract class AiPlugin {
	
	public String recon;
	
	public static final String s = ",";
	
	public String name;
	
	public void update(ThingProps t, double delta){}
	
	/**
	 * that executes the specific action of the plugin (e.g. Following follows)
	 * This is also used to make it possible to update each plugin in a for loop (without logical connections)
	 * @param t
	 * @param delta
	 * @return if it succeeded
	 */
	public boolean action(ThingProps t, double delta){return false;};
	
	public void setup(ThingProps t){};
	
	public void partRender(ThingProps t){}

	public void remove(ThingProps t) {}
}
