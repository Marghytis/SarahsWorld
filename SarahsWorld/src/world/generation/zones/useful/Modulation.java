package world.generation.zones.useful;

import java.util.Random;

public abstract class Modulation {

	protected Random rand;
	int wavelength;
	double amplitude;
	
	public Modulation(Random rand, int wavelength, double amplitude){
		this.rand = rand;
		this.wavelength = wavelength;
		this.amplitude = amplitude;
	}
	
	public abstract double next();
	
}
