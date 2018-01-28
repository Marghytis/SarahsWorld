package world.generation.zones.useful;

import java.util.Random;

public class EvenedModulation extends Modulation {
	
	public double[] evening;
	public int index = 0;
	public double mean = 0;
	
	public EvenedModulation(Random rand, int wavelength, double amplitude){
		super(rand, wavelength, amplitude);
		this.rand = rand;
		this.evening = new double[wavelength];
	}
	
	public double next(){
		evening[index] = (2*rand.nextDouble()-1)*amplitude;
		mean = 0;
		for(int i = 0; i < evening.length; i++){
			mean += evening[i];
		}
		mean = mean/wavelength;
		evening[index] = mean;
		index++;
		index %= wavelength;
		return mean;
	}
}
