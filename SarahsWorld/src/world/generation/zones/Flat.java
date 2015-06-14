package world.generation.zones;

import world.generation.Zone;
import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;

public class Flat extends Zone{

	double width;
	
	public Flat(BiomeManager biome, double originX, Biome b, double aimWidth, boolean left) {
		super(biome, originX, left);
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
