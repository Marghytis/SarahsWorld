package world.generation.environment.modules;

import world.data.Vertex;
import world.generation.zones.useful.Roughness;

public class ModulationModule extends Module {
	
	
	protected Roughness roughness;
	private Roughness baseTerrain;
	private StructureModulation structure;
	double fixedY;
	double baseHeight;
	double roughHeight;
	Vertex vertex;

	public ModulationModule(int index0, Vertex vertex, Roughness baseTerrain, Roughness roughness){
		this(index0, vertex, baseTerrain, roughness, new StructureModulation());
	}
	public ModulationModule(int index0, Vertex vertex, Roughness baseTerrain, Roughness roughness, StructureModulation structure){
		super(index0, 1, true);
		this.vertex = vertex;
		this.fixedY = vertex.y;
		this.baseTerrain = baseTerrain;
		this.roughness = roughness;
		this.structure = structure;
	}
	
	public void step(){
		baseHeight = fixedY + baseTerrain.next() + structure.next();
		roughHeight = roughness.next();
	}
	
	public int stepsBeforeEnd(){
		return 0;
	}
	
	public StructureModulation getStructureMod() {
		return structure;
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