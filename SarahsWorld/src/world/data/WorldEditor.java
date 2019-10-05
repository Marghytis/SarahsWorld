package world.data;

import basis.entities.Entity;
import extra.things.Thing;
import util.math.Vec;
import world.World;
import world.window.ThingWindow;

public class WorldEditor {

	WorldData data;
	
	public WorldEditor(WorldData data) {
		this.data = data;
	}
	
	public void makeCrater(Vec pos, double radius) {
		Vec startEnd = new Vec(pos.x, pos.x);
		data.world.landscapeWindow.forEachColumn(column -> {
			double xDist = column.getX() - pos.x;
			if(xDist < radius) {
				double y = pos.y - Math.sqrt(radius*radius - (xDist*xDist));
				double dy = y - column.getTopSolidVertex().getY();
				if(dy < 0) {
					if(column.getX() < startEnd.x) startEnd.x = column.getX();
					if(column.getX() > startEnd.y) startEnd.y = column.getX();
					
					column.shiftColumnTop(dy + (20*World.rand.nextDouble()));
				}
			}
		});
		data.world.landscapeWindow.reload(startEnd.x, startEnd.y);
		data.world.getBackgroundWindow().reload(startEnd.x, startEnd.y);
		
		Thing[] thingsIn = data.world.thingWindow.thingsIn(pos, radius);
		
		for(Thing t : thingsIn) {
			delete(t);
		}
	}
	
//	/**
//	 * Constructs this thing in the world and sets it up.
//	 * @param t
//	 */
//	public void realize(Thing t) {
//		if(t.real) {
//			//I could destroy the thing and then realize it again here, but that's overkill, this function shouldn't be used for this.
//			throw new RuntimeException("Thing is already spawned.");
//		} else if(t.newLink == null){
//			throw new RuntimeException("Can't give birth to a Thing without a link!");
//		} else {
//			t.setup();
//			t.real = true;
//		}
//	}

	/**
	 * Removes this thing from the world and finalizes it.
	 * @param t
	 */
	public void delete(Entity t) {
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
