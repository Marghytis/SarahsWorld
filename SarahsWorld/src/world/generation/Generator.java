package world.generation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.math.Vec;
import world.data.Column;
import world.data.WorldData;
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
		Biome startBiome = Biome.JUNGLE;
		
		posL = 0;
		posR = 0;
		
		biomeL = new BiomeManager(world, startBiome, true);
		biomeR = new BiomeManager(world, startBiome, false);

		zoneL = new Jungle(random, biomeL, 0, true);
		zoneR = new Jungle(random, biomeR, 0, false);
		
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
			switch(random.nextInt(3)){
				case 0 : zoneR = new Mountains(random, zoneR.biome, posR, false);break;
				case 1 : zoneR = new Meadow(random, zoneR.biome, posR, false);break;
				case 2 : zoneR = new Jungle(random, zoneR.biome, posR, false);break;
			}
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
			zoneL = new Mountains(random, zoneL.biome, -posL, true);
		}
		
		zoneL.stepColumn(nextColumnL);

		world.processNewColumn(nextColumnL, -1, zoneL.description);

		zoneL.spawnThings(nextColumnL.right);
		
		return true;
	}
	
	public boolean extend(int iDir) {
		switch(iDir) {
		case 0 : return extendLeft();
		case 1 : return extendRight();
		default: return false;
		}
	}
}
