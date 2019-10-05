package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.ZoneAttribute;

public class Village extends Hills {
	
	public static boolean[] description = describe(ZoneAttribute.TREES);
	
	public Village(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, biome, originX, left, 0, 3, 1, 3000, description);
		biome.switchToBiome(Biome.FIR_VILLAGE);
	}
}
