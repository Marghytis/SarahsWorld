package world.generation.zones;

import java.util.Random;

import util.math.Function;
import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;

public class MountainRange extends Zone {

	static double borderWidthByHeight = 1.5;

	Function slope;
	double width;
	double height;
	double borderWidth;
	
	public MountainRange(Random random, BiomeManager biome, double originX, boolean left) {
		super(random, Biome.FIR_FORREST, biome, originX, left, null);
		
		width = 20000 + (Math.random()*1000);
		height = 5000 + (Math.random()*1000);
		borderWidth = height*borderWidthByHeight;
		
		slope = Function.parabola2(borderWidth, height);
	}
	
	public double step(double x) {
		double dy = 0;
		if(x < borderWidth){
			dy = slope.f(x);
		} else if(x >= width){
			dy = 0;
			end = true;
		} else if(x > width - borderWidth){
			dy = -slope.f(width - x);
		} else {
			dy = 0;
		}
		
		ownHeight += dy;
		ownHeight += Math.sin(x*0.01)*5;//TODO add Mountains
		
		return ownHeight;
	}
}