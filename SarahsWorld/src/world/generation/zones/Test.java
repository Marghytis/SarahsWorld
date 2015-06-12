package world.generation.zones;

import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;


public class Test extends Hills {

	public Test(BiomeManager biome, double originX, boolean left) {
		super(biome, originX, left, 3, 1, 4000);
		biome.switchToBiome(Biome.FIR_FORREST);
	}

}
