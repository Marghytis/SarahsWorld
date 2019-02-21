package world.data;

import things.Thing;
import things.ThingType;

public abstract class ColumnListElement extends DirListElement<Column> {

	public void setLeft(ColumnListElement c) {
		super.setLeft((Column)c);
	}
	public void setRight(ColumnListElement c) {
		super.setRight((Column)c);
	}
	
	public Column column() {
		return (Column) this;
	}

	public abstract void setIndex(int index);
	public abstract int getIndex();
	public abstract Vertex vertices(int yIndex);
	public abstract Thing firstThing(int ordinal);
	public abstract Thing firstThing(ThingType coin);
}
