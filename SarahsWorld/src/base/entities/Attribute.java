package base.entities;

public interface Attribute {
	
	/**
	 * This method is called every update cycle by the Entity class.
	 * You may NOT move things from one column to another (see coins), only modifiy the newLink variable.
	 * The things are moved to their new column at the end of the update.
	 * @param t
	 * @param delta
	 */
	public default void update(double delta){}
}
