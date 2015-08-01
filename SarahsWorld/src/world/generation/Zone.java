package world.generation;

import java.util.Random;


public abstract class Zone {

	public static enum Attribute {
		TREES, DRY, MOIST, LAKES, ROUGH, FLAT, LONELY, BUSY, HOT, HILLY
	}
	protected Random random;
	public BiomeManager biome;
	public boolean end;
	public double ownHeight;
	public Zone subZone;
	public double originX;
	public boolean left;
	public boolean[] description;
	
	/**
	 * The Zone is just responsible for the terrain height - the biome manager manages the rest like structure materials and things
	 * @param biome the BiomeManager this zone shall use
	 * @param originX the starting point on the x-Axis of this zone
	 */
	public Zone(Random random, BiomeManager biome, double originX, boolean left, boolean[] description){
		this.biome = biome;
		this.originX = originX;
		this.random = random;
		this.left = left;
		this.description = description;
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
	 * @return the height at x of this zone and all subZones together
	 */
	public double y(double x){
		return step(x) + (subZone != null ? subZone.y(x - subZone.originX) : 0);
	}
}
