package world.generation;

import effects.WorldEffect;
import things.Thing;
import things.ThingType;
import util.Color;
import util.math.Vec;
import world.Material;
import world.Stratum;
import world.WorldData;
import world.WorldData.Column;
import world.WorldWindow;

public enum Biome {
	DESERT(new Color(0.2f, 0.4f, 0.65f), new Color(0.73f, 0.84f, 0.95f),
			new Stratum[]{
				null,
				null,
				new Stratum(Material.SAND, 40.0, 20.0, 5, 40, 5, 4),
				new Stratum(Material.SANDSTONE, 50, 20.0, 20, 40, 5, 4),
				new Stratum(Material.SANDSTONE2, 100, 20.0, 20, 40, 5, 4),
				new Stratum(Material.SANDSTONE, 100, 20.0, 20, 40, 5, 4),
				null,
				new Stratum(Material.SANDSTONE2, 1000.0, 200.0, 20, 40, 5, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(ThingType.PYRAMID.defaultSpawner, 0.03),
				new ThingSpawner(ThingType.CACTUS.defaultSpawner, 0.05),
				new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.05),
				new ThingSpawner(ThingType.SCORPION.defaultSpawner, 0.01)
			},
			new EffectSpawner[]{
			}
	),
	OASIS(new Color(0.2f, 0.4f, 0.65f), new Color(0.73f, 0.84f, 0.95f),
			new Stratum[]{
				null,
				new Stratum(Material.GRASS, 10, 3.0, 0.15, 40, 5, 4),
				new Stratum(Material.SAND, 40.0, 20.0, 5, 40, 5, 4),
				new Stratum(Material.SANDSTONE, 50, 20.0, 20, 40, 5, 4),
				new Stratum(Material.SANDSTONE2, 100, 20.0, 20, 40, 5, 4),
				new Stratum(Material.SANDSTONE, 100, 20.0, 20, 40, 5, 4),
				null,
				new Stratum(Material.SANDSTONE2, 1000.0, 200.0, 20, 40, 5, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.1),
				new ThingSpawner(ThingType.TREE_PALM.defaultSpawner, 0.1),
				new ThingSpawner(ThingType.SCORPION.defaultSpawner, 0.02),
				new ThingSpawner(ThingType.BUSH_NORMAL.defaultSpawner, 0.08)
			},
			new EffectSpawner[]{
			}
	),
	LAKE_DESERT(new Color(0.2f, 0.4f, 0.65f), new Color(0.73f, 0.84f, 0.95f),
			new Stratum[]{
				new Stratum(Material.WATER, 200.0, 20.0, 10, 50, 0, 4),
				OASIS.stratums[1],
				OASIS.stratums[2],
				OASIS.stratums[3],
				OASIS.stratums[4],
				OASIS.stratums[5],
				OASIS.stratums[6],
				OASIS.stratums[7],
				OASIS.stratums[8],
			},
			new ThingSpawner[]{
			},
			new EffectSpawner[]{
			}),
	CANDY(new Color(0.6f, 0.14f, 0.87f), new Color(0.9f, 0.68f, 0.9f),
			new Stratum[]{
				null,
				new Stratum(Material.CANDY, 60.0, 20.0, 20, 40, 20, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
				null,
				null,
				null,
				null,
				new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(ThingType.BUSH_CANDY.defaultSpawner, 0.03),
				new ThingSpawner(ThingType.CACTUS.defaultSpawner, 0.05)
			},
			new EffectSpawner[]{
			}),
	GRAVEYARD(new Color(0.5f, 0.5f, 0.5f), new Color(0.7f, 0.7f, 0.7f),
			new Stratum[]{
				null,
				new Stratum(Material.GRASS, 10.0, 20.0, 20, 10, 5, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 5, 4),
				new Stratum(Material.CLAY, 120.0, 20.0, 20, 80, 5, 4),
				null,
				null,
				null,
				new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 5, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(ThingType.TREE_GRAVE.defaultSpawner, 0.1),
				new ThingSpawner(ThingType.ZOMBIE.defaultSpawner, 0.05),
				new ThingSpawner(ThingType.GRAVE.defaultSpawner, 0.2)
	//			new ThingSpawner((w, c, pos, ed) -> {WorldWindow.toAdd.add(new Fog(pos.xInt() + Window.WIDTH_HALF, pos.yInt() + Window.HEIGHT_HALF + 50, 100, 4, 50)); return null;}, 0.01),
	//			new ThingSpawner((w, c, pos, ed) -> {WorldWindow.weather.fog.emittFog(pos.x + Window.WIDTH_HALF, pos.y + Window.HEIGHT_HALF); return null;}, 1)
			},
			new EffectSpawner[]{
					new EffectSpawner(WorldWindow.weather.fog, 1.5)
			}),
	MEADOW(new Color(0.65f, 0.83f, 1f), new Color(0.27f, 0.47f, 0.81f),
			new Stratum[]{
				null,
				new Stratum(Material.GRASS, 60.0, 20.0, 20, 40, 20, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
				null,
				null,
				null,
				null,
				new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(ThingType.PYRAMID.defaultSpawner, 0.03),
				new ThingSpawner(ThingType.CACTUS.defaultSpawner, 0.05),
				new ThingSpawner(ThingType.GRASS.defaultSpawner, 0.5)
			},
			new EffectSpawner[]{
			}),
	FIR_FORREST(new Color(0.65f, 0.83f, 1f), new Color(0.27f, 0.47f, 0.81f),
			new Stratum[]{
				null,
				new Stratum(Material.GRASS, 20.0, 20.0, 5, 40, 2, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 5, 4),
				null,
				null,
				null,
				null,
				new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 5, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(ThingType.TREE_FIR.defaultSpawner, 0.3)
				,new ThingSpawner(ThingType.GRASS.defaultSpawner, 01.2)
				,new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.05)
				,new ThingSpawner(ThingType.SNAIL.defaultSpawner, 0.01)
				,new ThingSpawner(ThingType.BUTTERFLY.defaultSpawner, 0.01)
//				,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.VILLAGER, w,p, f), 0.01)	
			},
			new EffectSpawner[]{
			}),
		LAKE(new Color(0.65f, 0.83f, 1f), new Color(0.27f, 0.47f, 0.81f),
				new Stratum[]{
					new Stratum(Material.WATER, 200.0, 20.0, 10, 50, 0, 4),
					new Stratum(Material.SAND, 20.0, 20.0, 5, 40, 2, 4),
					new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 2, 4),
					new Stratum(Material.STONE, 50.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE2, 50.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE, 100.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE2, 100.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
					null
				},
				new ThingSpawner[]{
				},
				new EffectSpawner[]{
				}),
		JUNGLE(new Color(1, 1, 1), new Color(1f, 1f, 1f),
				new Stratum[]{
					null,
					new Stratum(Material.GRASS, 40.0, 20.0, 20, 40, 20, 4),
					new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 20, 4),
					new Stratum(Material.STONE, 50.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE2, 50.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE, 100.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE2, 100.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
					null
				},
				new ThingSpawner[]{
					 new ThingSpawner(ThingType.TREE_JUNGLE.defaultSpawner, 0.5)
					,new ThingSpawner(ThingType.FERN.defaultSpawner, 0.5)
					,new ThingSpawner((w, c, pos, ed) -> {Thing thing = new Thing(ThingType.BAMBOO, w, c, pos); if(thing.behind > 0) thing.behind = 0; return thing;}, 0.2)
					,new ThingSpawner((w, c, pos, ed) -> {Thing thing = new Thing(ThingType.BUSH_NORMAL, w, c, pos); if(thing.behind > 0) thing.behind = 0; return thing;}, 0.2)
					,new ThingSpawner((w, c, pos, ed) -> {Thing thing = new Thing(ThingType.GIANT_GRASS, w, c, pos); if(thing.behind > 0) thing.behind = 0; return thing;}, 0.1)
					,new ThingSpawner((w, c, pos, ed) -> new Thing(ThingType.CRACK, w, c, pos.shift(0, -100 - w.random.nextInt(1000))), 0.3)
					,new ThingSpawner((w, c, pos, ed) -> new Thing(ThingType.FOSSIL, w, c, pos.shift(0, -200 - w.random.nextInt(1000))), 0.1)
					,new ThingSpawner((w, c, pos, ed) -> {Thing thing = new Thing(ThingType.FLOWER_NORMAL, w, c, pos); if(thing.behind > 0) thing.behind = 0; return thing;}, 0.1)
					,new ThingSpawner((w, c, pos, ed) -> new Thing(ThingType.BUSH_JUNGLE, w, c, pos, 1.5, -4), 0.5)
					,new ThingSpawner(ThingType.GRASS.defaultSpawner, 01.2)
					,new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.05)
					,new ThingSpawner(ThingType.SNAIL.defaultSpawner, 0.01)
					,new ThingSpawner(ThingType.BUTTERFLY.defaultSpawner, 0.05)
					,new ThingSpawner(ThingType.MIDGE.defaultSpawner, 2)
				},
				new EffectSpawner[]{
				}
		),
	EMPTY(new Color(1, 1, 1), new Color(1f, 1f, 1f), new Stratum[]{
			null,
			null,
			null,
			null,
			null,
			new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
			null,
			null,
			null
			},
			new ThingSpawner[]{
			},
			new EffectSpawner[]{
			}),
	TEST(new Color(1, 1, 1), new Color(1f, 1f, 1f),
			new Stratum[]{
				null,
				null,
				null,
				null,
				null,
				new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
				null,
				null,
				null
			},
			new ThingSpawner[]{
			},
			new EffectSpawner[]{
			}
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.TREE_FIR, w,p, f), 0.3),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.GRASS, w,p, f), 01.2),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.CLOUD, w,p, f), 0.05),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.SNAIL, w,p, f), 0.01),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.BUTTERFLY, w,p, f.copy().shift(0, 90)), 0.01),
//				new ThingSpawner((w, p, f) -> new ThingProps(ThingType.PERSON, w,p, f), 0.01)
				);
	
	ThingSpawner[] spawners;
	public static int layerCount = 9;
	public final Stratum[] stratums;
	public Color topColor, lowColor;
	public EffectSpawner[] effects;
	
	Biome(Color topColor, Color lowColor, Stratum[] stratums, ThingSpawner[] spawners, EffectSpawner[] effects){
		this.stratums = stratums;
		this.topColor = topColor;
		this.lowColor = lowColor;
		this.spawners = spawners;
		this.effects = effects;
		for(int i = 0; i < stratums.length; i++){
			if(stratums[i] == null){
				stratums[i] = Stratum.air;
			}
		}
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
	
	public void spawnEffects(WorldData world, Column c, boolean left){
		for(int i = 0; i < effects.length; i++){
			double prob = effects[i].probabilityOnFieldWidth;
			if(prob >= 1){
				int greaterThanOne = (int) prob;
				prob -= greaterThanOne;
				for(; greaterThanOne > 0; greaterThanOne--){
					c.getRandomTopLocation(world.random, posField);
					effects[i].effect.spawn(posField.x, posField.y, left);
				}
			}
			if(world.random.nextDouble() < prob){
				c.getRandomTopLocation(world.random, posField);
				effects[i].effect.spawn(posField.x, posField.y, left);
			}
		}
	}
	
	public static class EffectSpawner {
		
		WorldEffect effect;
		double probabilityOnFieldWidth;
		
		public EffectSpawner(WorldEffect effect, double probabilityOnFieldWidth){
			this.effect = effect;
			this.probabilityOnFieldWidth = probabilityOnFieldWidth;
		}
	}
	
	public static class ThingSpawner {
		
		Spawner spawner;
		double probabilityOnFieldWidth;
		
		public ThingSpawner(Spawner spawner, double probabilityOnFieldWidth){
			this.spawner = spawner;
			this.probabilityOnFieldWidth = probabilityOnFieldWidth;
		}
		
		public interface Spawner { public Thing spawn(WorldData world, Column field, Vec pos, Object... extraData); }
		//default: (w, c, pos, ed) -> new Thing(ThingType.EXAMPLE, w, c, pos.copy(), ed);
	}
}
