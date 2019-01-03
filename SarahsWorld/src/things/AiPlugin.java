package things;

import things.interfaces.BasicThing;

public abstract class AiPlugin<T extends BasicThing> {
	
	public String recon;
	
	public static final String s = ",";
	
	public String name;
	
	/**
	 * You may NOT move things from one column to another (see coins)
	 * @param t
	 * @param delta
	 */
	public void update(T t, double delta){}
	
	/**
	 * that executes the specific action of the plugin (e.g. Following follows)
	 * This is also used to make it possible to update each plugin in a for loop (without logical connections)
	 * @param t
	 * @param delta
	 * @return if it succeeded
	 */
	public boolean action(T t, double delta){return false;};
	
	public void setup(T t){};
	
	public void remove(T t) {}
	
}
