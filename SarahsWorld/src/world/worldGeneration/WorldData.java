package world.worldGeneration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import util.math.Vec;
import world.Material;
import world.worldGeneration.objects.ai.Thing;
import world.worldGeneration.objects.ai.ThingType;
import data.IndexBuffer;


public class WorldData {
	public Random random = new Random();
	public Column mostRight, mostLeft;
	public World world;
	public WorldData(DataInputStream input) {
		//TODO ...
	}
	public WorldData(World world) {
		this.world = world;
	}
	@Deprecated //too expensive
	public void add(Thing t){
		int xIndex = t.pos.p.xInt()/(int)Column.step;//I know, the middle one has the things of two areas
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
		public static final double step = 100;
		public Column left, right;
		public Vertex[] vertices;
		public Thing[] things;//just dummies
		public int xIndex;
		public double xReal;
		public int collisionVec;
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
			this.things = new Thing[ThingType.values().length];
			for(int i = 0; i < things.length; i++){
				things[i] = ThingType.DUMMY.create(WorldData.this, vertices[0], null);
			}
		}
		
		public int getCollisionVec(){
			int i = 0;
				while(i < World.layerCount - 1 && (vertices[i].mats.empty() || vertices[i].mats.write.previous.data.solid == false))
					i++;
			if(i == World.layerCount - 1) i = -1;
			return i;
		}
		
		public Vec getRandomTopLocation(Random random, Vertex[] linkField){
			collisionVec = getCollisionVec();
			int index = collisionVec;
			linkField[0] = vertices[index];
			double fac = random.nextDouble();
			return new Vec(xReal + (fac*(right.xReal - xReal)),
					vertices[index].y + (fac*(right.vertices[index].y - vertices[index].y)));
		}
		
		/**
		 * Disconnects the thing by itself
		 * Adds it to the left of the dummy
		 * @param t
		 */
		public void add(Thing t){
			if(t.type == ThingType.DUMMY){
				things[t.type.ordinal()] = t;
			} else {
				t.disconnect();
				int t2 = t.type.ordinal();
				t.left = things[t2].left;
				things[t2].left = t;
				t.right = things[t2];
				if(t.left != null) t.left.right = t;
			}
		}
		
		public Vec getTopLine(){
			return new Vec(xReal - left.xReal, vertices[collisionVec].y - left.vertices[collisionVec].y);
		}
	}
	public class Vertex {
		public static final int maxMatCount = 5;
		public double y;
		public IndexBuffer<Material> mats;
		public double transitionHeight;
		public double[] alphas;
		public Column parent;
		public Vertex(){
			mats = new IndexBuffer<>(maxMatCount);
			alphas = new double[maxMatCount];
		}
		public Vertex(IndexBuffer<Material> copy, double[] alphas, double transitionHeight, double y) {
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
