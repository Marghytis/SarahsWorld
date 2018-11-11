package world.generation.environment.modules;

import world.data.Vertex;

public abstract class Module {
	int index0;
	int nVertices;
	boolean collision;
	public Module(int index0, int nVertices, boolean collision){
		this.index0 = index0;
		this.nVertices = nVertices;
		this.collision = collision;
	}
	public int getIndex0() { return index0; }
	public int getNVertices() {return nVertices; }
	public abstract Vertex createVertex(int index);
	public abstract double getCollisionY();
	public abstract void step();
	public abstract int stepsBeforeEnd();
	public boolean done() {return false;}
	public Module getFollower() {
		return null;
	}
	public boolean collision() {
		return collision;
	}
}