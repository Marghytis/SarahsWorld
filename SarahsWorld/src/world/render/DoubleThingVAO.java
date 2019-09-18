package world.render;

import extra.Main;
import extra.things.traits.Animating.AnimatingPlugin;

public class DoubleThingVAO extends ThingVAO {

	public static int BACK = 0, FRONT = 1;
	public static int lowerSide = BACK, higherSide = FRONT;
	public static int[] dirs = new int[2];
	static {dirs[lowerSide] = 1; dirs[higherSide] = -1;}
	
	private int[] lastUsedIndices = new int[2];
	private final int[] ends = new int[2];//ends are usable indices
	
	public DoubleThingVAO(int capacity) {
		super(capacity);
		ends[lowerSide] = 0;
		ends[higherSide] = capacity-1; 
		lastUsedIndices[lowerSide] = ends[lowerSide]-dirs[lowerSide];
		lastUsedIndices[higherSide] = ends[higherSide]-dirs[higherSide];
	}

	public int end(int side) {
		return Math.max(ends[side], lastUsedIndices[side]);
	}
	
	public int start(int side) {
		return Math.min(ends[side], lastUsedIndices[side]);
	}
	
	public int size(int side) {
		return ((lastUsedIndices[side] - ends[side])*dirs[side])+1;
	}
	
	public int sizePrint(int side) {
		if(side == 1) {
			System.out.println(lastUsedIndices[side]);
		}
		return ((lastUsedIndices[side] - ends[side])*dirs[side])+1;
	}
	
	
	
	public boolean empty(int side) {
		return lastUsedIndices[side]*dirs[side] < ends[side]*dirs[side];
	}
	
	public boolean empty() {
		return empty(BACK) && empty(FRONT);
	}

	/*
	 * dirLeft = +1					dirRight = -1
	 *  endLeft luiLeft       luiRight   endRight
	 *  |       |             |          |
	 * ;---------0000000000000------------;
	 */
	public void printArray() {
		String output =   "dirs[0] = +1;              dirs[1] = -1               \n"
						+ "eL := ends[0];             eR := ends[1]              (eL, eR) = (" + ends[0] + ", " + ends[1] + ")\n"
						+ "lL := lastUsedIndices[0];  lR := lastUsedIndices[1]   (lL, lR) = (" + lastUsedIndices[0] + ", " + lastUsedIndices[1] + ")\n";
		char[] text = new char[capacity+2];
		char[] pointers = new char[capacity+1];
		char[] values = new char[capacity];
		for(int i = 0; i < capacity; i++) {
			text[i] = ' ';
			pointers[i] = ' ';
			if(i <= lastUsedIndices[0] || i >= lastUsedIndices[1]){
				values[i] = '-';
			} else {
				values[i] = '0';
			}
		}
		text[ends[0]] = 'e'; text[ends[0]+1] = 'L';
		pointers[ends[0]] = '|';
		text[lastUsedIndices[0]] = 'l'; text[lastUsedIndices[0]+1] = 'L';
		pointers[lastUsedIndices[0]] = '|';
		text[lastUsedIndices[1]] = 'l'; text[lastUsedIndices[1]+1] = 'R';
		pointers[lastUsedIndices[1]] = '|';
		text[ends[1]] = 'e'; text[ends[1]+1] = 'R';
		pointers[ends[1]] = '|';

		output += " " + new String(text) + "\n";
		output += " " + new String(pointers) + "\n";
		output += " " + new String(values);
		
		System.out.println(output);
	}
	
	protected int nextUsedIndex(int i) {
		if(i == lastUsedIndices[lowerSide])
			return lastUsedIndices[higherSide];
		else
			return i + 1;
	}
	
	public static int whichSide(AnimatingPlugin t) {
		return t.getZ() < 0 ? FRONT : BACK;
	}
	
	public int spaceLeft() {
		return capacity - size(FRONT) - size(BACK);
	}

	public void add(AnimatingPlugin t, boolean inBatch){
		add(t, inBatch, true);
	}
	public void add(AnimatingPlugin t, boolean inBatch, boolean automaticallyRemoveFreedThings){

		t.onVisibilityChange(true);
		int side = whichSide(t);

		//if the lastUsedIndices would cross, freed things are removed to make space
		if(dirs[side]*lastUsedIndices[side] + 1 >= dirs[side]*lastUsedIndices[1-side]){
			if(automaticallyRemoveFreedThings) {
				Main.out.println("removing freed things");
				removeFreedThings();
			} else {
				throw new RuntimeException("VAO is full!");
			}
		}
		//if the removal of freed things didn't help, the array is enlarged
		if(dirs[side]*lastUsedIndices[side] + 1 >= dirs[side]*lastUsedIndices[1-side]){
			System.err.println("Not enough space for " + t.getThing().type.name + "s! Current capacity: " + capacity + " quads. Default: " + t.getThing().type.maxVisible);
			enlarge();
		}
		
		//last used index is shifted by one and the thing is placed at the new index.
		lastUsedIndices[side] += dirs[side];
		things[lastUsedIndices[side]] = t;
		t.setIndex((short) lastUsedIndices[side]);
		t.setAddedToVAO(true);
		if(!inBatch) {
			changeUsual(t, inBatch);
			changeUnusual(t, inBatch);
		}
	}
	
	public void remove(AnimatingPlugin t, boolean vboAsWell){
		int side = whichSide(t);
		if(dirs[side]*lastUsedIndices[side] < dirs[side]*ends[side]){
			new Exception("You removed one " + t.getThing().type.name + " too much!!!!").printStackTrace();
			return;
		}
		if(t.getIndex() == -1) {
			new Exception("This " + t.getThing().type.name + " is already deleted in the VAO!!");
			return;
		}
		t.onVisibilityChange(false);
		
		//move last thing in the list to t's location and update lastUsedIndex
		moveThing(lastUsedIndices[side], t.getIndex(), vboAsWell);
		lastUsedIndices[side] -= dirs[side];
		
		t.setIndex((short)-1);
		t.setAddedToVAO(false);
		t.setFreeToMakeInvisible(false);//reset this flag
		if(t.selected()) System.out.println("Removing...");
	}
	
	public void enlarge() {
		enlarge(true);
	}
	public void enlarge(boolean vboAsWell){
		super.enlarge();
		
		int newEnd = capacity - 1;
		int shift = newEnd - ends[higherSide];
		
		//move all the (normally) FRONT things to the higher end of the buffer and array
		for(int i = ends[higherSide]; i != lastUsedIndices[higherSide] + dirs[higherSide]; i+=dirs[higherSide]) {
			moveThing(i, i + shift, vboAsWell);
		}
		ends[higherSide] += shift;
		lastUsedIndices[higherSide] += shift;
	}

	public void free(double xMin, double xMax) {
		for(int i = 0; i < things.length; i++) {
			if(things[i] != null && xMin <= things[i].getThing().pos.x && xMax >= things[i].getThing().pos.x) {
				things[i].setFreeToMakeInvisible(true);
			}
		}
	}
}
