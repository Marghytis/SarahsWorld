package things.interfaces;

import things.ThingType;
import world.data.ListElement;

public interface StructureThing<T extends ListElement<T>> extends ListElement<T> {

	public void free();
	public ThingType type();
	public void setLinked(boolean linked);
}
