package things.aiPlugins;

import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;

public class StateChangement extends AiPlugin2 {

	public static interface TriConsumer<S1, S2, S3> {
		public void accept(S1 s1, S2 s2, S3 s3);
	}
	
	TriConsumer<Thing, Double, double[]> action;
	int nVariables;

	public StateChangement(TriConsumer<Thing, Double, double[]> action) {
		this(0, action);
	}
	public StateChangement(int nVariables, TriConsumer<Thing, Double, double[]> action) {
		this.action = action;
		this.nVariables = nVariables;
	}
	
	@Override
	public StatePlugin createAttribute(Entity thing) {
		return new StatePlugin(thing);
	}
	
	public class StatePlugin extends ThingPlugin {
		
		private double[] stateVariables;

		public StatePlugin(Entity thing) {
			super(thing);
			this.stateVariables = new double[nVariables];
		}
		
		@Override
		public void update(double delta) {
			action.accept(thing, delta, stateVariables);
		}
		
	}
	
}
