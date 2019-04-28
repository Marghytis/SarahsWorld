package things.aiPlugins;

import base.entities.Attribute;
import things.AiPlugin2;
import things.Entity;
import things.OnInteraction;
import things.Thing;
import things.ThingPlugin;
import util.math.Vec;

public class Interaction extends AiPlugin2 {
	
	private OnInteraction onInteraction;
	
	public Interaction(OnInteraction onInteraction) {
		this.onInteraction = onInteraction;
	}

	@Override
	public Attribute createAttribute(Entity entity) {
		return new InteractionPlugin(entity);
	}
	
	public class InteractionPlugin extends ThingPlugin {
		
		private InteractionPlugin(Entity entity) {
			super(entity);
		}
		
		public void onInteraction(Thing source, Vec worldPos) {
			onInteraction.run(source, worldPos, thing);
		}
	}

}
