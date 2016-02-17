package world.generation;

import things.ThingType;
import util.math.Vec;
import world.Material;
import world.Stratum;
import world.WorldData;
import world.WorldData.Column;

public enum Biome {
	DESERT(new Stratum[]{
			null,
			null,
			new Stratum(Material.SAND, 40.0, 20.0, 5, 40, 2, 4),
			new Stratum(Material.SANDSTONE, 50, 20.0, 20, 40, 2, 4),
			new Stratum(Material.SANDSTONE2, 100, 20.0, 20, 40, 2, 4),
			new Stratum(Material.SANDSTONE, 100, 20.0, 20, 40, 2, 4),
			new Stratum(Material.SANDSTONE2, 1000000.0, 200.0, 20, 40, 5, 5),
			null
			},
			
			new ThingSpawner(ThingType.PYRAMID.defaultSpawner, 0.03),
			new ThingSpawner(ThingType.CACTUS.defaultSpawner, 0.05),
			new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.05),
			new ThingSpawner(ThingType.SCORPION.defaultSpawner, 0.01)
	),
	OASIS(new Stratum[]{
			null,
			new Stratum(Material.GRASS, 10, 3.0, 0.15, 40, 2, 4),
			new Stratum(Material.SAND, 40.0, 20.0, 5, 40, 2, 4),
			new Stratum(Material.SANDSTONE, 50, 20.0, 20, 40, 2, 4),
			new Stratum(Material.SANDSTONE2, 100, 20.0, 20, 40, 2, 4),
			new Stratum(Material.SANDSTONE, 100, 20.0, 20, 40, 2, 4),
			new Stratum(Material.SANDSTONE2, 1000000.0, 200.0, 20, 40, 5, 5),
			null
			},
			
			new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.1),
			new ThingSpawner(ThingType.TREE_PALM.defaultSpawner, 0.1),
			new ThingSpawner(ThingType.SCORPION.defaultSpawner, 0.02),
			new ThingSpawner(ThingType.BUSH_NORMAL.defaultSpawner, 0.08)
	),
	LAKE_DESERT(new Stratum[]{
			new Stratum(Material.WATER, 200.0, 20.0, 10, 50, 0, 4),
			OASIS.stratums[1],
			OASIS.stratums[2],
			OASIS.stratums[3],
			OASIS.stratums[4],
			OASIS.stratums[5],
			OASIS.stratums[6],
			null
			}),
	CANDY(new Stratum[]{
			null,
			new Stratum(Material.CANDY, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 1, 5),
			null,
			null
			},
			
			new ThingSpawner(ThingType.BUSH_CANDY.defaultSpawner, 0.03),
			new ThingSpawner(ThingType.CACTUS.defaultSpawner, 0.05)),
	GRAVEYARD(new Stratum[]{
			null,
			new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 1, 5),
			null,
			null
			},
			
			new ThingSpawner(ThingType.PYRAMID.defaultSpawner, 0.03),
			new ThingSpawner(ThingType.CACTUS.defaultSpawner, 0.05)),
	MEADOW(new Stratum[]{
			null,
			new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
			null,
			null,
			new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 1, 5),
			null,
			null
			},
			
			new ThingSpawner(ThingType.PYRAMID.defaultSpawner, 0.03),
			new ThingSpawner(ThingType.CACTUS.defaultSpawner, 0.05)),
	FIR_FORREST(new Stratum[]{
			null,
			new Stratum(Material.GRASS, 20.0, 20.0, 5, 40, 2, 4),
			new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 5, 4),
			null,
			null,
			null,
			new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 5, 5),
			null
			}
			
			
			,new ThingSpawner(ThingType.TREE_FIR.defaultSpawner, 0.3)
			,new ThingSpawner(ThingType.GRASS.defaultSpawner, 01.2)
			,new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.05)
			,new ThingSpawner(ThingType.SNAIL.defaultSpawner, 0.01)
			,new ThingSpawner(ThingType.BUTTERFLY.defaultSpawner, 0.01)
//			,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.VILLAGER, w,p, f), 0.01)
			),
		LAKE(new Stratum[]{
				new Stratum(Material.WATER, 200.0, 20.0, 10, 50, 0, 4),
				new Stratum(Material.SAND, 20.0, 20.0, 5, 40, 2, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 2, 4),
				null,
				null,
				null,
				new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 4, 5),
				null
				}),
		NORMAL(new Stratum[]{
				null,
				new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
				null,
				null,
				new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 1, 5),
				null,
				null
				},
				
				new ThingSpawner(ThingType.PYRAMID.defaultSpawner, 0.03),
				new ThingSpawner(ThingType.CACTUS.defaultSpawner, 0.05)
		),
		JUNGLE(new Stratum[]{
				null,
				new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
				null,
				null,
				new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 1, 5),
				null,
				null
				}

//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.TREE_JUNGLE, w,p, f), 0.2)
//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.BUSH_JUNGLE, w,p, f), 0.3)
//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.FERN, w,p, f), 0.5)
//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.BIRD_RAINBOW, w,p, f), 0.1)
		),
	EMPTY(new Stratum[]{
			null,
			null,
			null,
			null,
			null,
			new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 1, 5),
			null,
			null
			}),
	TEST(new Stratum[]{
			null,
			null,
			null,
			null,
			null,
			new Stratum(Material.STONE, 1000000.0, 200.0, 20, 40, 1, 5),
			null,
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
	public final Stratum[] stratums;
	
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
					ts.spawner.spawn(world, c.getRandomTopLocation(world.random, posField), posField.copy());
				}
			}
			if(world.random.nextDouble() < prob){
				ts.spawner.spawn(world, c.getRandomTopLocation(world.random, posField), posField.copy());
			}
		}
	}
}
