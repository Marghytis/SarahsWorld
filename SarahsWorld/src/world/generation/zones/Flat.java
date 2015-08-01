package world.generation.zones;

import java.util.Random;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;

public class Flat extends Zone{

	double width;
	
	public Flat(Random random, BiomeManager biome, double originX, Biome b, double aimWidth, boolean left) {
		super(random, biome, originX, left, describe());
		this.width = aimWidth;
		biome.switchToBiome(b);
	}

	public double step(double x) {
		if(x >= width){
			end = true;
		}
		return 0;
	}

}
