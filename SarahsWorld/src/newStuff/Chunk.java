package newStuff;

import render.VAO;
import things.Thing;
import world.WorldData.Column;

public class Chunk {
	
	public static final int size = 11;//101
	public static final double realSize = (size-1)*Column.step;
	
	public Thing[] thingPool = new Thing[1000]; int index;
	
	public Chunk right, left;
	
	public Column[] columns;
	public int amount;
	public boolean ready;
	public int xIndex;
	
	public VAO vao;
	
	public Chunk(int xIndex, Column first){
		this.xIndex = xIndex;
		this.amount = 1;
		columns = new Column[size];
		insertColumn(first);
	}
	
	public void add(Column c){
		if(ready){
			(new Exception("This chunk is already full! " + xIndex)).printStackTrace();
			System.exit(1);
		}
		insertColumn(c);
		amount++;
		if(amount >= size){
			ready = true;
		}
	}
	
	void insertColumn(Column c){
		columns[c.xIndex - (xIndex*(size-1))] = c;
	}
}
