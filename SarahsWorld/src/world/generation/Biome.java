package world.generation;

import effects.WorldEffect;
import main.Main;
import things.Thing;
import things.ThingType;
import util.Color;
import util.math.Function;
import util.math.Vec;
import world.World;
import world.data.Column;
import world.data.Vertex;

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
				new ThingSpawner(spawnBetween(ThingType.PYRAMID, -30, -50), 0.03),
				new ThingSpawner(spawnBetween(ThingType.CACTUS, -5, -10), 0.05),
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
				new Stratum(Material.SAND, 40.0, 20.0, 3, 40, 5, 4),
				new Stratum(Material.SANDSTONE, 50, 20.0, 20, 40, 5, 4),
				new Stratum(Material.SANDSTONE2, 100, 20.0, 20, 40, 5, 4),
				new Stratum(Material.SANDSTONE, 100, 20.0, 20, 40, 5, 4),
				null,
				new Stratum(Material.SANDSTONE2, 1000.0, 200.0, 20, 40, 5, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.1),
				new ThingSpawner(spawnBetween(ThingType.TREE_PALM, -7, -14), 0.1),
				new ThingSpawner(spawnBetween(ThingType.BUSH_NORMAL, -5, -10), 0.08),
				new ThingSpawner(ThingType.SCORPION.defaultSpawner, 0.02),
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
	OCEAN(new Color(0.2f, 0.4f, 0.65f), new Color(0.73f, 0.84f, 0.95f),
			new Stratum[]{
					new Stratum(Material.WATER, 400.0, 20.0, 10, 50, 0, 4),
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
	CANDY(new Color(0.6f, 0.14f, 0.87f), new Color(0.9f, 0.68f, 0.9f),
			new Stratum[]{
				null,
				new Stratum(Material.CANDY, 60.0, 20.0, 20, 100, 20, 4),
				null,
				null,
				null,
				null,
				null,
				new Stratum(Material.SNOW, 1000.0, 200.0, 20, 40, 1, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(spawnBetween(ThingType.BUSH_CANDY, -5, -15), 0.1),
				new ThingSpawner(spawnBetween(ThingType.FLOWER_CANDY, -5, -10), 0.2),
				new ThingSpawner(spawnBetween(ThingType.TREE_CANDY, -20, -30), 0.06),
				new ThingSpawner((c, pos, ed) -> new Thing(ThingType.HEART, c, pos, 1), 0.04),
				new ThingSpawner(ThingType.UNICORN.defaultSpawner, 0.01),
				new ThingSpawner((c, pos, ed) -> new Thing(ThingType.CLOUD, c, pos, new Color(0.9f, 0.68f, 0.9f)), 0.06),
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
				new ThingSpawner(ThingType.GRAVE.defaultSpawner, 0.2),
				new ThingSpawner((c, pos, ed) -> new Thing(ThingType.WORLD_EFFECT, c, pos, Main.world.data.getWeather().fog),1.5)
	//			new ThingSpawner((w, c, pos, ed) -> {WorldWindow.toAdd.add(new Fog(pos.xInt() + Main.HALFSIZE.w, pos.yInt() + Main.HALFSIZE.h + 50, 100, 4, 50)); return null;}, 0.01),
	//			new ThingSpawner((w, c, pos, ed) -> {WorldWindow.weather.fog.emittFog(pos.x + Main.HALFSIZE.w, pos.y + Main.HALFSIZE.h); return null;}, 1)
			},
			new EffectSpawner[]{
//					new EffectSpawner(Main.world.window.getWeather().fog, 1.5)
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
				new ThingSpawner(ThingType.GRASS.defaultSpawner, 0.5),
				new ThingSpawner(ThingType.COW.defaultSpawner, 0.01),
				new ThingSpawner(ThingType.FLOWER_NORMAL.defaultSpawner, 0.1),
				new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.1),
				new ThingSpawner(ThingType.BUSH_NORMAL.defaultSpawner, 0.01)
			},
			new EffectSpawner[]{
			}),
	FIR_FORREST(new Color(0.65f, 0.83f, 1f), new Color(0.27f, 0.47f, 0.81f),
			new Stratum[]{
				null,
				new Stratum(Material.GRASS, 20.0, 20.0, 5, 40, 2, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 5, 4),
				new Stratum(Material.STONE, 50.0, 20.0, 20, 40, 1, 5),
				new Stratum(Material.STONE2, 50.0, 20.0, 20, 40, 1, 5),
				new Stratum(Material.STONE, 100.0, 20.0, 20, 40, 1, 5),
				new Stratum(Material.STONE2, 100.0, 20.0, 20, 40, 1, 5),
				new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
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
	WINTER_FORREST(new Color(0.91f, 0.94f, 0.94f), new Color(0.73f, 0.82f, 0.86f),
			new Stratum[]{
				null,
				new Stratum(Material.SNOW, 20.0, 20.0, 5, 40, 2, 4),
				new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 5, 4),
				new Stratum(Material.STONE, 50.0, 20.0, 20, 40, 1, 5),
				new Stratum(Material.STONE2, 50.0, 20.0, 20, 40, 1, 5),
				new Stratum(Material.STONE, 100.0, 20.0, 20, 40, 1, 5),
				new Stratum(Material.STONE2, 100.0, 20.0, 20, 40, 1, 5),
				new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
				null
			},
			new ThingSpawner[]{
				new ThingSpawner(ThingType.TREE_FIR_SNOW.defaultSpawner, 0.3)
				,new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.05)
				,new ThingSpawner((c, pos, ed) -> new Thing(ThingType.RABBIT, c, pos, 1), 0.01)
				,new ThingSpawner((c, pos, ed) -> new Thing(ThingType.HEART, c, pos, 1), 0.04)
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
						new ThingSpawner((c, pos, ed) -> new Thing(ThingType.BUSH_JUNGLE, c, pos, 2.0, 0.9), 0.5),
						new ThingSpawner(spawnZ(ThingType.TREE_JUNGLE, 0.2),		0.5),
						new ThingSpawner(spawnZ(ThingType.FERN, 			0.1,	0.2),	0.05),
						new ThingSpawner(spawnZ(ThingType.BAMBOO, 			0.1,	0.2),	0.1),
						new ThingSpawner(spawnZ(ThingType.BUSH_NORMAL, 			0.1,	0.2),	0.1),
						new ThingSpawner(spawnZ(ThingType.GIANT_GRASS, 0.1, 0.2), 0.05),
						new ThingSpawner(spawnZ(ThingType.FLOWER_NORMAL, 0.1, 0.2), 0.05),
						new ThingSpawner(spawnZ(ThingType.GRASS, 0.1, 0.2), 0.6),
						new ThingSpawner(spawnZ(ThingType.FLOWER_JUNGLE, 0.1, 0.2), 0.6),
						new ThingSpawner(spawnBetween(ThingType.FLOWER_JUNGLE, 0, -20, true), 0.3),
						new ThingSpawner(spawnBetween(ThingType.GRASS, -5, -15, true), 0.3),
						new ThingSpawner(spawnBetween(ThingType.FLOWER_NORMAL, 0, -5, true), 0.05),
						new ThingSpawner(spawnBetween(ThingType.GIANT_GRASS, 0, -25, true), 0.05),
						new ThingSpawner(spawnBetween(ThingType.BUSH_NORMAL, 			0,	-10, true),	0.1),
						new ThingSpawner(spawnBetween(ThingType.BAMBOO, 			0,	-25, true),	0.1),
						new ThingSpawner(spawnBetween(ThingType.FERN, 			0,	-25, true),	0.5),
						new ThingSpawner(spawnZ(ThingType.TREE_JUNGLE, -0.2),		0.05),
//					new ThingSpawner(posSpawner(ThingType.BAMBOO, 0, 0.1, -5), 0.2)
//					,new ThingSpawner(posSpawner(ThingType.BUSH_NORMAL, 0, 0.1, 0), 0.2)
//					,new ThingSpawner(posSpawner(ThingType.GIANT_GRASS, 0, 0.1, 0), 0.1)
//					,new ThingSpawner((w, c, pos, ed) -> new Thing(ThingType.BUSH_JUNGLE, w, c, pos, 1.5, -4), 0.5)
//					,new ThingSpawner(posSpawner(ThingType.FLOWER_NORMAL, 0, 0.1, 0), 0.1)
//					,new ThingSpawner(ThingType.GRASS.defaultSpawner, 01.2)
					
					new ThingSpawner((c, pos, ed) -> new Thing(ThingType.CRACK, c, pos.shift(0, -100 - World.rand.nextInt(1000))), 0.3),
					new ThingSpawner((c, pos, ed) -> new Thing(ThingType.FOSSIL, c, pos.shift(0, -200 - World.rand.nextInt(1000))), 0.1)
//					,new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.05)
					,new ThingSpawner(ThingType.SNAIL.defaultSpawner, 0.01)
					,new ThingSpawner(ThingType.BUTTERFLY.defaultSpawner, 0.05)
					,new ThingSpawner(ThingType.MIDGE.defaultSpawner, 2)
				},
				new EffectSpawner[]{
				}
		),
		FIR_VILLAGE(new Color(0.65f, 0.83f, 1f), new Color(0.27f, 0.47f, 0.81f),
				new Stratum[]{
					null,
					new Stratum(Material.GRASS, 20.0, 20.0, 5, 40, 2, 4),
					new Stratum(Material.EARTH, 60.0, 20.0, 20, 40, 5, 4),
					new Stratum(Material.STONE, 50.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE2, 50.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE, 100.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE2, 100.0, 20.0, 20, 40, 1, 5),
					new Stratum(Material.STONE, 1000.0, 200.0, 20, 40, 1, 5),
					null
				},
				new ThingSpawner[]{
					new ThingSpawner(ThingType.TREE_FIR.defaultSpawner, 0.05)
					,new ThingSpawner(ThingType.GRASS.defaultSpawner, 0.2)
					,new ThingSpawner(ThingType.CLOUD.defaultSpawner, 0.03)
					,new ThingSpawner(ThingType.VILLAGER.defaultSpawner, 0.1)
					,new ThingSpawner(spawnBetween(ThingType.HOUSE, -20, -30, false), 0.05)
					,new ThingSpawner(spawnBetween(ThingType.TOWN_OBJECT, -20, -30), 0.05)
					,new ThingSpawner(ThingType.BUTTERFLY.defaultSpawner, 0.01)
					,new ThingSpawner(ThingType.CAKE.defaultSpawner, 0.01)
//					,new ThingSpawner((w, p, f) -> new ThingProps(ThingType.VILLAGER, w,p, f), 0.01)	
				},
				new EffectSpawner[]{
				}),
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
	
	public Vertex[] createCenterVertices(){
		Vertex[] vertices = new Vertex[layerCount];
		double y = 0;
		for(int i = 0; i < layerCount; i++){
			Material[] mats = new Material[Vertex.maxMatCount];
			double[] alphas = new double[Vertex.maxMatCount];
			alphas[0] = 1;
			mats[1] = Material.AIR;
			mats[2] = Material.AIR;
			mats[3] = Material.AIR;
			if(stratums[i] == null){
				mats[0] = Material.AIR;
				vertices[i] = new Vertex(i, mats, alphas, 0, 0, 0, y);
			} else {
				mats[0] = stratums[i].material;
				vertices[i] = new Vertex(i, mats, alphas, 0, 0, stratums[i].transitionHeight, y);
				y -= stratums[i].thickness;
			}
		}
		return vertices;
	}
	
	Vec posField = new Vec();
	
	public void spawnThings(Column c){
		for(ThingSpawner ts : spawners){
			applySpawner(c, ts);
		}
	}
	
	public void applySpawner(Column c, ThingSpawner ts) {
		double prob = ts.probabilityOnFieldWidth;
		if(prob >= 1){
			int greaterThanOne = (int) prob;
			prob -= greaterThanOne;
			for(; greaterThanOne > 0; greaterThanOne--){
				ts.spawner.spawn(c.getRandomTopLocation(World.rand, posField), posField.copy());
			}
		}
		if(World.rand.nextDouble() < prob){
			ts.spawner.spawn(c.getRandomTopLocation(World.rand, posField), posField.copy());
		}
	}
	
	public void spawnEffects(Column c, boolean left){
		for(int i = 0; i < effects.length; i++){
			double prob = effects[i].probabilityOnFieldWidth;
			if(prob >= 1){
				int greaterThanOne = (int) prob;
				prob -= greaterThanOne;
				for(; greaterThanOne > 0; greaterThanOne--){
					c.getRandomTopLocation(World.rand, posField);
					effects[i].effect.spawn(posField.x, posField.y);
				}
			}
			if(World.rand.nextDouble() < prob){
				c.getRandomTopLocation(World.rand, posField);
				effects[i].effect.spawn(posField.x, posField.y);
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
	
	public static Spawner spawnZ(ThingType type, double z){
		return (c, pos, ed) -> {
			Thing t = new Thing(type, c, pos.copy(), ed);
			t.z = z;
			return t;
		};
	}
	
	public static Spawner spawnZ(ThingType type, double z1, double z2){
		return (c, pos, ed) -> {
			Thing t = new Thing(type, c, pos.copy(), ed);
			t.z = z1 + World.rand.nextDouble()*(z2-z1);
			return t;
		};
	}

	public static Spawner spawnBetween(ThingType type, double y1, double y2){
		return spawnBetween(type, y1, y2, true, true);
	}
	
	public static Spawner spawnBetween(ThingType type, double y1, double y2, boolean front){
		return spawnBetween(type, y1, y2, false, front);
	}
	
	public static Spawner spawnBetween(ThingType type, double y1, double y2, boolean randomSide, boolean front){
		return (c, pos, ed) -> {
			Thing t = new Thing(type, c, pos.copy(), ed);
			t.yOffset = y1 + World.rand.nextDouble()*(y2-y1);
			boolean frontLocal = front;
			if(randomSide) frontLocal = World.rand.nextBoolean();
			if(!frontLocal){
				t.z = foliageZ.f(t.yOffset);
			} else {
				t.z = -foliageZ.f(t.yOffset);
			}
			return t;
		};
	}

	//assigns a z value to every yOffset, so that no plants overlap badly (leaves a gap of 0.2 around z=0 for living things)
	public static final Function foliageZ = (y) -> 0.1*(1.5 - (Math.atan(y)/Math.PI));
	
	public static class SpawnType {
		ThingType type;
		double prob;
	}
	
	public static class ThingSpawner {
		
		Spawner spawner;
		double probabilityOnFieldWidth;
		Object[] extraData;

		public ThingSpawner(Spawner spawner, double probabilityOnFieldWidth){
			this.spawner = spawner;
			this.probabilityOnFieldWidth = probabilityOnFieldWidth;
		}
	}

	//default: (w, c, pos, ed) -> new Thing(ThingType.EXAMPLE, w, c, pos.copy(), ed);
}
