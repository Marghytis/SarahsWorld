package world.render;

import things.Thing;

public class DoubleThingVAO extends ThingVAO {

	public static int BACK = 0, FRONT = 1;
	public static int lowerSide = BACK, higherSide = FRONT;
	public static int[] dirs = new int[2];
	static {dirs[lowerSide] = 1; dirs[higherSide] = -1;}
	
	private int[] lastUsedIndices = new int[2];
	private int[] ends = new int[2];
	
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
		return (lastUsedIndices[side] - ends[side])*dirs[side]+1;
	}
	
	public boolean empty(int side) {
		return lastUsedIndices[side]*dirs[side] < ends[side]*dirs[side];
	}
	
	public boolean empty() {
		return empty(BACK) && empty(FRONT);
	}
	
	protected int nextUsedIndex(int i) {
		if(i == lastUsedIndices[lowerSide])
			return lastUsedIndices[higherSide];
		else
			return i + 1;
	}
	
	@Deprecated
	protected int start() {
		return super.start();
	}
	
	@Deprecated
	protected int size() {
		return super.size();
	}
	
	@Deprecated
	protected Thing getThing(int index) {
		return super.getThing(index);
	}
	
	public static int whichSide(Thing t) {
		return t.z < 0 ? FRONT : BACK;
	}

	public void add(Thing t, boolean inBatch){

		t.onVisibilityChange(true);
		int side = whichSide(t);

		if(dirs[side]*lastUsedIndices[side] + 1 >= dirs[side]*lastUsedIndices[1-side]){
			removeFreedThings();
		}
		if(dirs[side]*lastUsedIndices[side] + 1 >= dirs[side]*lastUsedIndices[1-side]){
			System.err.println("Not enough space for " + t.type.name + "s! Current capacity: " + capacity + " quads. Default: " + t.type.maxVisible);
			enlarge();
		}
		lastUsedIndices[side] += dirs[side];
		things[lastUsedIndices[side]] = t;
		t.index = (short) lastUsedIndices[side];
		t.addedToVAO = true;
		if(!inBatch) {
			changeUsual(t, inBatch);
			changeUnusual(t, inBatch);
		}
	}
	
	public void remove(Thing t, boolean vboAsWell){
		int side = whichSide(t);
		if(dirs[side]*lastUsedIndices[side] < dirs[side]*ends[side]){
			new Exception("You removed one " + t.type.name + " too much!!!!").printStackTrace();
			return;
		}
		if(t.index == -1) {
			new Exception("This " + t.type.name + " is already deleted in the VAO!!");
			return;
		}
		t.onVisibilityChange(false);
		
		//move last thing in the list to t's location and update lastUsedIndex
		moveThing(lastUsedIndices[side], t.index, vboAsWell);
		lastUsedIndices[side] -= dirs[side];
		
		t.index = -1;
		t.addedToVAO = false;
		t.freeToMakeInvisible = false;//reset this flag
		if(t.selected) System.out.println("Removing...");
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
}
