package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Material;
import world.generation.Stratum;
import world.generation.BiomeManager.State;
import world.generation.Zone;

public class Lake extends Zone {
	
	/**
	 * Use at yIndex 0 :)
	 */
	public static Stratum lake = new Stratum(Material.WATER, 200.0, 20.0, 10, 50, 0, 4);

	double width;
	double height;
	Biome before;
	
	/**
	 * 
	 * @param biome
	 * @param originX
	 * @param width is actually exactly half the width of the lake :D
	 * @param height is where the last zone stopped
	 * @param left
	 */
	public Lake(Random random, BiomeManager biome, double originX, double width, double height, boolean left, Biome lakeBiome) {
		super(random, biome, originX, left, null);
		before = biome.biome;
		biome.switchToBiome(lakeBiome);
		if(biome.ants[0].state == State.NOTHING) biome.ants[0].switchTo(lake, 0);
		this.width = width;
		this.ownHeight = height;
	}
	public Lake(Random random, BiomeManager biome, double originX, double width, double height, boolean left) {
		this(random, biome, originX, width, height, left, Biome.LAKE);
	}
	
	boolean endReached;

	public double step(double x) {
		if(!endReached && x >= width){
			endReached = true;
			biome.ants[0].resize(0, 0, lake.sizingSpeed);
		}
		if(endReached && biome.ants[0].reachedSize){
			end = true;
			biome.switchToBiome(before);
		}
		return ownHeight;
	}

}
