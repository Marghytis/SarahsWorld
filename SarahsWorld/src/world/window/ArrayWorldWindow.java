package world.window;

import world.data.Column;
import world.data.Dir;

public class ArrayWorldWindow extends RealWorldWindow {

	protected Column[] columns;
	protected int center;
	private int indexShift;//is private because it should really not be changed outside this class
	
	public ArrayWorldWindow(Column anchor, int radius) {
		super(anchor, radius);
		columns = new Column[2*radius+1];
		this.center = anchor.xIndex;
		
		//create Column array and find borders
		insertColumn(anchor);
		for(int end = 0; end <= 1; end++) {
			while(Dir.s[end]*(ends[end].xIndex - center) < radius && ends[end].next(end) != null){
				ends[end] = ends[end].next(end);
				insertColumn( ends[end]);
				letAppear(ends[end], end);
			}
		}
		if(ends[Dir.l].xIndex > center-radius || ends[Dir.r].xIndex < center + radius){
			new Exception("World data is not large enough yet").printStackTrace();
			System.exit(-1);
		}
	}
	
	protected void shiftOutwards(int end) {
		super.shiftOutwards(end);
		add(ends[end], end);
		letAppear(ends[end], end);
	}
	
	protected void shiftInwards(int end) {
		letDisappear(ends[end]);
		super.shiftInwards(end);
	}
	
	protected int indexShift() {
		return indexShift;
	}
	
	protected void add(Column c, int iDir) {
		if(iDir == Dir.l) {
			indexShift = (indexShift+columns.length-1)%columns.length;//this has to run prior to the other two statements
			columns[indexShift] = c;
			addAtIndexShift(c);
		} else if(iDir == Dir.r) {
			columns[indexShift] = c;
			addAtIndexShift(c);
			indexShift = (indexShift+1)%columns.length;
		}
	}
	
	protected void addAtIndexShift(Column c) {
		
	}
	
	protected void letAppear(Column c, int iDir) {
		//to override if wanted
	}
	
	protected void letDisappear(Column c) {
		//override
	}
	
	void insertColumn(Column c){
		int index = c.xIndex - (center - radius); 
		columns[index] = c;
		if(index > 0 && columns[index-1] != null){
			c.left = columns[index-1];
			columns[index-1].right = c;
		}
		if(index < columns.length-1 && columns[index+1] != null){
			c.right = columns[index+1];
			columns[index+1].left = c;
		}
	}

}
