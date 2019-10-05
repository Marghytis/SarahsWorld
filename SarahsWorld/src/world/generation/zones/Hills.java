package world.generation.zones;

import java.util.Random;

import util.math.Function;
import util.math.UsefulF;
import util.math.Vec;
import world.data.Column;
import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;
import world.generation.ZoneAttribute;

public class Hills extends Zone {

	public static boolean[] description = describe(ZoneAttribute.HILLY);
	
	double aimWidth;
	int partWidth;
	int widthVariance;
	int heightVariance;
	
	/**Start and end points of curve segment*/
	Vec p1, p2;
	Function curve;
	boolean last;
	
	public Hills(Random random, BiomeManager biome, double originX, boolean left, double startHeight, double amplifierX, double amplifierY, double aimWidth, boolean[] description) {
		this(random, null, biome, originX, left, startHeight, amplifierX, amplifierY, aimWidth, description);
	}
	
	public Hills(Random random, Biome startBiome, BiomeManager biome, double originX, boolean left, double startHeight, double amplifierX, double amplifierY, double aimWidth, boolean[] description) {
		super(random, null, biome, originX, left, description != null ? description : Hills.description);
		
		this.aimWidth = aimWidth;

		partWidth = (int)(200*amplifierX);
		widthVariance = (int)(40*amplifierX);
		heightVariance = (int)(30*amplifierY);
		
		p1 = new Vec(0, startHeight);
		p2 = new Vec(0, startHeight);
		
		nextDelta();
		curve = next();
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
		if(x + Column.COLUMN_WIDTH >= p2.x){
			
			if((p1.x + p2.x)/2 > aimWidth){
				if(last){
					end = true;
				} else {
					shift.set(partWidth, -p2.y);
					curve = next();
					last = true;
				}
			} else {
				nextDelta();
				curve = next();
				reachedP2 = true;
			}
		}

		//return dy
		return ownHeight;
	}
	
	public Vec shift = new Vec();
	
	public Function next(){
		p1.set(p2);
		p2.shift(shift);
		return (x) -> UsefulF.cubicUnit.f(x/shift.x)*shift.y;
	}
	
	public void nextDelta(){
		shift.set(partWidth + (random.nextInt(2*widthVariance) - widthVariance),
				(random.nextInt(2*heightVariance) - heightVariance) - p2.y);
	}
}