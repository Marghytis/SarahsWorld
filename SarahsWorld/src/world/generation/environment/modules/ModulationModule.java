package world.generation.environment.modules;

import world.data.Vertex;
import world.generation.zones.useful.Roughness;

public class ModulationModule extends Module {
	
	protected Roughness roughness;
	private Roughness baseTerrain;
	double fixedY;
	double baseHeight;
	double roughHeight;
	Vertex vertex;
	
	public ModulationModule(int index0, Vertex vertex, Roughness baseTerrain, Roughness roughness){
		super(index0, 1, true);
		this.vertex = vertex;
		this.fixedY = vertex.y;
		this.baseTerrain = baseTerrain;
		this.roughness = roughness;
	}
	
	public void step(){
		baseHeight = fixedY + baseTerrain.next();
		roughHeight = roughness.next();
	}
	
	public int stepsBeforeEnd(){
		return 0;
	}
	
	public Vertex createVertex(int index){
		checkWrongModuleException(index);
		return new Vertex(vertex, baseHeight + roughHeight);
	}
	
	public double getCollisionY(){
		return baseHeight;
	}
	
	public void change(Vertex vertex, int index){
		checkWrongModuleException(index);
		this.vertex = vertex;
		fixedY = vertex.y;
	}
	
	public void checkWrongModuleException(int index){
		if(index != index0){
			new Exception("You called the wrong terrain module for vertex " + index + ", this is for index " + index0).printStackTrace();
			System.exit(-1);
		}
	}
}