package world.data;

import java.util.Random;

import things.*;
import util.Color;
import util.math.Vec;
import world.generation.Biome;

public class Column implements ListElement<Column> {
	public static final double COLUMN_WIDTH = 20;
	public Column left, right;
	public Vertex[] vertices;
	public Thing[] things;//these are the anchors. may be null
	public int xIndex;
	public double xReal;
	private Vertex topSolidVertex, topFluidVertex;
	private double collisionYSolid, collisionYFluid;
	public Biome biome;
	public Color topColor, lowColor;
	public int testInt;

	public Column(int xIndex, Biome biome, Color top, Color low, Vertex[] vertices, double... collisionVecs){
		this.xIndex = xIndex;
		this.xReal = xIndex*COLUMN_WIDTH;
		this.biome = biome;
		this.topColor = top;
		this.lowColor = low;
		this.vertices = vertices;
		for(Vertex v : vertices){
			v.parent = this;
		}
		setCollisionVecs(collisionVecs);
		this.things = new Thing[ThingType.types.length];
	}
	
	private void setCollisionVecs(double[] collisionVecs){
		topSolidVertex = findTopSolidVertex(vertices);
		topFluidVertex = findTopFluidVertex(vertices);
		if(collisionVecs.length >= 1){
			this.collisionYSolid = collisionVecs[0];
			if(collisionVecs.length >= 2){
				this.collisionYFluid = collisionVecs[1];
			} else {
				this.collisionYFluid = collisionVecs[0];
			}
		} else {
			this.collisionYSolid = topSolidVertex.y;
			this.collisionYFluid = topFluidVertex.y;
		}
		
	}
	
	public void setX(int xIndex){
		this.xIndex = xIndex;
		this.xReal = xIndex*COLUMN_WIDTH;
	}
	
	public static Vertex findTopSolidVertex(Vertex[] vertices){
		int i = 0; while(i < Biome.layerCount - 1 && (vertices[i].empty() || vertices[i].averageSolidity <= 1))
			i++;
		if(i == Biome.layerCount - 1) i = -1;
		return vertices[i];
	}
	
	public static Vertex findTopFluidVertex(Vertex[] vertices){
		int i = 0;
			while(i < Biome.layerCount - 1 && (vertices[i].empty() || vertices[i].averageSolidity < 1))
				i++;
		if(i == Biome.layerCount - 1) i = -1;
		return vertices[i];
	}
	
	public Column getRandomTopLocation(Random random, Vec posField){
		topSolidVertex = findTopSolidVertex(vertices);
		double fac = random.nextDouble();
		posField.set(
				xReal + (fac*(right.xReal - xReal)),
				topSolidVertex.y + (fac*(right.vertices[topSolidVertex.yIndex].y - topSolidVertex.y)));
		return this;
	}
	
	public Column getRandomTopLocation(Random random, Vec posField, int dir){
		topSolidVertex = findTopSolidVertex(vertices);
		double fac = random.nextDouble();
		if(dir == -1) {
			posField.set(
					xReal + (fac*(right.xReal - xReal)),
					topSolidVertex.y + (fac*(right.vertices[topSolidVertex.yIndex].y - topSolidVertex.y)));
		} else if(dir == 1) {
			posField.set(
					xReal + (fac*(left.xReal - xReal)),
					topSolidVertex.y + (fac*(left.vertices[topSolidVertex.yIndex].y - topSolidVertex.y)));
		} else {
			new Exception("Unknown direction!").printStackTrace();
		}
		return this;
	}
	
	/**
	 * Disconnects the thing by itself
	 * @param t
	 */
	public void add(Thing t){
		int o = t.type.ordinal;
		t.prev = null;
		t.next = things[o];
		if(things[o] != null) things[o].prev = t;
		things[o] = t;
		t.link = this;
		t.oldLink = this;
	}
	public void appear(boolean left){
		for(int i = 0; i < things.length; i++){
			for(Thing t = things[i]; t != null; t = t.next){
				t.setVisible(true);
			}
		}
		biome.spawnEffects(this, left);
	}
	
	public void disappear(){
		for(int i = 0; i < things.length; i++){
			for(Thing t = things[i]; t != null; t = t.next){
				t.setVisible(false);
			}
		}
	}
	
	public Vec getTopLine(Vec topLine){
		return topLine.set(right.xReal - xReal, right.vertices[topSolidVertex.yIndex].y - topSolidVertex.y);
	}
	
	public Vec getCollisionLine(Vec topLine){
		return topLine.set(right.xReal - xReal, right.getCollisionY() - getCollisionY());
	}
	public final Vertex getTopSolidVertex(){
		return topSolidVertex;
	}
	public final Vertex getTopFluidVertex(){
		return topFluidVertex;
	}
	public final double getCollisionY(){
		return collisionYSolid;
	}
	public final double getCollisionYFluid(){
		return collisionYFluid;
	}

	public Column next(int index) {
		switch(index) {
		case Dir.l : return left;
		case Dir.r : return right;
		default: return null;
		}
	}

	public Column next() {
		return next(Dir.r);
	}

}
