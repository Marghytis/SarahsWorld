package world.data;

import things.Thing;
import things.ThingType;
import world.window.RealWorldWindow;

public class WorldEditor {

	WorldData data;
	
	public WorldEditor(WorldData data) {
		this.data = data;
	}

	public void delete(Thing t) {
		t.disconnectFrom(t.link);
		t.remove();
	}

	public void reLink(RealWorldWindow range) {
		
		for(int type = 0; type < ThingType.types.length; type++)
		for(Column col = range.getEnd(0); col != range.getEnd(1).next(1); col = col.next(1))
		for(Thing t = col.things[type]; t != null; t = t.next)
			if(t.oldLink != t.link)
				t.applyLink();
	}
}
