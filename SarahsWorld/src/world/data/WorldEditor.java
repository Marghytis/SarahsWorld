package world.data;

import things.Thing;
import world.window.ThingWindow;

public class WorldEditor {

	WorldData data;
	
	public WorldEditor(WorldData data) {
		this.data = data;
	}
	
	/**
	 * Constructs this thing in the world and sets it up.
	 * @param t
	 */
	public void realize(Thing t) {
		if(t.real) {
			//I could destroy the thing and then realize it again here, but that's overkill, this function shouldn't be used for this.
			throw new RuntimeException("Thing is already spawned.");
		} else if(t.newLink == null){
			throw new RuntimeException("Can't give birth to a Thing without a link!");
		} else {
			t.setup();
			t.real = true;
		}
	}

	/**
	 * Removes this thing from the world and finalizes it.
	 * @param t
	 */
	public void delete(Thing t) {
		t.newLink.remove(t);
		t.remove();
		t.real = false;
	}

	/**
	 * Link all things to the column they're at. Needed if they changed position since the last call.
	 * @param range of things to relink
	 */
	public void reLink(ThingWindow range) {
		
		range.relinkThings();
	}
}
