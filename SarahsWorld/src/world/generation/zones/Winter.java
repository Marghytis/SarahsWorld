package world.generation.zones;

import java.util.Random;

import world.generation.*;

public class Winter extends Hills {
	
	public static boolean[] description = describe(Attribute.TREES, Attribute.COLD);
	
	public Winter(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, biome, originX, left, 0, 3, 1, 40000, description);
		biome.switchToBiome(Biome.WINTER_FORREST);//TODO
	}
}
