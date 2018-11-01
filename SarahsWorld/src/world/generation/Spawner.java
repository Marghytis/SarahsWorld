package world.generation;

import things.Thing;
import util.math.Vec;
import world.data.Column;
import world.data.WorldData;

public interface Spawner {
	public Thing spawn(WorldData world, Column field, Vec pos, Object... extraData);
}