package world.generation.zones;

import util.math.Vec;
import world.generation.BiomeManager;
import world.generation.Zone;

public class WaterColumns extends Zone{

	public WaterColumns(BiomeManager biome, Vec start, boolean left) {
		super(biome, start, left);
	}

	public double deltaY() {
		return 0;
	}

}
