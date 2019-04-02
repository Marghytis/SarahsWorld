package world.data;

import java.util.Random;

import things.Entity;
import things.Species;
import things.Thing;
import util.Color;
import util.math.Vec;
import world.generation.Biome;

/**
 * Represents a vertical slice of the World. Contains, most importantly, the vertices that make up the terrain and the Thing located in this slice.
 * Every column also defines a Biome and background colors. This class inherits from ColumnListElement to be able to quickly loop through neighboring columns.
 * @author Mario
 *
 */
public class Column extends ColumnListElement {
	
	public static final double COLUMN_WIDTH = 20;
	private Vertex[] vertices;
	/**These are the anchors. may be null.*/
	private Entity[] things;
	private int xIndex;
	private double xReal;
	private Vertex topSolidVertex, topFluidVertex;
	private double collisionYSolid, collisionYFluid;
	private Biome biome;
	private Color topColor, lowColor;

	/**
	 * Constructs a column.
	 * @param xIndex Unique index of this column that also determines it's x-coordinate
	 * @param biome The Biome this column is contained in
	 * @param top Sky color at top of the screen
	 * @param low Sky color at the bottom of the screen
	 * @param vertices Array of Vertex belonging to this slice of the world
	 * @param collisionVecs y-Coordinates for the moving Things to collide with (solid and/or fluid)
	 */
	public Column(int xIndex, Biome biome, Color top, Color low, Vertex[] vertices, double... collisionVecs){
		this.xIndex = xIndex;
		this.xReal = xIndex*COLUMN_WIDTH;
		this.biome = biome;
		this.topColor = top;
		this.lowColor = low;
		this.vertices = vertices;
		for(Vertex v : vertices){
			v.setParent(this);
		}
		setCollisionVecs(collisionVecs);
		this.things = new Thing[Species.types.length];
	}
	
	/**
	 * Determines the y coordinates for solid and fluid collision with things. If coordinates are given, they are used instead.
	 * @param collisionVecs Predetermined coordinates. If only one coordinate is given, it is used for both coordinates.
	 */
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
			this.collisionYSolid = topSolidVertex.y();
			this.collisionYFluid = topFluidVertex.y();
		}
		
	}
	
	/**
	 * Searches the upper most Vertex that's not empty and whose average solidity is geater than 1.
	 * @param vertices
	 * @return The found Vertex. If no Vertex is found, a RuntimeException will be thrown.
	 */
	public Vertex findTopSolidVertex(Vertex[] vertices){
		int i = 0;//start from the top and stop at the first solid vertex.
		while(i < Biome.layerCount - 1 && (vertices[i].empty() || vertices[i].getAverageSolidity() <= 1))
			i++;
		
		if(i == Biome.layerCount - 1) 
			throw new RuntimeException("No solid vertex found!");
		
		return vertices[i];
	}
	
	/**
	 * Searches the upper most Vertex that's not empty and whose average solidity is above or equal to 1.
	 * @param vertices
	 * @return The found Vertex. If no Vertex is found, a RuntimeException will be thrown.
	 */
	public Vertex findTopFluidVertex(Vertex[] vertices){
		int i = 0;//start from the top and stop at the first solid vertex.
		while(i < Biome.layerCount - 1 && (vertices[i].empty() || vertices[i].getAverageSolidity() < 1))
			i++;
		
		if(i == Biome.layerCount - 1) 
			throw new RuntimeException("No fluid vertex found!");
		return vertices[i];
	}

	/**
	 * Puts the coordinates of a random location on top of this column into posField. 
	 * @param random
	 * @param posField
	 * @param dir +1 for left, -1 for right
	 * @return This column, to allow piping.
	 */
	public Column getRandomTopLocation(Random random, Vec posField){
		topSolidVertex = findTopSolidVertex(vertices);
		double fac = random.nextDouble();
		int dir = 0;
		if(right != null)
			dir = -1;
		else if(left != null)
			dir = +1;
		interpolateTopLocation(topSolidVertex, fac, posField, dir);
		return this;
	}
	
	/**
	 * Puts the coordinates of a random location on top of this column in direction dir into posField. 
	 * @param random
	 * @param posField
	 * @param dir +1 for left, -1 for right
	 * @return This column, to allow piping.
	 */
	public Column getRandomTopLocation(Random random, Vec posField, int dir) {
		topSolidVertex = findTopSolidVertex(vertices);
		double fac = random.nextDouble();
		interpolateTopLocation(topSolidVertex, fac, posField, dir);
		return this;
	}
	
	/**
	 * Interpolates the path between the given vertex and it's neighbor in the dir direction and places it's value at the offset factor*COLUMN_WIDTH into posField.
	 * @param topSolidVertex The vertex to be interpolated
	 * @param factor Offset in terms of COLUMN_WIDTH to interpolate
	 * @param posField Vector that the result will be written to.
	 * @param dir Direction of the interpolation. May be +1 (left) or -1 (right)
	 */
	private void interpolateTopLocation(Vertex topSolidVertex, double factor, Vec posField, int dir) {
		if(dir == -1) {
			posField.set(
					xReal + (factor*(right.getX() - xReal)),
					topSolidVertex.y() + (factor*(right.vertices[topSolidVertex.getYIndex()].y() - topSolidVertex.y())));
		} else if(dir == 1) {
			posField.set(
					xReal + (factor*(left.getX() - xReal)),
					topSolidVertex.y() + (factor*(left.vertices[topSolidVertex.getYIndex()].y() - topSolidVertex.y())));
		} else {
			new Exception("Unknown direction!").printStackTrace();
		}
	}
	
	/**
	 * Adds the thing t to this column.
	 * Doesn't close the gap if it was added somewhere else before,
	 * so it should be removed from there earlier.
	 * @param t Thing to add
	 */
	public void add(Entity t) {
		int o = t.type.ordinal;
		t.setPrev(null);
		t.setNext(things[o]);
		if(things[o] != null) things[o].setPrev(t);
		things[o] = t;
	}
	
	/**
	 * Removes the thing t from this column. Closes the created gap too.
	 * @param t Thing to remove
	 */
	public void remove(Entity t) {
		t.free();
		if(things[t.type.ordinal] == t)
			things[t.type.ordinal] = t.next();
		t.setLinked(false);
	}
	
	//Getters
	
	@SuppressWarnings("unchecked")
	public <T extends Entity> T firstThing(Species<T> type) {	return (T)things[type.ordinal];	}
	public Entity firstThing(int type) {					return things[type];	}
	public Vertex vertices(int index) {					return vertices[index];	}
	public int getIndex() {								return xIndex;	}
	public Vertex getTopSolidVertex(){					return topSolidVertex;	}
	public Vertex getTopFluidVertex(){					return topFluidVertex;	}
	public double getCollisionY(){						return collisionYSolid;	}
	public double getCollisionYFluid(){					return collisionYFluid;	}
	public double getX() {								return xReal;	}
	public <T extends Entity> T getFirst(Species<T> type) {				return firstThing(type);	}	
	public Biome getBiome() {							return biome;	}	
	public Color getTopColor() {						return topColor;	}
	public Color getLowColor() {						return lowColor;	}
	public Vec getTopLine(Vec topLine) {
		return topLine.set(right.getX() - xReal, right.vertices[topSolidVertex.getYIndex()].y() - topSolidVertex.y());}
	public Vec getCollisionLine(Vec topLine){
		return topLine.set(right.getX() - xReal, right.getCollisionY() - getCollisionY());}
	
	//Setters
	public void setTopColor(Color color) {				this.topColor = color;	}
	public void setLowColor(Color color) {				this.lowColor = color;	}
	public void setIndex(int xIndex){
		this.xIndex = xIndex;
		this.xReal = xIndex*COLUMN_WIDTH;
	}
}
