package world.generation;

import util.math.Vec;
import world.Material;
import world.Stratum;
import world.WorldData;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.things.ThingProps;
import world.things.ThingType;

public enum Biome {
	DESERT(new Stratum[]{
			null,
			new Stratum(Material.SAND, 60.0, 20.0, 5, 40, 2, 4),
			null,
			null,
			null,
			new Stratum(Material.SANDSTONE, 100000000.0, 200.0, 20, 40, 5, 5),
			null
			},
			
			new ThingSpawner((w, p, f) -> new ThingProps(ThingType.PYRAMID, w, p, f), 0.03),
			new ThingSpawner((w, p, f) -> new ThingProps(ThingType.CACTUS, w,p, f), 0.05)),
	CANDY(new Stratum[]{
			null,
			new Stratum(Material.CANDY, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, p, f) -> new ThingProps(ThingType.PYRAMID, w,p, f), 0.03),
			new ThingSpawner((w, p, f) -> new ThingProps(ThingType.CACTUS, w,p, f), 0.05)),
	GRAVEYARD(new Stratum[]{
			null,
			new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, p, f) -> new ThingProps(ThingType.PYRAMID, w,p, f), 0.03),
			new ThingSpawner((w, p, f) -> new ThingProps(ThingType.CACTUS, w,p, f), 0.05)),
	MEADOW(new Stratum[]{
			null,
			new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			},
			
			new ThingSpawner((w, p, f) -> new ThingProps(ThingType.PYRAMID, w,p, f), 0.03),
			new ThingSpawner((w, p, f) -> new ThingProps(ThingType.CACTUS, w,p, f), 0.05)),
	FIR_FORREST(new Stratum[]{
			null,
			new Stratum(Material.GRASS, 20.0, 20.0, 5, 40, 2, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 5, 4),
			null,
			null,
			new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 5, 5),
			null
			}
			
			
			,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.TREE_FIR, w,p, f), 0.3)
			,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.GRASS, w,p, f), 01.2)
			,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.CLOUD, w,p, f), 0.05)
			,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.SNAIL, w,p, f), 0.01)
			,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.BUTTERFLY, w,p, f.copy().shift(0, 90)), 0.01)
			,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.VILLAGER, w,p, f), 0.01)
			),
		NORMAL(new Stratum[]{
				null,
				new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
				null,
				null,
				new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
				null
				},
				
				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.PYRAMID, w,p, f), 0.03),
				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.CACTUS, w,p, f), 0.05)
		),
		JUNGLE(new Stratum[]{
				null,
				new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
				null,
				null,
				new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
				null
				}

//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.TREE_JUNGLE, w,p, f), 0.2)
//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.BUSH_JUNGLE, w,p, f), 0.3)
//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.FERN, w,p, f), 0.5)
//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.BIRD_RAINBOW, w,p, f), 0.1)
		),
		LAKE(new Stratum[]{
			new Stratum(Material.WATER, 200.0, 20.0, 10, 50, 0, 4),
			new Stratum(Material.SAND, 20.0, 20.0, 5, 40, 2, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			}),
	EMPTY(new Stratum[]{
			null,
			null,
			null,
			null,
			null,
			new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			}),
	TEST(new Stratum[]{
			null,
			null,
			null,
			null,
			null,
			new Stratum(Material.STONE, 100000000.0, 200.0, 20, 40, 1, 5),
			null
			}
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.TREE_FIR, w,p, f), 0.3),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.GRASS, w,p, f), 01.2),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.CLOUD, w,p, f), 0.05),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.SNAIL, w,p, f), 0.01),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.BUTTERFLY, w,p, f.copy().shift(0, 90)), 0.01),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.PERSON, w,p, f), 0.01)
				);
	
	ThingSpawner[] spawners;
	Stratum[] stratums;
	
	Biome(Stratum[] stratums, ThingSpawner... spawners){
		this.stratums = stratums;
		this.spawners = spawners;
	}
	
	Vec posField = new Vec();
	
	public void spawnThings(WorldData world, Column c){
		for(ThingSpawner ts : spawners){
			double prob = ts.probabilityOnFieldWidth;
			if(prob >= 1){
				int greaterThanOne = (int) prob;
				prob -= greaterThanOne;
				for(; greaterThanOne > 0; greaterThanOne--){
					ts.spawner.spawn(world, c.getRandomTopLocation(world.random, posField), posField);
				}
			}
			if(world.random.nextDouble() < prob){
				ts.spawner.spawn(world, c.getRandomTopLocation(world.random, posField), posField);
			}
		}
	}
	
	public static class ThingSpawner {
		
		Spawner spawner;
		double probabilityOnFieldWidth;
		
		public ThingSpawner(Spawner spawner, double probabilityOnFieldWidth){
			this.spawner = spawner;
			this.probabilityOnFieldWidth = probabilityOnFieldWidth;
		}
		public interface Spawner { public void spawn(WorldData world, Vertex field, Vec pos); }
	}
}
