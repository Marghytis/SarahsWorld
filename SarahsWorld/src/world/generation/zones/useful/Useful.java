package world.generation.zones.useful;

import java.util.Random;

import world.data.Column;
import world.generation.*;

public class Useful extends Zone2 {

	public static boolean[] description = describe(ZoneAttribute.HILLY);
	
	protected Roughness roughness;
	private Roughness baseTerrain;
	double baseHeight;
	
	double aimWidth;
	
	public Useful(Random random, Biome startBiome, BiomeManager biome, double originX, double aimWidth, double startHeight, boolean left, ZoneAttribute... attribs) {
		super(random, startBiome, biome, originX, left, attribs.length > 0 ? describe(attribs) : Useful.description);
		
		this.aimWidth = aimWidth;
		this.ownHeight = startHeight;
		
		roughness = new Roughness(new EvenedModulation(random, 4, 10));
		baseTerrain = new Roughness(new LinearSplineModulation(random, 6, 20), new SplineModulation(random, 40, 100, true));
		
		startNextSection(originX);
		baseHeight = ownHeight;
	}
	
	public double getHeight(double x){
		baseHeight = ownHeight + baseTerrain.next();
		return baseHeight + roughness.next();// + spline.getY(x) 
	}
	
	public double getDefinitionEnd() {
		return Double.MAX_VALUE;
	}

	public void startNextSection(double x) {
		
	}
	
	protected Column createColumn(double x){
		return biomeManager.createColumn(lastHeight, baseHeight);
	}
}