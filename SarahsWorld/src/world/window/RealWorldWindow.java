package world.window;

import world.data.Column;
import world.data.Dir;

public class RealWorldWindow {

	protected int radius;
	protected Column[] ends = new Column[2];
	
	public RealWorldWindow(Column anchor, int radius) {
		this.radius = radius;

		ends[Dir.r] = anchor;
		ends[Dir.l] = ends[Dir.r];
	}
	
	public Column getEnd(int iDir) {
		return ends[iDir];
	}
	
	public void moveToColumn(int xIndex) {
		//keep this order! It prevents overtaking for generating world windows.
		//increase size
		for(int end = 0; end < Dir.s.length; end++) {
			while(Dir.s[end]*(ends[end].xIndex - xIndex) < radius && dirOkay(end)) {
				shiftOutwards(end);
			}
		}
		//decrease size
		for(int end = 0; end < Dir.s.length; end++) {
			while(Dir.s[end]*(ends[end].xIndex - xIndex) > radius && ends[end].next(1-end) != null) {
				shiftInwards(end);
			}
		}
	}

	protected void shiftOutwards(int iDir) {
		ends[iDir] = ends[iDir].next(iDir);
	}
	
	protected void shiftInwards(int end) {
		ends[end] = ends[end].next(1-end);
	}
	
	protected boolean dirOkay(int index) {
		return ends[index].next(index) != null;
	}
}
