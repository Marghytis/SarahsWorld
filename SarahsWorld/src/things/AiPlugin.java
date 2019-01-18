package things;

import things.interfaces.BasicThing;
import util.math.Vec;

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
	
	public class BasicPlugin extends ThingPlugin {
		
		Vec pos;
		double rotation;
		double size;
		double yOffset;
		double yOffsetToBalanceRotation;

		public BasicPlugin(Thing thing) {
			super(thing);
			pos = new Vec();
		}

		public void setRotation(double angle) {
			rotation = angle;
		}

		public ThingType getType() {
			return thing.getType();
		}

		public double getSize() {
			return size;
		}
		
		public double getRotation() {
			return rotation;
		}

		public Vec pos() {
			return pos;
		}

		public double getYOffset() {
			return yOffset;
		}

		public double getYOffsetToBalanceRotation() {
			return yOffsetToBalanceRotation;
		}
		
		
	}
	
}
