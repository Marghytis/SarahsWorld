package base.entities;

import base.entities.entity.Entity;

/**
 * A Trait is a property of a Species that doesn't change over time and for different Entities.
 * @author Mario
 *
 */
public interface Trait {

	/**
	 * A Trait appears in an Entity as an Attribute.
	 * @return A new Attribute instance to be added to an Entity.
	 */
	public Attribute createAttribute(Entity entity);
}
