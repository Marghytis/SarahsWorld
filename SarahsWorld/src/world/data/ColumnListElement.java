package world.data;

import things.Thing;
import things.ThingType;

public class ColumnListElement extends DirListElement<ColumnListElement>{

	private Column column;

	public ColumnListElement(Column column) {
		this.column = column;
	}
	
	public Column column() {
		return column;
	}
	
	public int getIndex() {
		return column.getIndex();
	}
	
	public Vertex vertices(int index) {
		return column.vertices(index);
	}
	
	public Thing firstThing(int type) {
		return column.firstThing(type);
	}
	
	public Thing firstThing(ThingType type) {
		return column.firstThing(type);
	}
}
