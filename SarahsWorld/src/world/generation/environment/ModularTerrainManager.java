package world.generation.environment;

import java.util.*;

import world.data.Vertex;
import world.generation.Biome;
import world.generation.zones.useful.Roughness;

public class ModularTerrainManager {
	
	List<Module> activeModules = new ArrayList<>();
	Module[] modules;
	Module staticModule;
	Vertex[] lastVertices;
	
	public ModularTerrainManager(Vertex[] startVertices){
		this.lastVertices = startVertices;
		staticModule = new StaticModule(0,startVertices);
		modules = new Module[startVertices.length];
		activeModules.add(staticModule);
		Arrays.fill(modules, staticModule);
	}

	public double getCollisionY() {
		return modules[0].getCollisionY(0);
	}

	public Vertex[] createVertices() {
		lastVertices = new Vertex[modules.length];
		for(int i = 0; i < modules.length; i++){
			lastVertices[i] = modules[i].createVertex(i);
		}
		return lastVertices;
	}
	
	public void step(){
		activeModules.forEach((m)->step());
	}

	public Biome getBiome() {
		// TODO Auto-generated method stub
		return null;
	}

	abstract class Module {
		int index0;
		public Module(int index0){
			this.index0 = index0;
		}
		public abstract Vertex createVertex(int index);
		public abstract double getCollisionY(int index);
		public abstract void step();
		public abstract int stepsBeforeEnd();
	}
	
	class StaticModule extends Module {

		Vertex[] vertices;
		
		public StaticModule(int index0, Vertex[] vertices){
			super(index0);
			this.vertices = vertices;
		}
		
		public void step(){}
		
		public int stepsBeforeEnd(){
			return 0;
		}
		
		public Vertex createVertex(int index){
			return new Vertex(vertices[index-index0]);
		}
		
		public double getCollisionY(int index){
			return vertices[index - index0].y;
		}
		
		public void change(Vertex vertex, int index){
			vertices[index-index0] = vertex;
		}
	}
	class ModulationModule extends Module {

		
		protected Roughness roughness;
		private Roughness baseTerrain;
		double fixedY;
		double baseHeight;
		double roughHeight;
		Vertex vertex;
		
		public ModulationModule(int index0, Vertex vertex, Roughness baseTerrain, Roughness roughness){
			super(index0);
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
		
		public double getCollisionY(int index){
			checkWrongModuleException(index);
			return baseHeight;
		}
		
		public void change(Vertex vertex, int index){
			checkWrongModuleException(index);
			this.vertex = vertex;
			fixedY = vertex.y;
		}
		
		public void checkWrongModuleException(int index){
			if(index != index0){
				new Exception("You called the wrong terrain module for vertex " + index + ", this ist for index " + index0).printStackTrace();
				System.exit(-1);
			}
		}
	}
}
