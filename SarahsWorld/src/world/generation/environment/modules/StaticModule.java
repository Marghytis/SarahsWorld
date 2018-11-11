package world.generation.environment.modules;

import world.data.Vertex;
import world.generation.Material;

public class StaticModule extends Module {

	Vertex[] vertices;
	int iCollisionRel;

	public StaticModule(int index0, Vertex[] vertices){
		this(index0, vertices, -1);
	}
	public StaticModule(int index0, Vertex[] vertices, int iCollisionRel){
		super(index0, vertices.length, iCollisionRel != -1);
		this.vertices = vertices;
		this.iCollisionRel = iCollisionRel;
	}
	
	public void step(){
		//remove empty vertices
		double yAbove = vertices[index0].getY();
		for(int i = index0+1; i < nVertices; i++) {
			if(vertices[i].getY() == yAbove) {
				Material[] mats = new Material[Vertex.maxMatCount];
				double[] alphas = new double[Vertex.maxMatCount];
				alphas[0] = 1;
				mats[0] = Material.AIR;
				mats[1] = Material.AIR;
				mats[2] = Material.AIR;
				mats[3] = Material.AIR;
				vertices[i] = new Vertex(i, mats, alphas, 0, 0, 0, yAbove);
			}
		}
		
	}
	
	public int stepsBeforeEnd(){
		return 0;
	}
	
	public Vertex createVertex(int index){
		return new Vertex(vertices[index-index0]);
	}
	
	public void update(Vertex[] vertices) {
		this.vertices = vertices;
	}
	
	public double getCollisionY(){
		if(iCollisionRel == -1) {
			new Exception("You called the wrong terrain module for the collision y").printStackTrace();
			System.exit(-1);
		}
		return vertices[iCollisionRel - index0].y;
	}
	
	public void change(Vertex vertex, int index){
		vertices[index-index0] = vertex;
	}
}