package extra.things.traits;

import java.util.function.BiConsumer;

import basis.entities.Trait;
import extra.things.Thing;
import extra.things.ThingAttribute;
import basis.entities.Attribute;
import basis.entities.Entity;

public class LogicCombination extends Trait {
	
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
	
	public class LogicPlugin extends ThingAttribute {

		public LogicPlugin(Entity thing) {
			super(thing);
		}
		
		public void update(double delta) {
			
			doLogic(thing, delta);
		}
	}

}
