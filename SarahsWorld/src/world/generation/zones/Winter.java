package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.ZoneAttribute;

public class Winter extends Hills {
	
	public static boolean[] description = describe(ZoneAttribute.TREES, ZoneAttribute.COLD);
	
	public Winter(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, biome, originX, left, 0, 3, 1, 4000, description);
		biome.switchToBiome(Biome.WINTER_FORREST);//TODO
	}
}
