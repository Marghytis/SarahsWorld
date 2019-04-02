package base.entities;

import base.entities.entity.Entity;

public class Species {

	String name;
	protected Trait[] traits;
	
	public Species(Trait... traits) {
		this.traits = traits;
	}
	
	public Trait[] getTraits() {
		return traits;
	}
	
	/**
	 * Prepares the given entity for the world after it has been initialized.
	 * @param entity Should be of the right species.
	 * @param extraData
	 */
	public void prepare(Entity entity, Object... extraData) {}
}
