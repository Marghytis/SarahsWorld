package world.generation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import menu.Settings;
import world.data.Column;
import world.data.Dir;
import world.data.WorldData;
import world.generation.Zone.ZoneType;


public class Generator implements GeneratorInterface {
	
	WorldData world;
	Random random = new Random();
	
//	double posL, posR;
	double[] positions = {0,0};

	BiomeManager biomeL;
	BiomeManager biomeR;
	
	private Zone[] zones = {null, null};
//	private Zone zoneL, lastZoneL;
//	private Zone zoneR, lastZoneR;
	
//	Column nextColumnR, nextColumnL, columnR, columnL;
	private Column[] nextColumns = {null, null};

	public List<Spawner> questThings = new ArrayList<>();
	
	public Generator(WorldData world){
		this.world = world;
		
		ZoneType startZone = newZoneType();
		
		Biome startBiome = startZone.startBiome;

		positions[Dir.l] = 0;
		positions[Dir.r] = 0;
		
		biomeL = new BiomeManager(startBiome, true);
		biomeR = new BiomeManager(startBiome, false);

		zones[Dir.l] = startZone.supply.get(random, biomeL, 0, true);
		zones[Dir.r] = startZone.supply.get(random, biomeR, 0, false);
		
		world.addFirst(startBiome, biomeR.createVertices(0));
	}

	public Generator(WorldData world, DataInputStream input) {
		this.world = world;
		//TODO
	}
	
	public boolean borders(double d, double e) {
		while(positions[Dir.r] < e){
			if(!extend(Dir.r))
				return false;
		}
		while(positions[Dir.l] > d){
			if(!extend(Dir.l))
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

	@Override
	public boolean extend(int iDir) {
		Zone oldZone = zones[iDir];
		positions[iDir] += Dir.s[iDir]*Column.COLUMN_WIDTH;
//		columnR = nextColumnR;

		//if the current zone has ended, start a new one
		if(zones[iDir].end){
			zones[iDir] = newZone(zones[iDir].biomeManager, Dir.s[iDir]*positions[iDir], iDir == Dir.l);
		}
		//create next column
		nextColumns[iDir] = zones[iDir].nextColumn(Dir.s[iDir]*positions[iDir] - zones[iDir].originX);

		zones[iDir].setLastColumn(nextColumns[iDir]);

		//add new Column to the world, spawn quests, things, etc.
		world.processNewColumn(nextColumns[iDir], Dir.s[iDir], zones[iDir].description);
		Column columnToSpawnThings = iDir == Dir.r ? nextColumns[iDir].left() : nextColumns[iDir];
		oldZone.spawnThings(columnToSpawnThings);
		
		return true;
	}

	public boolean extendLeft() {

		return true;
	}
	
	public Zone newZone(BiomeManager biomeM, double originX, boolean left) {
		ZoneType startZone = newZoneType();
		return startZone.supply.get(random, biomeM, originX, left);
	}
	
	public ZoneType newZoneType() {
		ZoneType zoneType;
		if("RANDOM".equals(Settings.getString("ZONETYPE")))
			zoneType = ZoneType.values()[random.nextInt(ZoneType.values().length)];
		else
			zoneType = ZoneType.valueOf(Settings.getString("ZONETYPE"));
		
		return zoneType;
	}
	
	@Override
	public boolean extendRight() {
		// TODO Auto-generated method stub
		return false;
	}
}
