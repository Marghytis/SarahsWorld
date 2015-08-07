package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;


public class Meadow extends Hills {

	public Meadow(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, biome, originX, left, 0, 3, 1, 40000, null);
		biome.switchToBiome(Biome.MEADOW);
	}
}
