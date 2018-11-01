package world.generation.environment;

import java.util.ArrayList;
import java.util.List;

import util.Color;
import world.data.Column;
import world.data.Vertex;
import world.generation.Biome;
import world.generation.Spawner;
import world.generation.environment.ModularTerrainManager.Module;

/**
 * An environment is a large area of a world, normally there is one for the right hand side and one for the left.
 * It keeps track of all the variables needed for world generation and can generate columns according to it's current state.
 * @author Mario
 *
 */
public abstract class Environment {
	
	public ModularTerrainManager terrain;
	public List<Module> terrainModules;
	public MaterialManager materials;
	public ThingManager things;
	public SkyManager sky;
	double distanceGenerated = 0;
	
	public Environment(Vertex[] startVertices){
		terrain = new ModularTerrainManager(startVertices);
		terrainModules = new ArrayList<>();
		materials = new MaterialManager();
		things = new ThingManager();
		sky = new SkyManager();
	}
	
	public double getDistanceGenerated() {
		return distanceGenerated;
	}
	
	public boolean[] getDescription() {
		return null;//TODO
	}
	
	public void step(){
		terrain.step();
		things.step();
		sky.step();
		
		distanceGenerated += Column.COLUMN_WIDTH;
	}
	
	public void spawnExtra(List<Spawner> spawners) {
		things.spawnExtra(spawners);
	}
	
	public Column createColumn(){
		
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
		things.populate(column);
		
		return column;
	}
	
}
