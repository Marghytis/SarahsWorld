package world.generation;

import java.util.Random;

import main.Savable;
import util.math.UsefulF;
import util.math.Vec;
import world.WorldContainer.WorldField;
import world.generation.zones.Desert;
import world.generation.zones.Flat;
import world.generation.zones.Meadow;
import world.generation.zones.Mountains;
import world.worldGeneration.Biome;
import world.worldGeneration.World;
import world.worldGeneration.WorldData.Vertex;

public class Generator implements Savable {

	World world;
	Random random;
	
	Vec posL;
	Vec posR;

	world.worldGeneration.BiomeManager biomeL;
	world.worldGeneration.BiomeManager biomeR;
	
	public Zone zoneL;
	public Zone zoneR;
	
	public Generator(World world){
		this.world = world;
		this.random = new Random();
	}
	
	public void createStartZones(){
		posL = new Vec();
		posR = new Vec();
		
		biomeL = new world.worldGeneration.BiomeManager(world.data, Biome.NORMAL);
		biomeR = new world.worldGeneration.BiomeManager(world.data, Biome.NORMAL);

		zoneL = new Desert(biomeL, 0);
		zoneR = new Mountains(biomeR, 0);
	}
	
	public Vertex[] shift(boolean left){
		if(left){
			Vertex[] out = shiftZone(true);
			if(zoneL.end){
				switch(random.nextInt(3)){
				case 0 : zoneL = new Desert(biomeL, posL.x); break;
				case 1 : zoneL = new Meadow(biomeL, posL.x); break;
				case 2 : zoneL = new Mountains(biomeL, posL.x); break;
				}
			}
			return out;
		} else {
			Vertex[] out = shiftZone(false);
			if(zoneR.end){
				switch(random.nextInt(3)){
				case 0 : zoneR = new Flat(biomeR, posR.x, Biome.NORMAL, 10000); break;
				case 1 : zoneR = new Meadow(biomeR, posR.x); break;
				case 2 : zoneR = new Mountains(biomeR, posR.x); break;
				}
			}
			return out;
		}
	}
	
	public Vertex[] shiftZone(boolean left){
		Zone z = left ? zoneL : zoneR;
		Vec pos = left ? posL : posR;
		
		z.biome.step();
		
		pos.x += left ? -WorldField.width : WorldField.width;
		pos.y = z.y(UsefulF.abs(pos.x - z.originX));
		
		return z.biome.createVertices(pos.y);
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}
}