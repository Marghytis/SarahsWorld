package world.generation.zones.useful;

import java.util.Random;

import util.math.Vec;

public class SplineModulation extends Modulation {
	
	LinearSpline spline;
	int x;
	
	public SplineModulation(Random rand, int wavelength, double amplitude, boolean cubic) {
		super(rand, wavelength, amplitude);
		if(!cubic){
			spline = new LinearSpline(new Vec());
		} else {
			spline = new CubicSpline(new Vec());
		}
		x = 0;
		nextSection();
	}

	public double next() {
		x++;
		if(spline.end.x <= x){
			nextSection();
		}
		return spline.getY(x);
	}
	
	private void nextSection(){
		spline.goTo(x, wavelength, (2*rand.nextDouble()-1)*amplitude);
	}

}
