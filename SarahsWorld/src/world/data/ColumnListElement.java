package world.data;

import basis.entities.Entity;
import basis.entities.Species;
import moveToLWJGLCore.DirListElement;

/**
 * An abstract specification of an element in a directed list of Columns. Is implemented fully in Column.
 * @author Mario
 *
 */
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
	public abstract Entity firstThing(int ordinal);
	public abstract <T extends Entity> T firstThing(Species<T> coin);
}
