package world.generation;

import java.util.Random;


public abstract class Zone {

	protected Random random;
	public world.worldGeneration.BiomeManager biome;
	public boolean end;
	public double ownHeight;
	public Zone subZone;
	public double originX;
	public boolean left;
	
	/**
	 * The Zone is just responsible for the terrain height - the biome manager manages the rest like structure materials and things
	 * @param biome the BiomeManager this zone shall use
	 * @param originX the starting point on the x-Axis of this zone
	 */
	public Zone(world.worldGeneration.BiomeManager biome, double originX, boolean left){
		this.biome = biome;
		this.originX = originX;
		random = new Random();
		this.left = left;
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
