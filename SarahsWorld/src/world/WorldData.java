package world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import quest.ActiveQuest;
import things.Thing;
import things.ThingType;
import util.Color;
import util.math.Vec;
import world.generation.Biome;


public class WorldData {
	public Random random = World.rand;
	public int mostLeft, mostRight;
	public Column rightColumn, leftColumn;
	public int first;
//	public Chunk rightChunk, leftChunk;
//	public ChunkBank allChunks = new ChunkBank(100);
	public World world;
	
	public List<ActiveQuest> quests = new ArrayList<>();
	
	public WorldData(DataInputStream input) {
		//TODO ...
	}
	public WorldData(World world) {
		this.world = world;
	}
	
	public void addFirst(Biome biome, Vertex... vertices){
		Column f = new Column(0, biome, biome.topColor, biome.lowColor, vertices);
		leftColumn = f;
		rightColumn = f;
		first = f.xIndex;
		
//		leftChunk = new Chunk(-1, f);
//		rightChunk = new Chunk(0, f);
//		allChunks.put(leftChunk);
//		allChunks.put(rightChunk);
//		leftChunk.right = rightChunk;
//		rightChunk.left = leftChunk;
	}
	
	public Column addLeft(Biome biome, Color top, Color low, Vertex... vertices){
		Column l = new Column(leftColumn.xIndex-1, biome, top, low, vertices);
		l.right = leftColumn;
		leftColumn.left = l;
		leftColumn = l;
		mostLeft--;
		
//		leftChunk.add(l);
//		if(leftChunk.ready){
//			
//			Chunk newChunk = new Chunk(leftChunk.xIndex-1, l);
//			leftChunk.left = newChunk;
//			newChunk.right = leftChunk;
//			leftChunk = newChunk;
//			allChunks.put(newChunk);
//			mostLeftLoaded--;
//		}
		return l;
	}
	public Column addRight(Biome biome, Color top, Color low, Vertex... vertices){
		Column r = new Column(rightColumn.xIndex+1, biome, top, low, vertices);
		r.left = rightColumn;
		rightColumn.right = r;
		rightColumn = r;
		mostRight++;
		
//		rightChunk.add(r);
//		if(rightChunk.ready){
//			Chunk newChunk = new Chunk(rightChunk.xIndex + 1, r);
//			rightChunk.right = newChunk;
//			newChunk.left = rightChunk;
//			rightChunk = newChunk;
//			allChunks.put(newChunk);
//			mostRightLoaded++;
//		}
		return r;
	}

	public class Column {
		public static final double step = 20;
		public Column left, right;
		public Vertex[] vertices;
		public Thing[] things;//these are the anchors. may be null
		public int xIndex;
		public double xReal;
		public int collisionVec;
		public int collisionVecWater;
		public Biome biome;
		public Color topColor, lowColor;
		
		public Column(int xIndex, Biome biome, Color top, Color low, Vertex[] vertices){
			this.xIndex = xIndex;
			this.xReal = xIndex*step;
			this.biome = biome;
			this.topColor = top;
			this.lowColor = low;
			this.vertices = vertices;
			for(Vertex v : vertices){
				v.parent = this;
			}
			collisionVec = getCollisionVec();
			collisionVecWater = getCollisionVecWater();
			this.things = new Thing[ThingType.types.length];
		}
		
		public int getCollisionVec(){
			int i = 0; while(i < Biome.layerCount - 1 && (vertices[i].empty() || vertices[i].averageSolidity <= 1))
				i++;
			if(i == Biome.layerCount - 1) i = -1;
			return i;
		}
		
		public int getCollisionVecWater(){
			int i = 0;
				while(i < Biome.layerCount - 1 && (vertices[i].empty() || vertices[i].averageSolidity < 1))
					i++;
			if(i == Biome.layerCount - 1) i = -1;
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
		 * @param t
		 */
		public void add(Thing t){
			t.disconnectFrom(t.oldLink);
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
			biome.spawnEffects(WorldData.this, this, left);
		}
		
		public void disappear(){
			for(int i = 0; i < things.length; i++){
				for(Thing t = things[i]; t != null; t = t.next){
					t.setVisible(false);
				}
			}
		}
		
		public Vec getTopLine(Vec topLine){
			return topLine.set(right.xReal - xReal, right.vertices[collisionVec].y - vertices[collisionVec].y);
		}
	}
	public class Vertex {
		public static final int maxMatCount = 4;
		public double y;
		Material[] mats;
		public double averageSolidity, averageDeceleration, averageBouyancy;
		public int lastMatIndex, firstMatIndex;
		public double transitionHeight;
		public double[] alphas;
		public Column parent;
		public int yIndex;
		
		public boolean prepared;
		public float[] texCoordsPrepared = new float[4];
		
		public Vertex(int yIndex, Material[] copy, double[] alphas, int firstMatIndex, int lastMatIndex, double transitionHeight, double y) {
			this.yIndex = yIndex;
			this.firstMatIndex = firstMatIndex;
			this.lastMatIndex = lastMatIndex;
			this.alphas = alphas;
			mats = copy;
			calculateAverage();
			this.transitionHeight = transitionHeight;
			this.y = y;
		}
		public Material[] mats(){return mats;}
		public void enqueueMat(Material mat, double alpha, boolean below){
			if(!below){
				lastMatIndex = (lastMatIndex+1)%maxMatCount;
				mats[lastMatIndex] = mat;
				alphas[lastMatIndex] = alpha;
			} else {
				firstMatIndex = (firstMatIndex+maxMatCount-1)%maxMatCount;
				mats[firstMatIndex] = mat;
				alphas[firstMatIndex] = alpha;
			}
			calculateAverage();
//			alpha = 1 - alpha;
//			for(int i = 0; i < maxMatCount; i++){
//				if(i != nextMatIndex){
//					alphas[i] *= alpha;
//				}
//			}
		}
		public void calculateAverage(){
			averageSolidity = 0;
			averageDeceleration = 0;
			averageBouyancy = 0;
			double totalWeight = 0;
			for(int i = 0; i < maxMatCount; i++){
				if(mats[i] != Material.AIR){
					totalWeight += alphas[i];
					averageSolidity += alphas[i]*mats[i].solidity;
					averageDeceleration += alphas[i]*mats[i].deceleration;
					averageBouyancy += alphas[i]*mats[i].bouyancy;
				}
			}
			if(totalWeight != 0){
				averageSolidity /= totalWeight;
				averageDeceleration /= totalWeight;
				averageBouyancy /= totalWeight;
			}
		}
		public boolean empty(){
			for(int i = 0; i < maxMatCount; i++)
				if(alphas[i] != 0)
					return false;
			return true;
		}
	}
	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
