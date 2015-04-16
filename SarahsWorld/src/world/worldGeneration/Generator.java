package world.worldGeneration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import util.math.Vec;
import world.generation.Zone;
import world.generation.zones.Desert;
import world.generation.zones.Mountains;
import world.objects.ThingType;


public class Generator {
	
	WorldData world;
	Random random = new Random();
	
	Vec posL;
	Vec posR;

	BiomeManager biomeL;
	BiomeManager biomeR;
	
	public Zone zoneL;
	public Zone zoneR;
	
	public Generator(WorldData world){
		this.world = world;
		//TODO fill in the first vertices and things
		posL = new Vec();
		posR = new Vec();
		
		biomeL = new BiomeManager(world, Biome.DESERT);
		biomeR = new BiomeManager(world, Biome.DESERT);

		zoneL = new Desert(biomeL, 0);
		zoneR = new Mountains(biomeR, 0);
		
		world.addFirst(biomeR.createVertices(0));
		world.mostLeft.add(ThingType.SARAH.create(world, world.mostLeft.vertices[0], new Vec(0, world.yTop(0))));
		
	}

	public Generator(WorldData data, DataInputStream input) {
		this.world = data;
		//TODO
	}

	public void borders(double d, double e) {
		// TODO Auto-generated method stub
		
	}

	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
