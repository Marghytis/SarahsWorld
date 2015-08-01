package world.generation.zones;

import world.generation.Biome;
import world.generation.BiomeManager;


public class Graveyard extends Hills {

	public Graveyard(BiomeManager biome, double originX, boolean left) {
		super(biome, originX, left, 3, 1, 40000);
		biome.switchToBiome(Biome.GRAVEYARD);
	}
}
