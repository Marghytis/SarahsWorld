package world.generation.zones.useful;

import java.util.Random;

public class LinearSplineModulation extends Modulation {

	int stepCount;
	double stepSize;
	double value;
	
	public LinearSplineModulation(Random rand, int wavelength, double amplitude) {
		super(rand, wavelength, amplitude);
		nextSection();
	}

	public double next() {
		if(stepCount == 0){
			nextSection();
		}
		value += stepSize;
		stepCount--;
		return value;
	}
	
	private void nextSection(){
		stepSize = ((2*rand.nextDouble()-1)*amplitude - value)/wavelength;
		stepCount = wavelength;
	}

}
