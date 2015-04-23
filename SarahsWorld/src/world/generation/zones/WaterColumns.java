package world.generation.zones;

import util.math.Vec;
import world.generation.Zone;
import world.worldGeneration.BiomeManager;

public class WaterColumns extends Zone{

	public WaterColumns(BiomeManager biome, Vec start, boolean left) {
		super(biome, start, left);
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
