package things.interfaces;

import things.ThingType;
import util.math.Vec;

public interface BasicThing {
	public void setRotation(double angle);
	
	public ThingType getType();
	public double getSize();
	public double getRotation();
	public Vec pos();
	public double getYOffset();
	public double getYOffsetToBalanceRotation();
}
