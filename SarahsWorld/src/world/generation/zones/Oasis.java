package world.generation.zones;

import java.util.Random;

import things.ThingType;
import util.math.Function;
import util.math.UsefulF;
import world.WorldData.Column;
import world.generation.Biome;
import world.generation.BiomeManager;
import world.generation.Zone;

public class Oasis extends Zone {
	
	public static boolean[] description = describe(Attribute.MOIST, Attribute.HOT, Attribute.TREES, Attribute.LAKES);

	/**Start and end points of curve segment*/
	double xStart, yStart, xNearShore, xFarShore, xEnd;
	Function curve;
	int part = 0;
	
	public Oasis(Random random, BiomeManager biome, double originX, double originY, boolean left) {
		super(random, biome, originX, left, description);
		biome.switchToBiome(Biome.OASIS);
		
		xStart = 0;
		yStart = originY;
		xNearShore = 1000;
		xFarShore = 2000;//roughly too
		xEnd = 3000;//roughly, may get changed again later
		
		curve = (x) -> UsefulF.cubicUnit.f(x/1000)*(-100);
	}
	boolean startingPart = true, endingPart1 = false, endingPart2 = false;
	public double step(double x){
		
		
		if(part == 0){//SLOPE DOWN
			ownHeight = curve.f(x - xStart) + yStart;
			if(startingPart) startingPart = false;
			if(x >= xNearShore - Column.step){
				part = 1;
				startingPart = true;
			}
		} else if(part == 1){//LAKE
			if(startingPart){
				biome.ants[1].resize(0, 0, 2);
				biome.ants[2].resize(0, 0, 5);
				startingPart = false;
			}
			if(subZone == null){
				subZone = new Lake(random, biome, x, 500, ownHeight, left, Biome.LAKE_DESERT);
				ownHeight = 0;
			} else if(!endingPart1 && x - subZone.originX > 725){
				endingPart1 = true;
				biome.ants[2].resize(Biome.OASIS.stratums[2], 5);
			} else if(!endingPart2 && x - subZone.originX > 800){
				endingPart2 = true;
				biome.ants[1].resize(Biome.OASIS.stratums[1], 2);
			}
			if(subZone.end){
				xEnd = x + 1000;
				xFarShore = x;
				curve = (x1) -> UsefulF.cubicUnit.f(x1/1000)*100;
				xStart = x;
				part = 2;
				biome.ants[1].resize(0, 0, Biome.OASIS.stratums[1].sizingSpeed);
			}
		} else {//SLOPE UP
			ownHeight = curve.f(x - xStart) + yStart - 100;
			if(subZone != null){
				subZone = null;
			}
			if(x + Column.step >= xEnd){
				end = true;
			}
		}
		if( (x >= xNearShore - Column.step && x <= xNearShore + Column.step) ||
			(x >= xFarShore - Column.step && x <= xFarShore + Column.step)){
			biome.extraSpawns.add(ThingType.TREE_PALM.defaultSpawner);
			biome.extraSpawns.add(ThingType.TREE_PALM.defaultSpawner);
		}
		
		//return dy
		return ownHeight;
	}
}
