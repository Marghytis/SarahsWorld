package world.generation.zones;

import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;


public class Desert extends Hills {

	public Desert(BiomeManager biome, double originX, boolean left) {
		super(biome, originX, left, 0, 3, 1, 4000);
		biome.switchToBiome(Biome.DESERT);
	}

}
