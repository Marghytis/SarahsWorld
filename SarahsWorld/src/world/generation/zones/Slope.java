package world.generation.zones;

import java.util.Random;

import world.generation.BiomeManager;
import world.generation.Zone;
import world.generation.ZoneAttribute;

public class Slope extends Zone {

	public static boolean[] description = describe(ZoneAttribute.HILLY);
	
	double aimWidth;
	int partWidth;
	int widthVariance;
	int heightVariance;
	
	/**Start and end points of curve segment*/
	
	public Slope(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, null, biome, originX, left, Slope.description);
		
		this.aimWidth = 10000000;
	}
	
	public double step(double x) {
		if(x >= aimWidth){
			end = true;
		}
		ownHeight = (left ? 1 : -1)*1000000*x/aimWidth;
		return ownHeight;
	}
}