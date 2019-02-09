package things;

public abstract class AiPlugin {
	
	public String recon;
	
	public static final String s = ",";
	
	public String name;
	
	/**
	 * You may NOT move things from one column to another (see coins)
	 * @param t
	 * @param delta
	 */
	public void update(Thing t, double delta){}
	
	/**
	 * that executes the specific action of the plugin (e.g. Following follows)
	 * This is also used to make it possible to update each plugin in a for loop (without logical connections)
	 * @param t
	 * @param delta
	 * @return if it succeeded
	 */
	public boolean action(Thing t, double delta){return false;};
	
	public void setup(Thing t){}
	
	public void remove(Thing t) {}
	
}
