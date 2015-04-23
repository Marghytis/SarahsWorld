package world.generation;

import util.Color;
import util.math.Vec;
import world.Material;
import world.World;
import world.WorldContainer.WorldField;
import world.objects.ThingType;


public enum BiomeOld {
	NORMAL(			new AimLayer[]{new AimLayer(Material.GRASS, 30.0, 1.0, 100), new AimLayer(Material.EARTH, 50.0, 5.0, 80), new AimLayer(Material.STONE, 100000000.0, 20.0, 0)}),
	
	UNNORMAL(		new AimLayer[]{new AimLayer(Material.EARTH, 20.0, 1.0, 90), new AimLayer(Material.SAND, 60.0, 5.0, 79), new AimLayer(Material.WATER, 100000000.0, 20.0, 1)}),
	
	STONY(			new AimLayer[]{new AimLayer(Material.STONE, 2000, 20, 0)}),
	
	CANDY(			new AimLayer[]{new AimLayer(Material.CANDY, 30.0, 1.0, 100), new AimLayer(Material.EARTH, 50.0, 5.0, 80), new AimLayer(Material.STONE, 100000000.0, 20.0, 0)},
						new ThingSpawner((w, f, p) -> ThingType.TREE_CANDY.create(w, f, p.copy()), 0.05)),
	
	MEADOW(			new AimLayer[]{new AimLayer(Material.GRASS, 30.0, 1.0, 100), new AimLayer(Material.EARTH, 50.0, 5.0, 80), new AimLayer(Material.STONE, 100000000.0, 20.0, 0)},
						new ThingSpawner((w, f, p) -> ThingType.GRASS.create(w, f, p.copy()), 01.2),
						new ThingSpawner((w, f, p) -> ThingType.FLOWER_NORMAL.create(w, f, p.copy()), 0.2),
						new ThingSpawner((w, f, p) -> ThingType.COW.create(w, f, p.copy()), 0.05),
						new ThingSpawner((w, f, p) -> ThingType.BUSH_NORMAL.create(w, f, p.copy()), 0.05)),
	
	MIXED_FOREST(	new AimLayer[]{new AimLayer(Material.GRASS, 30.0, 1.0, 100), new AimLayer(Material.EARTH, 50.0, 5.0, 80), new AimLayer(Material.STONE, 100000000.0, 20.0, 0)},
						new ThingSpawner((w, f, p) -> ThingType.TREE_NORMAL.create(w, f, p.copy()), 0.05)),
					
	FIR_FORREST(	new AimLayer[]{new AimLayer(Material.GRASS, 30.0, 1.0, 99), new AimLayer(Material.EARTH, 50.0, 5.0, 80), new AimLayer(Material.STONE, 100000000.0, 20.0, 0)},
						new ThingSpawner((w, f, p) -> ThingType.TREE_FIR.create(w, f, p), 0.3),
						new ThingSpawner((w, f, p) -> ThingType.GRASS.create(w, f, p.copy()), 01.2),
						new ThingSpawner((w, f, p) -> ThingType.CLOUD.create(w, f, p.copy()), 0.05),
						new ThingSpawner((w, f, p) -> ThingType.SNAIL.create(w, f, p.copy()), 0.01),
						new ThingSpawner((w, f, p) -> ThingType.BUTTERFLY.create(w, f, p.copy().shift(0, 90)), 0.01)),
					
	FIR_SNOW_FORREST(new AimLayer[]{new AimLayer(Material.SNOW, 30.0, 1.0, 100), new AimLayer(Material.EARTH, 50.0, 5.0, 80), new AimLayer(Material.STONE, 100000000.0, 20.0, 0)},
						new ThingSpawner((w, f, p) -> ThingType.TREE_FIR_SNOW.create(w, f, p.copy()), 0.3),
						new ThingSpawner((w, f, p) -> ThingType.CLOUD.create(w, f, p.copy()), 0.05)),
	GRAVEYARD(		new AimLayer[]{new AimLayer(Material.GRASS, 30.0, 1.0, 100), new AimLayer(Material.EARTH, 50.0, 5.0, 80), new AimLayer(Material.STONE, 100000000.0, 20.0, 0)},
						new ThingSpawner((w, f, p) -> ThingType.TREE_GRAVE.create(w, f, p.copy()), 0.3),
						new ThingSpawner((w, f, p) -> ThingType.CLOUD.create(w, f, p.copy(), new Color(0.4f, 0.4f, 0.4f)), 0.05)),
	DESERT(			new AimLayer[]{new AimLayer(Material.SAND, 60.0, 1.0, 92), new AimLayer(Material.SANDSTONE, 100000000.0, 200.0, 1)},
						new ThingSpawner((w, f, p) -> ThingType.PYRAMID.create(w, f, p.copy()), 0.03),
						new ThingSpawner((w, f, p) -> ThingType.CACTUS.create(w, f, p.copy()), 0.05)),;
	
	public AimLayer[] layers;
	public ThingSpawner[] spawners;
	
	BiomeOld(AimLayer[] layers, ThingSpawner... spawners){
		this.layers = layers;
		this.spawners = spawners;
	}
	
	public static class AimLayer {

		public Material mat;
		public double sizingSpeed;
		public double thickness;
		public int priority;
		
		public AimLayer(Material mat, double thickness, double sizingSpeed, int priority) {
			this.mat = mat;
			this.thickness = thickness;
			this.sizingSpeed = sizingSpeed;
			this.priority = priority;
		}
	}
	
	public static class ThingSpawner {
		
		Spawner spawner;
		double probabilityOnFieldWidth;
		
		public ThingSpawner(Spawner spawner, double probabilityOnFieldWidth){
			this.spawner = spawner;
			this.probabilityOnFieldWidth = probabilityOnFieldWidth;
		}

		public interface Spawner { public void spawn(World world, WorldField field, Vec pos); }
		
	}
}
