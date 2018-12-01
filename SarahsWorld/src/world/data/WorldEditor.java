package world.data;

import things.Thing;
import world.window.ThingWindow;

public class WorldEditor {

	WorldData data;
	
	public WorldEditor(WorldData data) {
		this.data = data;
	}

	public void delete(Thing t) {
		t.link.remove(t);
		t.remove();
	}

	public void reLink(ThingWindow range) {
		
		range.relinkThings();
	}
}
