package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;

public class Jungle extends Hills {
	
	public static boolean[] description = describe(Attribute.MOIST, Attribute.HOT, Attribute.TREES);
	
	public Jungle(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, biome, originX, left, 0, 3, 1, 40000, description);
		biome.switchToBiome(Biome.JUNGLE);
	}
}
