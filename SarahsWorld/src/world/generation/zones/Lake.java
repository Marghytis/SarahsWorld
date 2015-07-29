package world.generation.zones;

import java.util.Random;

import world.generation.Zone;
import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;

public class Lake extends Zone {

	double width;
	double height;
	Biome before;
	
	/**
	 * 
	 * @param biome
	 * @param originX
	 * @param width
	 * @param height is where the last zone stopped
	 * @param left
	 */
	public Lake(Random random, BiomeManager biome, double originX, double width, double height, boolean left) {
		super(random, biome, originX, left, null);
		before = biome.biome;
		biome.switchToBiome(Biome.LAKE);
		this.width = width;
		this.ownHeight = height;
	}
	
	boolean endReached;

	public double step(double x) {
		if(!endReached && x >= width){
			endReached = true;
			biome.ants[0].resize(0, biome.ants[0].sizingSpeed);
		}
		if(endReached && biome.ants[0].reachedSize){
			end = true;
			biome.switchToBiome(before);
		}
		return ownHeight;
	}

}
