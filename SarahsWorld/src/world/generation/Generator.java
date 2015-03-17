package world.generation;

import java.util.Random;

import main.Savable;
import util.math.UsefulF;
import util.math.Vec;
import world.World;
import world.WorldContainer.WorldColumn;
import world.WorldContainer.WorldField;
import world.generation.zones.Flat;
import world.generation.zones.Graveyard;
import world.generation.zones.Meadow;
import world.generation.zones.Mountains;

public class Generator implements Savable{

	World world;
	Random random;
	
	Vec posL;
	Vec posR;

	BiomeManager biomeL;
	BiomeManager biomeR;
	
	public Zone zoneL;
	public Zone zoneR;
	
	public Generator(World world){
		this.world = world;
		this.random = new Random();
	}
	
	public void createStartZones(){
		posL = new Vec();
		posR = new Vec();
		
		biomeL = new BiomeManager(Biome.NORMAL, world);
		biomeR = new BiomeManager(Biome.NORMAL, world);

		zoneL = new Graveyard(biomeL, 0);
		zoneR = new Mountains(biomeR, 0);
	}
	
	public WorldColumn shift(boolean left){
		if(left){
			return shiftZone(true);
		} else {
			WorldColumn out = shiftZone(false);
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
	
	public WorldColumn shiftZone(boolean left){
		Zone z = left ? zoneL : zoneR;
		Vec pos = left ? posL : posR;
		
		z.biome.step();
		
		pos.x += left ? -WorldField.width : WorldField.width;
		pos.y = z.y(UsefulF.abs(pos.x - z.originX));
		
		return z.biome.createColumn(pos.copy(), left);
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}
}