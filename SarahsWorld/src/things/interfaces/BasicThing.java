package things.interfaces;

import things.Species;
import util.math.Vec;

public interface BasicThing {
	public void setRotation(double angle);
	
	public Species getType();
	public double getSize();
	public double getRotation();
	public Vec pos();
	public double getYOffset();
	public double getYOffsetToBalanceRotation();
}
