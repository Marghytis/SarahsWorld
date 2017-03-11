package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;

public class Ocean extends Zone {
public static boolean[] description = describe(Attribute.MOIST);
	
	double aimWidth;
	
	public Ocean(Random random, BiomeManager biome, double originX, boolean left, double startHeight, double amplifierX, double amplifierY, double aimWidth) {
		super(random, biome, originX, left, description);
		biome.switchToBiome(Biome.OCEAN);
		
		this.aimWidth = aimWidth;
	}
	
	public double step(double x) {
		if(x >= aimWidth)
			end = true;
		//return dy
		return ownHeight;
	}
}
