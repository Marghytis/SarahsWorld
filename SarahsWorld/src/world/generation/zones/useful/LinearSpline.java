package world.generation.zones.useful;

import util.math.Vec;

public class LinearSpline {
	
	protected Vec start, end;
	protected double a;//y = a + b x
	protected double b;
	
	public LinearSpline(Vec start){
		this.start = start;
		this.end = new Vec();
		a = 0;
		b = 0;
	}
	
	public double getY(double x){
		return a + (b*x);
	}
	
	public double getSlope(double x){
		return b;
	}
	
	public void goTo(double x0, double dx, double dy){
		
		//set the new start and end vectors
		start.x = x0;
		start.y = getY(x0);
		end.x = start.y + dx;
		end.y = start.y + dy;
		
		//calculate the new parameters
		b = dy/dx;
		a = start.y - (b*x0);
	}
	
	public Vec getStart(){
		return start;
	}
	
	public Vec getEnd(){
		return end;
	}
}
