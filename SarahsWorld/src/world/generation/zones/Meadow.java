package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;


public class Meadow extends Hills {
	
	Attribute[] descr = {Attribute.HILLY, Attribute.EVELYN, Attribute.TREES};

	public Meadow(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, Biome.MEADOW, biome, originX, left, 0, 3, 1, 4000, null);
	}
}
