package things.aiPlugins;

import java.util.function.BiConsumer;

import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;

public class StateChangement extends AiPlugin2 {

	BiConsumer<Thing, Double> action;
	
	public StateChangement(BiConsumer<Thing, Double> action) {
		this.action = action;
	}
	
	@Override
	public StatePlugin createAttribute(Entity thing) {
		return new StatePlugin(thing);
	}
	
	public class StatePlugin extends ThingPlugin {

		public StatePlugin(Entity thing) {
			super(thing);
		}
		
		@Override
		public void update(double delta) {
			action.accept(thing, delta);
		}
		
	}
	
}
