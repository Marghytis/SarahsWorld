package world;

import newStuff.Chunk;

public class ChunkBank {

	Chunk[] positive = new Chunk[100], negative = new Chunk[100];
	
	public ChunkBank(int halfCapacity){
		positive = new Chunk[halfCapacity];
		negative = new Chunk[halfCapacity];//first element of negative is never used
	}
	
	public void put(Chunk chunk){
		if(chunk.xIndex >= 0){
			positive[chunk.xIndex] = chunk;
		} else {
			negative[-chunk.xIndex] = chunk;
		}
	}
	
	public Chunk get(int index){
		if(index >= 0){
			return positive[index];
		} else {
			return negative[-index];
		}
	}
}
