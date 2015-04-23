package world.generation.zones;

import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;


public class Desert extends Hills {

	public Desert(BiomeManager biomeL, double originX) {
		super(biomeL, originX, 3, 1, 4000);
		biomeL.switchToBiome(Biome.DESERT);
	}

}
