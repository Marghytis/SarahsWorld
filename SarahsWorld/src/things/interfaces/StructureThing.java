package things.interfaces;

import moveToLWJGLCore.ListElement;
import things.ThingType;

public interface StructureThing<T extends ListElement<T>> extends ListElement<T> {

	public void free();
	public ThingType type();
	public void setLinked(boolean linked);
}
