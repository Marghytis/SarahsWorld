package world.generation.zones;

import world.generation.BiomeManager;
import world.generation.Zone;

public class WaterColumns extends Zone{

	public WaterColumns(BiomeManager biome, double originX, boolean left) {
		super(biome, originX, left);
	}

	public double deltaY() {
		return 0;
	}

	@Override
	public double step(double x) {
		// TODO Auto-generated method stub
		return 0;
	}

}
