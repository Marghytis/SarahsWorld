package world.generation.zones;

import world.generation.Biome;
import world.generation.BiomeManager;

public class Graveyard extends Hills {

	public Graveyard(BiomeManager biome, double originX) {
		super(biome, originX, 3, 1, 40000);
		biome.set(Biome.GRAVEYARD);
	}
}
