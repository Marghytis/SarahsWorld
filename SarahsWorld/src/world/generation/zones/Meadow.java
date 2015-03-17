package world.generation.zones;

import world.generation.Biome;
import world.generation.BiomeManager;

public class Meadow extends Hills {

	public Meadow(BiomeManager biome, double originX) {
		super(biome, originX, 3, 1, 40000);
		biome.set(Biome.MEADOW);
	}
}
