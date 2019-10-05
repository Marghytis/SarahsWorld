package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.ZoneAttribute;


public class Meadow extends Hills {
	
	ZoneAttribute[] descr = {ZoneAttribute.HILLY, ZoneAttribute.EVELYN, ZoneAttribute.TREES};

	public Meadow(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, Biome.MEADOW, biome, originX, left, 0, 3, 1, 4000, null);
	}
}
