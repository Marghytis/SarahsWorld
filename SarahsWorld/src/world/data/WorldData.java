package world.data;

import java.io.*;
import java.util.*;

import quest.ActiveQuest;
import util.Color;
import world.World;
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
	
	public Column addLeft(Column l){
		l.setX(leftColumn.xIndex-1);
		l.right = leftColumn;
		leftColumn.left = l;
		leftColumn = l;
		mostLeft--;
		return l;
	}
	public Column addLeft(Biome biome, Color top, Color low, Vertex... vertices){
		return addLeft(new Column(0, biome, top, low, vertices));
	}
	public Column addRight(Column r){
		r.setX(rightColumn.xIndex+1);
		r.left = rightColumn;
		rightColumn.right = r;
		rightColumn = r;
		mostRight++;
		return r;
	}
	public Column addRight(Biome biome, Color top, Color low, Vertex... vertices){
		return addRight(new Column(0, biome, top, low, vertices));
	}

	public void save(DataOutputStream output) {
		// TODO Auto-generated method stub
		
	}
}
