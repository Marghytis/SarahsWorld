package world.generation.zones.useful;

import util.math.*;

public class CubicSpline extends LinearSpline{

	private double c, d;//f(x) = a + b x + c x^2 + d x^3
	private double xDest;
	
	public CubicSpline(Vec start){
		super(start);
		c = 0;
		d = 0;
	}
	
	public boolean reachedPoint(double x){
		return x >= xDest;
	}
	
	public double getY(double x){
		return a + (b*x) + (c*x*x) + (d*x*x*x);
	}
	
	public double getSlope(double x){
		return b + (c*x) + (d*x*x);
	}
	
	public void goTo(double x, double dx, double dy){
		xDest = x + dx;
		double y0 = getY(x);
		double s0 = 0;//getSlope(x);
		start.set(x, y0);
		end.set(start).shift(dx, dy);
		
		double aTemp = 0;
		double bTemp = s0;
		double cTemp = (3*dy/dx - (2*s0))/dx;
		double dTemp = (s0 - (2*dy/dx))/(dx*dx);
		
		a = aTemp - (1*bTemp*x) + (1*cTemp*x*x) - (dTemp*x*x*x) + y0;
		b = bTemp - (2*cTemp*x) + (3*dTemp*x*x);
		c = cTemp - (3*dTemp*x);
		d = dTemp;
	}
}
