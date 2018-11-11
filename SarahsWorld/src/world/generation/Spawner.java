package world.generation;

import things.Thing;
import util.math.Vec;
import world.data.Column;

public interface Spawner {
	public Thing spawn(Column field, Vec pos, Object... extraData);
}