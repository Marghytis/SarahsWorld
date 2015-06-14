package world.generation.zones;

import world.generation.Zone;
import world.worldGeneration.BiomeManager;

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
