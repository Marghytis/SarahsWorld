package world.generation.zones;

import java.util.ArrayList;
import java.util.List;

import util.math.Function;
import util.math.Vec;
import world.generation.Zone;
import world.worldGeneration.Biome;
import world.worldGeneration.BiomeManager;

public class Candy extends Zone {

	double width;
	
	Vec[] interpolationPoints;
	int interpolationIndex;
	Function curve;
	
	public Candy(BiomeManager biome, double originX) {
		super(biome, originX);
		biome.switchToBiome(Biome.CANDY);

		width =  10000 + random.nextInt(5000);
		
		List<Vec> points = new ArrayList<>();
		double x = 0;
		points.add(new Vec());

		int widthPar = 500;
		int widthVar = 300;
		int heightVar = 1000;
		
		while(x < width){
			x += widthPar + (random.nextInt(2*widthVar)-widthVar);
			points.add(new Vec(x, random.nextInt(2*heightVar)-heightVar));
		}
		this.width = x;
		
		interpolationPoints = points.toArray(new Vec[points.size()]);
	}
	
	public double step(double x) {
		//end zone
		if(x >= width){
			end = true;
		}
		
		//interpolate hills
		if(x >= interpolationPoints[interpolationIndex].x && interpolationIndex + 1 < interpolationPoints.length){
			interpolationIndex++;
			Vec delta = interpolationPoints[interpolationIndex].minus(interpolationPoints[interpolationIndex - 1]);
			curve = Function.parabola2(delta.x, delta.y);
		}
		
		//return dy
		double dy = 0;
		if(x < width){
			dy = curve.f(x - interpolationPoints[interpolationIndex - 1].x);
		}
		return dy;
	}
}