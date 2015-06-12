package world.generation.zones;

import util.math.Function;
import world.generation.Zone;
import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;

public class Mountains extends Zone {

	Function slope;
	double width;
	double height;

	public Mountains(BiomeManager biome, double originX, boolean left) {
		super(biome, originX, left);
		biome.switchToBiome(Biome.FIR_FORREST);
		
		width =  40000 + random.nextInt(10000);
		height = 20000 + random.nextInt(5000);
		
		slope = Function.cubic2(width, height);
	}

	public double step(double x) {
		//Change biome / subZone
		if(subZone == null || subZone.end){
			if(x >= width){
				end = true;
			} else {
				switch(random.nextInt(2)){
				case 0 :
					double amplifier = ownHeight/height;
					subZone = new Hills(biome, x, left, 100*amplifier + 1, width/30, 20*amplifier + 1);
					break;
				case 1 :
					subZone = new Hills(biome, x, left, 1, 1, width/30);
					break;
				}
			}
		}

		//return height
		if(x < width) {
			ownHeight += slope.f(x);
		}
		return ownHeight;
	}

}
