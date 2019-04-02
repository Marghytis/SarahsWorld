package things.aiPlugins;

import java.util.function.BiConsumer;

import base.entities.Attribute;
import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;

public class LogicCombination extends AiPlugin2 {
	
	private BiConsumer<Thing, Double> logic;
	
	public LogicCombination(BiConsumer<Thing, Double> logic) {
		this.logic = logic;
	}

	@Override
	public Attribute createAttribute(Entity entity) {
		return new LogicPlugin(entity);
	}
	
	protected void doLogic(Thing t, double delta) {
		logic.accept(t, delta);
	}
	
	public class LogicPlugin extends ThingPlugin {

		public LogicPlugin(Entity thing) {
			super(thing);
		}
		
		public void update(double delta) {
			
			doLogic(thing, delta);
		}
	}

}
