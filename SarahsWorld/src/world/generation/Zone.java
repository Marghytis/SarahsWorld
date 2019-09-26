package world.generation;

import java.util.Random;

import world.data.Column;
import world.generation.zones.Candy;
import world.generation.zones.Desert;
import world.generation.zones.Graveyard;
import world.generation.zones.Jungle;
import world.generation.zones.Meadow;
import world.generation.zones.Ocean;
import world.generation.zones.Rough;
import world.generation.zones.Test;
import world.generation.zones.Village;
import world.generation.zones.Winter;


public abstract class Zone {

	public static enum Attribute {
		TREES, DRY, MOIST, LAKES, ROUGH, FLAT, LONELY, BUSY, HOT, HILLY, SWEET, EVELYN, COLD
	}
	
	@FunctionalInterface
	public static interface ZoneSupplier {
		public Zone get(Random rand, BiomeManager biomeM, double originX, boolean left);
	}
	public static enum ZoneType{
		JUNGLE((r,b,o,l) -> new Jungle(r, b, o, l), Biome.JUNGLE),
		DESERT((r,b,o,l) -> new Desert(r, b, o, l), Biome.DESERT),
		GRAVEYARD((r,b,o,l) -> new Graveyard(r, b, o, l), Biome.GRAVEYARD),
		CANDY((r,b,o,l) -> new Candy(r, b, o, l), Biome.CANDY),
		MEADOW((r,b,o,l) -> new Meadow(r, b, o, l), Biome.MEADOW),
		 OCEAN((r,b,o,l) -> new Ocean (r, b, o, l), Biome.OCEAN),
		ROUGH((r,b,o,l) -> new Rough(r, b, o, l), Biome.CANDY),
		VILLAGE((r,b,o,l) -> new Village(r, b, o, l), Biome.FIR_VILLAGE),
		WINTER((r,b,o,l) -> new Winter(r, b, o, l), Biome.WINTER_FORREST),
		TEST((r,b,o,l) -> new Test(r, b, o, l), Biome.TEST);		
		public ZoneSupplier supply;
		public Biome startBiome;
		
		ZoneType(ZoneSupplier supply, Biome startBiome){
			this.supply = supply;
			this.startBiome = startBiome;
		}
	}
	protected Random random;
	public BiomeManager biomeManager;
	public Biome startBiome;
	public boolean end;
	public double ownHeight;
	public double originX;
	public boolean left;
	protected double lastHeight;
	public boolean[] description;
	@Deprecated
	protected Zone subZone;
	
	/**
	 * The Zone is just responsible for the terrain height - the biome manager manages the rest like structure materials and things
	 * @param biomeManager the BiomeManager this zone shall use
	 * @param originX the starting point on the x-Axis of this zone
	 */
	public Zone(Random random, Biome biome, BiomeManager biomeManager, double originX, boolean left, boolean[] description){
		this.biomeManager = biomeManager;
		this.originX = originX;
		this.random = random;
		this.left = left;
		this.description = description;
		this.startBiome = biome;
		if(startBiome != null)
			biomeManager.switchToBiome(startBiome);
	}
	
	public static boolean[] describe(Attribute... attributes){
		boolean[] out = new boolean[Attribute.values().length];
		for(Attribute attr : attributes){
			out[attr.ordinal()] = true;
		}
		return out;
	}
	
	/**
	 * Change biome/zone here and return own height
	 * @param x relative x
	 */
	public abstract double step(double x);
	
	/**
	 * Updates the height of this zone and returns the complete height
	 * @return the height at x of this zone and all subZones summed up
	 */
	public double stepOnce(double x){
		biomeManager.step();
		double height1 =step(x);
		double height2 = (subZone != null ? subZone.stepOnce(x - subZone.originX) : 0);

		return height1 + height2;
	}
	
	public void setLastColumn(Column nextColumn){
		biomeManager.lastColumn = nextColumn;
	}
	
	public void spawnThings(Column column){
		biomeManager.spawnThings(column);
	}
	
	/**
	 * Don't override this, it's generic
	 * @param world
	 * @param x
	 * @return
	 */
	public Column nextColumn(double x){
		lastHeight = stepOnce(x);
		return createColumn(x);
	}
	
	protected Column createColumn(double x){
		return biomeManager.createColumn(lastHeight);
	}
}
