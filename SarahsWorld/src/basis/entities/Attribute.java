package basis.entities;

public interface Attribute {
	
	/**
	 * This method is called every update cycle by the Entity class.
	 * You may NOT move things from one column to another (see coins), only modifiy the newLink variable.
	 * The things are moved to their new column at the end of the update.
	 * @param delta
	 */
	public default void update(double delta){}
	
	/**
	 * Put initialization code in here that uses other Attributes of an Entity.
	 * This method is called directly after all attributes have been attributed.
	 */
	public default void finishInitialization() {}
	
	/**
	 * Is called upon removal of the entity this Attribute belongs to.
	 * Use it to destroy objects and free storage.
	 */
	public default void remove() {}
}
