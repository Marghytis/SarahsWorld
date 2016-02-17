package world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import quest.ActiveQuest;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.generation.Biome;
import data.IndexBuffer;


public class WorldData {
	public Random random = World.rand;
	public Column mostRight, mostLeft;
	public World world;
	
	public List<ActiveQuest> quests = new ArrayList<>();
	
	public WorldData(DataInputStream input) {
		//TODO ...
	}
	public WorldData(World world) {
		this.world = world;
	}
	@Deprecated //too expensive
	public void add(Thing t){
		int xIndex = t.pos.xInt()/(int)Column.step;//I know, the middle one has the things of two areas
		get(xIndex).add(t);
	}
	
	public void addFirst(Biome biome, Vertex... vertices){
		Column l = new Column(0, biome, vertices);
		mostRight = l;
		mostLeft = l;
	}
	public void addLeft(Biome biome, Vertex... vertices){
		Column l = new Column(mostLeft.xIndex-1, biome, vertices);
		l.right = mostLeft;
		mostLeft.left = l;
		mostLeft = l;
		for(int i = 0; i < l.things.length; i++){
			Thing mostleftThing = l.right.things[i];
			while(mostleftThing.left != null) mostleftThing = mostleftThing.left;
			mostleftThing.left = l.things[i];
			l.things[i].right = mostleftThing;
		}
	}
	public void addRight(Biome biome, Vertex... vertices){
		Column r = new Column(mostRight.xIndex+1, biome, vertices);
		r.left = mostRight;
		mostRight.right = r;
		mostRight = r;
		for(int i = 0; i < r.things.length; i++){
			r.left.things[i].right = r.things[i];
			r.things[i].left = r.left.things[i];
		}
	}
	public Column get(int x){
		if(x < (mostRight.xIndex + mostLeft.xIndex)/2){
			Column cursor = mostLeft;
			for(; cursor.xIndex < x; cursor = cursor.right);
			return cursor;
		} else {
			Column cursor = mostRight;
			for(; cursor.xIndex > x; cursor = cursor.left);
			return cursor;
		}
	}
	public class Column {
		public static final double step = 20;
		public Column left, right;
		public Vertex[] vertices;
		public Thing[] things;//just dummies
		public int xIndex;
		public double xReal;
		public int collisionVec;
		public int collisionVecWater;
		public Biome biome;
		
		public Column(int xIndex, Biome biome, Vertex[] vertices){
			this.xIndex = xIndex;
			this.xReal = xIndex*step;
			this.biome = biome;
			this.vertices = vertices;
			for(Vertex v : vertices){
				v.parent = this;
			}
			collisionVec = getCollisionVec();
			collisionVecWater = getCollisionVecWater();
			this.things = new Thing[ThingType.types.length];
			for(int i = 0; i < things.length; i++){
				things[i] = new Thing(ThingType.DUMMY, WorldData.this, this, new Vec(xReal, vertices[collisionVec].y), i);
			}
		}
		
		public int getCollisionVec(){
			int i = 0; while(i < World.layerCount - 1 && (vertices[i].mats.empty() || vertices[i].mats.write.previous.data.solidity <= 1))
				i++;
			if(i == World.layerCount - 1) i = -1;
			return i;
		}
		
		public int getCollisionVecWater(){
			int i = 0;
				while(i < World.layerCount - 1 && (vertices[i].mats.empty() || vertices[i].mats.write.previous.data.solidity < 1))
					i++;
			if(i == World.layerCount - 1) i = -1;
			return i;
		}
		
		public Column getRandomTopLocation(Random random, Vec posField){
			collisionVec = getCollisionVec();
			double fac = random.nextDouble();
			posField.set(
					xReal + (fac*(right.xReal - xReal)),
					vertices[collisionVec].y + (fac*(right.vertices[collisionVec].y - vertices[collisionVec].y)));
			return this;
		}
		
		/**
		 * Disconnects the thing by itself
		 * Adds it to the left of the dummy
		 * @param t
		 */
		public void add(Thing t){
			t.disconnect();
			int o = t.type.ordinal;
			t.left = things[o].left;
			things[o].left = t;
			t.right = things[o];
			if(t.left != null) t.left.right = t;
		}
		
		public Vec getTopLine(Vec topLine){
			return topLine.set(right.xReal - xReal, right.vertices[collisionVec].y - vertices[collisionVec].y);
		}
	}
	public class Vertex {
		public static final int maxMatCount = 5;
		public double y;
		public IndexBuffer<Material> mats;
		public double transitionHeight;
		public double[] alphas;
		public Column parent;
		public int yIndex;
		public Vertex(int yIndex, IndexBuffer<Material> copy, double[] alphas, double transitionHeight, double y) {
			this.yIndex = yIndex;
			mats = copy;
			this.alphas = alphas;
			this.transitionHeight = transitionHeight;
			this.y = y;
		}
	}
	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
