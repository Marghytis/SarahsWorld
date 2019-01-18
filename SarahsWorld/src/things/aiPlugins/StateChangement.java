package things.aiPlugins;

import java.util.function.BiConsumer;

import things.AiPlugin;
import things.Thing;

public class StateChangement extends AiPlugin<Thing> {

	BiConsumer<Thing, Double> action;
	
	public StateChangement(BiConsumer<Thing, Double> action) {
		this.action = action;
	}
	
	public void update(Thing t, double delta) {
		action.accept(t, delta);
	}
	
}
