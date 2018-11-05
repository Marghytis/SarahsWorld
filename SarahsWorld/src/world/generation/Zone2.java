package world.generation;

import java.util.Random;


public abstract class Zone2 extends Zone {
	
	public Zone2(Random random, Biome startBiome, BiomeManager biome, double originX, boolean left, boolean[] description){
		super(random, startBiome, biome, originX, left, description);
	}
	
	public abstract double getHeight(double x);

	public abstract double getDefinitionEnd();
	
	public abstract void startNextSection(double x);
	
	@Override
	public double step(double x){
		return getHeight(x);
	}

	/**
	 * Updates the height of this zone and returns the complete height
	 * @return the height at x of this zone and all subZones together
	 */
	public double stepOnce(double x){
		x = Math.abs(x);
		
		//if the height function is not defined at values greater than x, the zone is expanded in the positive direction
		if(getDefinitionEnd() <= x){
			startNextSection(x);
		}
		
		biomeManager.step();
		//the input x is relative to originX, for zone2 we need to shift that
		return step(x + originX);
	}
}
