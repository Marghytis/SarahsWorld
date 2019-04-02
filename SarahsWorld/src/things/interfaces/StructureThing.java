package things.interfaces;

import moveToLWJGLCore.ListElement;
import things.Species;

public interface StructureThing<T extends ListElement<T>> extends ListElement<T> {

	public void free();
	public Species type();
	public void setLinked(boolean linked);
}
