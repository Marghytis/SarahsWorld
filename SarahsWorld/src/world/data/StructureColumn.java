package world.data;

import things.Thing;
import things.ThingType;

public interface StructureColumn {

	public void add(Thing t);
	public void remove(Thing t);	
	public Thing getFirst(ThingType type);
	public Thing firstThing(ThingType type);
	public Thing firstThing(int typeOrdinal);
	
	public int getIndex();
	public void setIndex(int i);
	public double getX();
	public void setX(double x);
}
