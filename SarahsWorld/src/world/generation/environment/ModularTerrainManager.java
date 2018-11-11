package world.generation.environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import world.data.Vertex;
import world.generation.environment.modules.Module;
import world.generation.environment.modules.StaticModule;

public class ModularTerrainManager {
	
	Set<Module> activeModules = new HashSet<>();
	Stack<Module> collisionModules = new Stack<>();
	List<Module> done = new ArrayList<>();//local
	Module[] modules;
	StaticModule staticModule;
	int collisionModule;
	Vertex[] lastVertices;
	
	public ModularTerrainManager(Vertex[] startVertices){
		this.lastVertices = startVertices;
		staticModule = new StaticModule(0,startVertices);
		modules = new Module[startVertices.length];
		Arrays.fill(modules, staticModule);
		activeModules.add(staticModule);
		collisionModules.add(staticModule);
	}

	public double getCollisionY() {
		return collisionModules.peek().getCollisionY();
	}
	
	public void addModule(Module module) {
		//add to active modules list
		activeModules.add(module);
		//if wanted, add to modules list for collision y
		if(module.collision())
			collisionModules.add(module);
		
		//add to layer array of modules
		for(int i = module.getIndex0(); i < module.getIndex0() + module.getNVertices(); i++) {
			modules[i] = module;
		}
	}
	
	public void removeModule(Module module) {
		activeModules.remove(module);
		collisionModules.remove(module);
		
		Module follower = module.getFollower();
		for(int i = module.getIndex0(); i < module.getIndex0() + module.getNVertices(); i++) {
			modules[i] = follower;
		}
	}

	public Vertex[] createVertices() {
		lastVertices = new Vertex[modules.length];
		for(int i = 0; i < modules.length; i++){
			lastVertices[i] = modules[i].createVertex(i);
		}
		//check for line crossings and align with the highest layer if needed
		double nextHigherY = Double.MAX_VALUE;
		for(int i = 0; i < modules.length-1; i++) {
			if(lastVertices[i].getY() > nextHigherY) {
				lastVertices[i].setNewY(nextHigherY);
			}
			if(!lastVertices[i].empty()) {
				nextHigherY = lastVertices[i].getY();
			}
		}
		return lastVertices;
	}
	
	public void step(){
		activeModules.forEach(m->{
			if(m.done())
				done.add(m);
			m.step();
		});
		done.forEach( m -> {
			removeModule(m);
		});
	}
	
}
