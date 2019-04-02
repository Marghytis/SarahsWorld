package extra.things.attributes;

import base.entities.Attribute;
import base.entities.Trait;
import base.entities.entity.Entity;

public class AnimatedTrait implements Trait {

	@Override
	public Attribute createAttribute(Entity entity) {
		
		return new AnimatedAttribute();
	}

	public class AnimatedAttribute implements Attribute {
		
		private AnimatedAttribute() {
			
		}
	}
}
