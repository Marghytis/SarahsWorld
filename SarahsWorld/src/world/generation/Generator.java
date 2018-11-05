package world.generation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.math.Vec;
import world.data.Column;
import world.data.Dir;
import world.data.WorldData;
import world.generation.Zone.ZoneType;
import world.generation.zones.Desert;
import world.generation.zones.Jungle;
import world.generation.zones.Meadow;
import world.generation.zones.Mountains;


public class Generator implements GeneratorInterface {
	
	WorldData world;
	Random random = new Random();
	
	double posL, posR;

	BiomeManager biomeL;
	BiomeManager biomeR;
	
	public Zone zoneL;
	public Zone zoneR;
	
	Column nextColumnR, nextColumnL;

	public List<Spawner> questThings = new ArrayList<>();
	
	public Generator(WorldData world){
		this.world = world;
		
//		Biome startBiome = Biome.values()[world.random.nextInt(Biome.values().length)];//TODO make it random
		ZoneType startZone = ZoneType.values()[random.nextInt(ZoneType.values().length)];
		Biome startBiome = startZone.startBiome;
		
		posL = 0;
		posR = 0;
		
		biomeL = new BiomeManager(world, startBiome, true);
		biomeR = new BiomeManager(world, startBiome, false);

		zoneL = startZone.supply.get(random, biomeL, 0, true);
		zoneR = startZone.supply.get(random, biomeR, 0, false);
		
		world.addFirst(startBiome, biomeR.createVertices(0));
	}

	public Generator(WorldData world, DataInputStream input) {
		this.world = world;
		//TODO
	}
	
	Vec questPos = new Vec();
	
	public boolean borders(double d, double e) {
		while(posR < e){
			if(!extendRight())
				return false;
		}
		while(posL > d){
			if(!extendLeft())
				return false;
		}
		return true;
	}

	public void spawnOnce(Spawner spawner) {
		questThings.add(spawner);
	}


	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}

	public boolean extendRight() {
		posR += Column.COLUMN_WIDTH;
		
		nextColumnR = zoneR.nextColumn(posR - zoneR.originX);
//		world.addRight(nextColumnR);
		
		if(zoneR.end){
//			switch(random.nextInt(2)){
//				case 0 : zoneR = new Mountains(random, zoneR.biomeManager, posR, false);break;
//				case 1 : zoneR = new Meadow(random, zoneR.biomeManager, posR, false);break;
//				case 2 : zoneR = new Jungle(random, zoneR.biomeManager, posR, false);break;
//				case 3 : zoneR = new Desert(random, zoneR.biomeManager, posR, false); break;
//			}
//			zoneR = new Jungle(random, zoneR.biome, posR, false);
			zoneR = newZone(zoneR.biomeManager, posR, false);
		}
		zoneR.stepColumn(nextColumnR);

		world.processNewColumn(nextColumnR, 1, zoneR.description);

		zoneR.spawnThings(nextColumnR.left);
		
		return true;
	}

	public boolean extendLeft() {

		posL -= Column.COLUMN_WIDTH;
		
		nextColumnL = zoneL.nextColumn(-posL - zoneL.originX);
//		world.addLeft(nextColumnL);

		if(zoneL.end){
			zoneL = new Mountains(random, zoneL.biomeManager, -posL, true);
		}
		
		zoneL.stepColumn(nextColumnL);

		world.processNewColumn(nextColumnL, -1, zoneL.description);

		zoneL.spawnThings(nextColumnL);
		
		return true;
	}
	
	public Zone newZone(BiomeManager biomeM, double originX, boolean left) {
		ZoneType startZone = ZoneType.values()[random.nextInt(ZoneType.values().length)];
		return startZone.supply.get(random, biomeM, originX, left);
	}
	
	public boolean extend(int iDir) {
		switch(iDir) {
		case Dir.l : return extendLeft();
		case Dir.r : return extendRight();
		default: return false;
		}
	}
}
