package world.window;

import main.Main;
import world.data.Column;
import world.data.Dir;

public abstract class ArrayWorldWindow extends RealWorldWindow {

	protected Column[] columns;
	protected int center;
	private int[] nextIndex;//is private because it really should not be changed outside this class
	
	public ArrayWorldWindow(Column anchor, int radius) {
		super(anchor, radius);
		columns = new Column[2*radius+1];
		this.center = anchor.xIndex;
		
		sideLastInwards = Dir.l;
		nextIndex = new int[2];
		nextIndex[Dir.r] = add1To(radius);
		nextIndex[Dir.l] = substract1From(radius);
		//create Column array and find borders
		insertColumn(anchor);
		for(int end = 0; end <= 1; end++) {
			while(Dir.s[end]*(ends[end].xIndex - center) < radius && ends[end].next(end) != null){
				ends[end] = ends[end].next(end);
				insertColumn( ends[end]);
				letAppear(ends[end], end);
				nextIndex[end] = shiftBy1(nextIndex[end], end);
			}
		}
		
		if(ends[Dir.l].xIndex > center-radius || ends[Dir.r].xIndex < center + radius){
			new Exception("World data is not large enough yet : (" + Main.world.genWindow.getEnd(Dir.l).xIndex + " - " + Main.world.genWindow.getEnd(Dir.r).xIndex + ")").printStackTrace();
			System.exit(-1);
		}
	}
	
	protected void shiftOutwards(int end) {
		super.shiftOutwards(end);
		addColumn(ends[end], end);
		letAppear(ends[end], end);
	}
	
	protected void shiftInwards(int end) {
		letDisappear(ends[end]);
		removeColumn(end);
		super.shiftInwards(end);
	}
	
	int sideLastInwards;
	
	protected int startIndexLeft() {
		if(sideLastInwards == Dir.l) {
			return nextIndex[Dir.r];
		} else {
			return add1To(nextIndex[Dir.l]);
		}
	}
	protected void removeColumn(int iDir) {
		nextIndex[iDir] = shiftBy1(nextIndex[iDir], 1-iDir);
		sideLastInwards = iDir;
	}
	protected void addColumn(Column c, int iDir) {
		columns[nextIndex[iDir]] = c;
		addAt(c, nextIndex[iDir]);
//		if(nextIndex[iDir] == shiftBy1(nextIndex[1-iDir], iDir)) {
//			nextIndex[1 - iDir] = shiftBy1(nextIndex[1-iDir], iDir);//this has to run prior to the other two statements
//		}
		nextIndex[iDir] = shiftBy1(nextIndex[iDir], iDir);
	}

	protected int add1To(int index) {
		return (index+1)%columns.length;
	}
	protected int substract1From(int index) {
		return (index+columns.length-1)%columns.length;
	}
	protected int shiftBy1(int index, int dir) {
		if(dir == Dir.l) {
			return substract1From(index);
		} else {
			return add1To(index);
		}
	}
	
	protected abstract void addAt(Column c, int index);
	
	protected void letAppear(Column c, int iDir) {
		//to override if wanted
	}
	
	protected void letDisappear(Column c) {
		//override
	}
	
	void insertColumn(Column c){
		int index = c.xIndex - (center - radius);//index relative to the most left location of this window
		columns[index] = c;
		if(index > 0 && columns[index-1] != null){
			c.setLeft(columns[index-1]);
			columns[index-1].setRight(c);
		}
		if(index < columns.length-1 && columns[index+1] != null){
			c.setRight(columns[index+1]);
			columns[index+1].setLeft(c);
		}
	}

}
