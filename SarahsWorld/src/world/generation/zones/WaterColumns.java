package world.generation.zones;

import java.util.Random;

import world.generation.*;

public class WaterColumns extends Zone {

	public WaterColumns(Random rand, BiomeManager biome, double originX, boolean left) {
		super(rand, null, biome, originX, left, describe());
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
