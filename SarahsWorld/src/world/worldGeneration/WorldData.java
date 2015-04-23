package world.worldGeneration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import util.math.Vec;
import world.Material;
import world.worldGeneration.objects.ai.Thing;
import world.worldGeneration.objects.ai.ThingType;
import data.IndexBuffer;
import data.Queue;


public class WorldData {
	public Column mostRight, mostLeft;
	public WorldData(DataInputStream input) {
		//TODO ...
	}
	public WorldData() {
		
	}
	@Deprecated //too expensive
	public void add(Thing t){
		int xIndex = t.pos.p.xInt()/(int)Column.step;//I know, the middle one has the things of two areas
		get(xIndex).add(t);
	}
	
	public void addFirst(Vertex... vertices){
		Column l = new Column(0, vertices);
		mostRight = l;
		mostLeft = l;
	}
	public void addLeft(Vertex... vertices){
		Column l = new Column(mostLeft.xIndex-1, vertices);
		l.right = mostLeft;
		mostLeft.left = l;
		mostLeft = l;
	}
	public void addRight(Vertex... vertices){
		Column r = new Column(mostRight.xIndex+1, vertices);
		r.left = mostRight;
		mostRight.right = r;
		mostRight = r;
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
		public Thing[] things;//only dummies
		public int xIndex;
		public double xReal;
		public int[] collisionVecs;
		
		public Column(int xIndex, Vertex[] vertices){
			this.xIndex = xIndex;
			this.xReal = xIndex*step;
			this.vertices = vertices;
			for(Vertex v : vertices){
				v.parent = this;
			}
			this.things = new Thing[ThingType.values().length];
			collisionVecs();
		}
		
		public void collisionVecs(){
			boolean searching = true;
			Queue<Integer> heights = new Queue<>();
			for(int i = World.layerCount-1; i >= 0; i--){
				if(searching && vertices[i].mats.read.data == Material.AIR){
					heights.enqueue(i);
					searching = false;
				} else if(!searching && vertices[i].mats.read.data != Material.AIR){
					searching = true;
				}
			}
			collisionVecs = new int[heights.length];
			Queue<Integer>.Node cursor = heights.leftEnd;
			for(int i = 0; i < collisionVecs.length; i++){
				collisionVecs[i] = cursor.data;
				cursor = cursor.next;
			}
		}
		
		public Vec getRandomTopLocation(Random random, boolean cave, Vertex[] linkField){
			int index = collisionVecs[0];
			if(cave && collisionVecs.length > 1){
				index = collisionVecs[random.nextInt(collisionVecs.length - 1) + 1];
			}
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
			t.disconnect();
			int t2 = t.type.ordinal(); 
			t.right = things[t2].left;
			things[t2].left = t;
			t.right = things[t2];
			t.left.right = t;
		}
		
		public Vec getTopLine(){
			return new Vec(xReal - left.xReal, collisionVecs[0] - left.collisionVecs[0]);
		}
	}
	public class Vertex {
		public static final int maxMatCount = 5;
		public double y;
		public IndexBuffer<Material> mats;
		public double transition;
		public double[] alphas;
		public int matCount;
		public Column parent;
		public Vertex(){
			mats = new IndexBuffer<>(maxMatCount);
			alphas = new double[maxMatCount];
		}
	}
	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
