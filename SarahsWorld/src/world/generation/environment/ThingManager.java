package world.generation.environment;

import java.util.ArrayList;
import java.util.List;

import world.data.Column;
import world.generation.Biome.ThingSpawner;

public class ThingManager {
	
	List<ThingSpawner> spawners = new ArrayList<>();
	
	public void populate(Column column) {
		column.getBiome().spawnThings(column);
		spawners.forEach(s -> column.getBiome().applySpawner(column, s));
		spawners.clear();
	}

	public void add(ThingSpawner ts) {
		spawners.add(ts);
	}

	public void add(ThingSpawner... tss) {
		for(ThingSpawner ts : tss) {
			spawners.add(ts);
		}
	}
	
	public void step(){
		
	}

}
