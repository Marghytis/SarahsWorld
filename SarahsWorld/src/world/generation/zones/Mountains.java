package world.generation.zones;

import java.util.Random;

import util.math.Function;
import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;

public class Mountains extends Zone {

	public static boolean[] description = describe(Attribute.LAKES,Attribute.TREES, Attribute.LONELY);
	
	Function f;
	double width;
	double height;
	double offsetByLakes;

	public Mountains(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, Biome.FIR_FORREST, biome, originX, left, description);
		
		width =  40000 + random.nextInt(10000);
		height = 1000 + random.nextInt(1000);
		
		double wH = width/2.0;
		double h = height;
		double a = h/Math.pow(wH, 4);
		double b = (2*h)/Math.pow(wH, 2);
		f = (x) -> (a*Math.pow(x - wH, 4)) - (b*Math.pow(x - wH, 2)) + h;

		subZone = new Hills(random, biome, 0, left, 0, 3, 1, width, description);
	}
	
	public double step(double x) {
		//Change biome / subZone
		if(subZone instanceof Hills && subZone.end) end = true;
		
		if(subZone instanceof Hills && ((Hills)subZone).reachedP2){
			if(random.nextInt(100) < 50){
				subZone = new Lake(random, biomeManager, x, 500, subZone.ownHeight, left);
			}
		}
		
		double amplifier = ownHeight/height;
		if(subZone instanceof Hills){
			((Hills)subZone).setAmplifier(2*amplifier + 3, 20*amplifier + 1);
		} else if(subZone.end){
			offsetByLakes += x - subZone.originX;
			width += x - subZone.originX;
			subZone = new Hills(random, biomeManager, x, left, subZone.ownHeight, 2*amplifier + 3, 20*amplifier + 1, width - x, description);
		}
		
		//return height
		if(x < width && !(subZone instanceof Lake)) {//
			ownHeight = f.f(x - offsetByLakes);
		}

		return ownHeight;
	}
}
