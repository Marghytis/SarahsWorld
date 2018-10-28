package world.generation.environment;

import java.util.*;

import util.Color;
import world.data.*;
import world.generation.Biome;
import world.generation.environment.ModularTerrainManager.Module;

public abstract class Environment {
	
	public ModularTerrainManager terrain;
	public List<Module> terrainModules;
	public MaterialManager materials;
	public ThingManager things;
	public SkyManager sky;
	
	public Environment(Vertex[] startVertices){
		terrain = new ModularTerrainManager(startVertices);
		terrainModules = new ArrayList<>();
	}
	
	public void step(){
		terrain.step();
		things.step();
		sky.step();
	}
	
	public void createColumn(){
		
		//create the vertices and define the biome of this column
		Biome biome = terrain.getBiome();
		Vertex[] vertices = terrain.createVertices();
		double collisionHeight = terrain.getCollisionY();
		
		//get top and bottom color of the sky for this column
		Color topColor = sky.getTopColor();
		Color lowColor = sky.getTopColor();
		
		//create the column with the information gathered
		Column column = new Column(0, biome, topColor, lowColor, vertices, collisionHeight);
		
		//spawn things on top of the column
		things.fill(column);
	}
	
}
