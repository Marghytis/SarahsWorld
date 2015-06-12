package world.worldGeneration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import util.math.Vec;
import world.generation.Zone;
import world.generation.zones.Desert;
import world.generation.zones.Test;
import world.worldGeneration.WorldData.Column;


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
		
		Biome startBiome = Biome.DESERT;//TODO make it random
		
		posL = new Vec();
		posR = new Vec();
		
		biomeL = new BiomeManager(world, startBiome, true);
		biomeR = new BiomeManager(world, startBiome, false);

		zoneL = new Desert(biomeL, 0, true);
		zoneR = new Desert(biomeR, 0, false);
		
		world.addFirst(startBiome, biomeR.createVertices(0));
	}

	public Generator(WorldData world, DataInputStream input) {
		this.world = world;
		//TODO
	}
	
	public void borders(double d, double e) {
		while(posR.x < e){
			biomeR.step(world.mostRight);
			posR.x += Column.step;
			
			posR.y = zoneR.y(posR.x);
			
			if(zoneR.end){
				zoneR = new Test(biomeR, posR.x, false);
			}
			world.addRight(biomeR.biome, biomeR.createVertices(posR.y));
			world.mostRight.biome.spawnThings(world, world.mostRight.left);
		}
		while(posL.x > d){
			biomeL.step(world.mostLeft);
			posL.x -= Column.step;
			
			posL.y = zoneL.y(posL.x);

			if(zoneL.end){
				zoneL = new Test(biomeL, posL.x, true);
			}
			world.addLeft(biomeL.biome, biomeL.createVertices(posL.y));
			world.mostLeft.biome.spawnThings(world, world.mostLeft.right);
		}
	}

	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
