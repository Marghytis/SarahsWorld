package world.generation.zones;

import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;


public class Graveyard extends Hills {

	public Graveyard(BiomeManager biome, double originX) {
		super(biome, originX, 3, 1, 40000);
		biome.switchToBiome(Biome.GRAVEYARD);
	}
}
