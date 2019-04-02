package extra.things;

import base.entities.Attribute;
import base.entities.Species;
import base.entities.entity.Entity;
import extra.things.attributes.AnimatedTrait.AnimatedAttribute;
import util.math.Vec;
import world.data.Column;

public class ExplicitEntity extends Entity {

	AnimatedAttribute ani;

	public ExplicitEntity(Species species, Column link, Vec pos, Object[] extraData) {
		super(species, link, pos, extraData);
	}

	/**
	 * Assigns the corresponding explicit Attribute variable of this Entity to the given Attribute.
	 * @param attrib
	 */
	private void nameAttribute(Attribute attrib) {
		
		if(attrib instanceof AnimatedAttribute) {
			ani = (AnimatedAttribute) attrib;
		}
	}

	@Override
	protected void onAttributeAdded(Attribute attrib) {
		
		nameAttribute(attrib);
	}

}
