package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;
import world.generation.ZoneAttribute;

public class Ocean extends Zone {
public static boolean[] description = describe(ZoneAttribute.MOIST);
	
	double aimWidth;
	
	public Ocean(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, Biome.OCEAN, biome, originX, left, description);
		
		this.aimWidth = 4000;
	}
	
	public double step(double x) {
		if(x >= aimWidth)
			end = true;
		//return dy
		return ownHeight;
	}
}
