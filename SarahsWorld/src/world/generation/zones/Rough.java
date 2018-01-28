package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;

public class Rough extends Zone {
	
public static boolean[] description = describe(Attribute.HILLY);
	
	double width;
	double height;
	double offsetByLakes;

	public Rough(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, biome, originX, left, description);
		biome.switchToBiome(Biome.CANDY);
		
		width =  40000 + random.nextInt(10000);
		
		subZone = new Hills(random, biome, 0, left, 0, 3, 1, width, description);
	}
	
	public double step(double x) {
		//Change biome / subZone
		if(subZone instanceof Hills && subZone.end) end = true;

		if(subZone instanceof Hills && ((Hills)subZone).reachedP2){
			subZone = new Hills(random, biome, x, left, subZone.ownHeight, 2, 1, width - x, description);
		}

		return ownHeight;
	}
}