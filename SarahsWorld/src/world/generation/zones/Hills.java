package world.generation.zones;

import util.math.Function;
import util.math.UsefulF;
import util.math.Vec;
import world.generation.Zone;
import world.worldGeneration.BiomeManager;
import world.worldGeneration.WorldData.Column;

public class Hills extends Zone {

	double aimWidth;
	int partWidth;
	int widthVariance;
	int heightVariance;
	
	/**Start and end points of curve segment*/
	Vec p1, p2;
	Function curve;
	boolean last;
	
	public Hills(BiomeManager biome, double originX, boolean left, double startHeight, double amplifierX, double amplifierY, double aimWidth) {
		super(biome, originX, left);
		
		this.aimWidth = aimWidth;

		partWidth = (int)(200*amplifierX);
		widthVariance = (int)(40*amplifierX);
		heightVariance = (int)(30*amplifierY);
		
		p1 = new Vec(0, startHeight);
		p2 = new Vec(0, startHeight);
		
		curve = next(nextDelta());
	}
	
	public void setAmplifier(double ampX, double ampY){
		partWidth = (int)(200*ampX);
		widthVariance = (int)(40*ampX);
		heightVariance = (int)(30*ampY);
	}
	
	public boolean reachedP2;
	
	public double step(double x) {
		ownHeight = curve.f(x - p1.x) + p1.y;
		reachedP2 = false;
		
		//interpolate hills
		if(x + Column.step >= p2.x){
			
			if((p1.x + p2.x)/2 > aimWidth){
				if(last){
					end = true;
				} else {
					curve = next(new Vec(partWidth, -p2.y));
					last = true;
				}
			} else {
				curve = next(nextDelta());
				reachedP2 = true;
			}
		}
		
		//return dy
		return ownHeight;
	}
	
	public Function next(Vec d){
		p1.set(p2);
		p2.shift(d);
		return (x) -> UsefulF.cubicUnit.f(x/d.x)*d.y;
	}
	
	public Vec nextDelta(){
		double dx =	partWidth + (random.nextInt(2*widthVariance) - widthVariance);
		double dy = (random.nextInt(2*heightVariance) - heightVariance) - p2.y;
		return new Vec(dx, dy);
	}
}