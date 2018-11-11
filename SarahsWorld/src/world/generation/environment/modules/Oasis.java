package world.generation.environment.modules;

import world.data.Vertex;

public class Oasis extends Module {

	boolean active;
	
	public Oasis(int index0, int nVertices, boolean collision) {
		super(index0, nVertices, true);
	}
	
	protected void start() {
		active = true;
	}

	public Vertex createVertex(int index) {
		return null;
	}

	public double getCollisionY() {
		return 0;
	}

	public void step() {
		
	}

	public int stepsBeforeEnd() {
		return 0;
	}

}
