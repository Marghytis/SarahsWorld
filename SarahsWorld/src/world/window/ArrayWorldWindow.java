package world.window;

import basis.exceptions.WorldTooSmallException;
import moveToLWJGLCore.Dir;
import world.data.ColumnListElement;

public abstract class ArrayWorldWindow extends RealWorldWindow {

	protected ColumnListElement[] columns;
	protected int center;
	private int[] nextIndex;//is private because it really should not be changed outside this class
	private boolean isBuilt = false;;
	
	int sideLastInwards;
	
	public ArrayWorldWindow(ColumnListElement anchor, int radius) throws WorldTooSmallException {
		super(anchor, radius);
		columns = new ColumnListElement[2*radius+1];
		this.center = anchor.getIndex();
		
		nextIndex = new int[2];
		
		loadAllColumns(anchor, anchor.getIndex());
		isBuilt = true;
	}
	
	/**
	 * Tries to fill this windows array with columns starting from the anchor column.
	 * If this is the first time this is done (at initialization),
	 * the columns are attached to each other and their appear-function is called as well.
	 * @param anchor
	 * @param radius
	 * @throws WorldTooSmallException
	 */
	private void loadAllColumns(ColumnListElement anchor, int center1) throws WorldTooSmallException {
		//calculate current window center
		center = center1;
		//move anchor to center
		while(anchor.getIndex() < center)
			anchor = anchor.next(Dir.r);
		while(anchor.getIndex() > center)
			anchor = anchor.next(Dir.l);
		
//		//detach all visible columns;
//		
//		for(Column c = start(); c != end(); c = c.next()) {
//			
//		}
		
		sideLastInwards = Dir.l;
		nextIndex[Dir.r] = add1To(radius);
		nextIndex[Dir.l] = substract1From(radius);

		ends[Dir.r] = anchor;
		ends[Dir.l] = ends[Dir.r];
		//create Column array and find borders
		insertColumn(anchor, !isBuilt);
		for(int end = 0; end <= 1; end++) {
			while(Dir.s[end]*(ends[end].getIndex() - center) < radius && ends[end].next(end) != null){
				ends[end] = ends[end].next(end);
				insertColumn( ends[end], !isBuilt);
				if(!isBuilt)
					letAppear(ends[end], end);
				nextIndex[end] = shiftBy1(nextIndex[end], end);
			}
		}
		
		if(ends[Dir.l].getIndex() > center-radius || ends[Dir.r].getIndex() < center + radius){
			throw new WorldTooSmallException();
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
	protected void addColumn(ColumnListElement c, int iDir) {
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
	
	protected abstract void addAt(ColumnListElement c, int index);
	
	protected void letAppear(ColumnListElement c, int iDir) {
		//to override if wanted
	}
	
	protected void letDisappear(ColumnListElement c) {
		//override
	}
	
	void insertColumn(ColumnListElement c, boolean modifiyNeighbors){
		int index = c.getIndex() - (center - radius);//index relative to the most left location of this window
		columns[index] = c;
		if(modifiyNeighbors) {
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

}
