package world.data;

import things.Thing;
import world.render.ThingWindow;

public class WorldEditor {

	WorldData data;
	
	public WorldEditor(WorldData data) {
		this.data = data;
	}

	public void delete(Thing t) {
		t.disconnectFrom(t.link);
		t.remove();
	}

	public void reLink(ThingWindow range) {
		
		range.forEach(Thing::applyLink);
	}
}
