package world.worldGeneration;

import util.math.Vec;
import world.Material;
import world.worldGeneration.WorldData.Vertex;
import world.worldGeneration.objects.ai.ThingType;

public enum Biome {
	DESERT(new Stratum[]{
			null,
			new Stratum(0, Material.SAND, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			null,
			new Stratum(1, Material.SANDSTONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, f, p) -> ThingType.PYRAMID.create(w, f, p.copy()), 0.03),
			new ThingSpawner((w, f, p) -> ThingType.CACTUS.create(w, f, p.copy()), 0.05)),
	CANDY(new Stratum[]{
			null,
			new Stratum(0, Material.CANDY, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(0, Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(1, Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, f, p) -> ThingType.PYRAMID.create(w, f, p.copy()), 0.03),
			new ThingSpawner((w, f, p) -> ThingType.CACTUS.create(w, f, p.copy()), 0.05)),
	GRAVEYARD(new Stratum[]{
			null,
			new Stratum(0, Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(0, Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(1, Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, f, p) -> ThingType.PYRAMID.create(w, f, p.copy()), 0.03),
			new ThingSpawner((w, f, p) -> ThingType.CACTUS.create(w, f, p.copy()), 0.05)),
	MEADOW(new Stratum[]{
			null,
			new Stratum(0, Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(0, Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(1, Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, f, p) -> ThingType.PYRAMID.create(w, f, p.copy()), 0.03),
			new ThingSpawner((w, f, p) -> ThingType.CACTUS.create(w, f, p.copy()), 0.05)),
	FIR_FORREST(new Stratum[]{
			null,
			new Stratum(0, Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(0, Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(1, Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, f, p) -> ThingType.PYRAMID.create(w, f, p.copy()), 0.03),
			new ThingSpawner((w, f, p) -> ThingType.CACTUS.create(w, f, p.copy()), 0.05)),
	NORMAL(new Stratum[]{
			null,
			new Stratum(0, Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(0, Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(1, Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, f, p) -> ThingType.PYRAMID.create(w, f, p.copy()), 0.03),
			new ThingSpawner((w, f, p) -> ThingType.CACTUS.create(w, f, p.copy()), 0.05));
	
	ThingSpawner[] spawners;
	Stratum[] stratums;
	
	Biome(Stratum[] stratums, ThingSpawner... spawners){
		this.stratums = stratums;
		this.spawners = spawners;
	}

	public static class ThingSpawner {
		
		Spawner spawner;
		double probabilityOnFieldWidth;
		
		public ThingSpawner(Spawner spawner, double probabilityOnFieldWidth){
			this.spawner = spawner;
			this.probabilityOnFieldWidth = probabilityOnFieldWidth;
		}

		public interface Spawner { public void spawn(World world, Vertex field, Vec pos); }
		
	}
}
