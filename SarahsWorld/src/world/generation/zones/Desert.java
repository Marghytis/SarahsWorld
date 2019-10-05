package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;
import world.generation.ZoneAttribute;


public class Desert extends Zone {

	public static boolean[] description = describe(ZoneAttribute.DRY, ZoneAttribute.HOT, ZoneAttribute.LONELY, ZoneAttribute.FLAT);
	
	double width;
	double offsetX;

	public Desert(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, Biome.DESERT, biome, originX, left, description);
		
		width =  6000 + random.nextInt(1000);
		
		subZone = new Hills(random, biome, 0, left, 0, 3, 1, width, description);
	}
	
	public double step(double x) {
		//Change biome / subZone
		if(subZone instanceof Hills && subZone.end) end = true;
		
		if(subZone instanceof Hills && ((Hills)subZone).reachedP2){
			if(random.nextInt(100) < 5){
				subZone = new Oasis(random, biomeManager, x, subZone.ownHeight, left);
			} else {
				subZone = new Hills(random, biomeManager, x, left, subZone.ownHeight, 2, 1, width - x, description);
			}
		}
		
		if(subZone instanceof Oasis && subZone.end){
			offsetX += x - subZone.originX;
			width += x - subZone.originX;
			biomeManager.switchToBiome(Biome.DESERT);
			subZone = new Hills(random, biomeManager, x, left, subZone.ownHeight, 2, 1, width - x, description);
		}
		
		return ownHeight;
	}
}
