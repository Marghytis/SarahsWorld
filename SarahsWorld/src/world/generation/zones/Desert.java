package world.generation.zones;

import world.generation.Biome;
import world.generation.BiomeManager;

public class Desert extends Hills {

	public Desert(BiomeManager biome, double originX) {
		super(biome, originX, 3, 1, 4000);
		biome.set(Biome.DESERT);
	}

}
