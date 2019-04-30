package extra.things.traits;

import basis.entities.Attribute;
import basis.entities.Entity;
import basis.entities.InteractionAttribute;
import basis.entities.Trait;
import extra.things.Thing;
import extra.things.ThingAttribute;
import extra.things.traitExtensions.OnInteraction;
import util.math.Vec;

public class Interaction extends Trait {
	
	private OnInteraction onInteraction;
	
	public Interaction(OnInteraction onInteraction) {
		this.onInteraction = onInteraction;
	}

	@Override
	public Attribute createAttribute(Entity entity) {
		return new InteractionPlugin(entity);
	}
	
	public class InteractionPlugin extends ThingAttribute implements InteractionAttribute<Thing> {
		
		private InteractionPlugin(Entity entity) {
			super(entity);
		}
		
		public void onInteraction(Thing source, Vec worldPos) {
			onInteraction.run(source, worldPos, thing);
		}
	}

}
