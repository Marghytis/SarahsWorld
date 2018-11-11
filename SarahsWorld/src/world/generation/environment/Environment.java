package world.generation.environment;

import java.util.ArrayList;
import java.util.List;

import util.Color;
import world.data.Column;
import world.data.Vertex;
import world.generation.Biome;
import world.generation.Zone.Attribute;
import world.generation.environment.modules.Module;

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
	boolean[] description;
	
	public Environment(Vertex[] startVertices, boolean[] description){
		terrain = new ModularTerrainManager(startVertices);
		terrainModules = new ArrayList<>();
		materials = new MaterialManager();
		things = new ThingManager();
		sky = new SkyManager();
		this.description = description;
	}

	public void addModule(Module module) {
		terrain.addModule(module);
	}
	
	public Vertex[] getLastVertices() {
		return terrain.lastVertices;
	}
	
	public double getDistanceGenerated() {
		return distanceGenerated;
	}
	
	public boolean[] getDescription() {
		return description;
	}
	
	public void step(){
		terrain.step();
		things.step();
		sky.step();
		
		distanceGenerated += Column.COLUMN_WIDTH;
	}

	public Biome getBiome() {
		return null;
	}
	
	public Column createColumn(){
		
		//create the vertices and define the biome of this column
		Biome biome = getBiome();
		Vertex[] vertices = terrain.createVertices();
		double collisionHeight = terrain.getCollisionY();
		
		//get top and bottom color of the sky for this column
		Color topColor = sky.getTopColor();
		Color lowColor = sky.getTopColor();
		
		//create the column with the information gathered
		Column column = new Column(0, biome, topColor, lowColor, vertices, collisionHeight);
		
		return column;
	}
	
	public void populate(Column c) {
		things.populate(c);
	}
	
	public static boolean[] describe(Attribute... attributes){
		boolean[] out = new boolean[Attribute.values().length];
		for(Attribute attr : attributes){
			out[attr.ordinal()] = true;
		}
		return out;
	}
	
}
