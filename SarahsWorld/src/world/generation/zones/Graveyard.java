package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;


public class Graveyard extends Zone {

public static boolean[] description = describe(Attribute.LONELY, Attribute.MOIST, Attribute.TREES, Attribute.FLAT);
	
	double width;
	double offsetX;

	public Graveyard(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, biome, originX, left, description);
		biome.switchToBiome(Biome.GRAVEYARD);
		
		width =  4000 + random.nextInt(1000);
		
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
