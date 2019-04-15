package base.entities.entity;

import base.entities.Attribute;
import base.entities.Species;
import base.entities.Trait;
import transition.ThingEntity;
import util.math.Vec;
import world.data.Column;

/**
 * Represents an object in the world at a specific location. It is defined by a set of Attributes.
 * Keep this in a separate package, so the protected fields can only be accessed by extending classes!
 * @author Mario
 *
 */
public class Entity implements ThingEntity {
	
	//states of realization
	static final int VIRTUAL = -1, REAL = 0;
	
	private int stateOfRealization = VIRTUAL;
	private Species species;
	private Vec pos;
	private Column realLink, newLink;
	private Attribute[] attributes;
	
	public Entity(Species species, Column link, Vec pos, Object... extraData) {
		this.pos = pos;
		
		//add all the attributes and initialize
		becomeSpecies(species);
		species.prepare(this, extraData);
		
		//place the entity in the world
		newLink = link;
		applyLink();
	}
	
	private void becomeSpecies(Species species) {
		this.species = species;
		//Create attributes from the species' traits.
		Trait[] traits = species.getTraits();
		this.attributes = new Attribute[traits.length];
		for(int i = 0; i < traits.length; i++) {
			Attribute attrib = traits[i].createAttribute(this);
			attributes[i] = attrib;
			onAttributeAdded(attrib);
		}
	}

	public void applyLink() {
		if(realLink != newLink || stateOfRealization == VIRTUAL) {
			if(realLink != null)
				realLink.remove(this);
			newLink.addCoins(this);
			stateOfRealization = REAL;
			realLink = newLink;
		}
	}
	
	/**
	 * This method is called when a new Attribute is added to this Entity. In Entity.class it's empty, so be welcome to override it.
	 * @param attrib The attribute that was added.
	 */
	protected void onAttributeAdded(Attribute attrib) {}

	
//Getters
	public Species	species() {				return species; }
	public Vec		pos() {					return pos; }
	public Column	realLink() {			return realLink; }
	
//Setters
	public void		setPos(Vec newPos) {	pos.set(newPos); }
	
}
