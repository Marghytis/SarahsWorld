package world.generation.zones;

import util.math.Function;
import world.generation.Zone;
import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;

public class Mountains extends Zone {

	Function f;
	double width;
	double height;
	double offsetByLakes;

	public Mountains(BiomeManager biome, double originX, boolean left) {
		super(biome, originX, left);
		biome.switchToBiome(Biome.FIR_FORREST);
		
		width =  40000 + random.nextInt(10000);
		height = 1000 + random.nextInt(1000);
		
		double wH = width/2.0;
		double h = height;
		double a = h/Math.pow(wH, 4);
		double b = (2*h)/Math.pow(wH, 2);
		f = (x) -> (a*Math.pow(x - wH, 4)) - (b*Math.pow(x - wH, 2)) + h;

		subZone = new Hills(biome, 0, left, 0, 3, 1, width);
	}
	
	public double step(double x) {
		//Change biome / subZone
		if(subZone instanceof Hills && subZone.end) end = true;
		
		if(subZone instanceof Hills && ((Hills)subZone).reachedP2){
			if(random.nextInt(100) < 50){
				subZone = new Lake(biome, x, 500, subZone.ownHeight, left);//It's p1, because it has already shifted by now
			}
		}
		
		double amplifier = ownHeight/height;
		if(subZone instanceof Hills){
			((Hills)subZone).setAmplifier(2*amplifier + 3, 20*amplifier + 1);
		} else if(subZone.end){
			offsetByLakes += x - subZone.originX;
			width += x - subZone.originX;
			subZone = new Hills(biome, x, left, subZone.ownHeight, 2*amplifier + 3, 20*amplifier + 1, width - x);
		}
		
		//return height
		if(x < width && !(subZone instanceof Lake)) {//
			ownHeight = f.f(x - offsetByLakes) + random.nextInt(3) - 1;
		}

		return ownHeight;
	}
}
