package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;


public class Test extends Hills {

	public Test(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, biome, originX, left, 0, 3, 1, 4000, null);
		biome.switchToBiome(Biome.FIR_FORREST);
	}

}
