package world.generation.zones;

import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;

public class Flat extends Zone{

	double width;
	
	public Flat(BiomeManager biome, double originX, Biome b, double aimWidth) {
		super(biome, originX);
		this.width = aimWidth;
		biome.set(b);
	}

	public double step(double x) {
		if(x >= width){
			end = true;
		}
		return 0;
	}

}
