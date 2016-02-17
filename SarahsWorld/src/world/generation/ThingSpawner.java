package world.generation;

import things.Thing;
import util.math.Vec;
import world.WorldData;
import world.WorldData.Column;

public class ThingSpawner {
	
	Spawner spawner;
	double probabilityOnFieldWidth;
	
	public ThingSpawner(Spawner spawner, double probabilityOnFieldWidth){
		this.spawner = spawner;
		this.probabilityOnFieldWidth = probabilityOnFieldWidth;
	}
	
	public interface Spawner { public Thing spawn(WorldData world, Column field, Vec pos, Object... extraData); }
}