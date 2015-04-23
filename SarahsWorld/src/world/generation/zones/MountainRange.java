package world.generation.zones;

import util.math.Function;
import util.math.Vec;
import world.generation.Zone;
import world.worldGeneration.BiomeManager;

public class MountainRange extends Zone {

	static double borderWidthByHeight = 1.5;

	Function slope;
	double width;
	double height;
	double borderWidth;
	
	public MountainRange(BiomeManager biome, Vec start) {
		super(biome, start);
		
		width = 20000 + (Math.random()*1000);
		height = 5000 + (Math.random()*1000);
		borderWidth = height*borderWidthByHeight;
		
		slope = Function.parabola2(borderWidth, height);
	}
	
	public double step() {
		double dy = 0;
		if(pos.x < borderWidth){
			dy = slope.f(pos.x);
		} else if(pos.x >= width){
			dy = 0;
			end = true;
		} else if(pos.x > width - borderWidth){
			dy = -slope.f(width - pos.x);
		} else {
			dy = 0;
		}
		
		dy += Math.sin(pos.x*0.01)*5;//TODO add Mountains
		
		return dy;
	}
}