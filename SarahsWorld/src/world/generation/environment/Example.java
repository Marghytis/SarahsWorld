package world.generation.environment;

import world.data.Vertex;
import world.generation.Biome;

public class Example extends Environment {

	public Example(Biome biome){
		this(biome.createCenterVertices());
	}
	
	public Example(Vertex[] startVertices) {
		super(startVertices);
	}

}
