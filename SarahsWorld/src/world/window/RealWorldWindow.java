package world.window;

import java.util.function.Consumer;

import world.data.Column;
import world.data.Dir;

public class RealWorldWindow {

	protected int radius;
	protected Column[] ends = new Column[2];
	protected Column it;
	
	public RealWorldWindow(Column anchor, int radius) {
		this.radius = radius;

		ends[Dir.r] = anchor;
		ends[Dir.l] = ends[Dir.r];
	}
	
	public Column getEnd(int iDir) {
		return ends[iDir];
	}
	
	public Column start() {
		return ends[Dir.l];
	}
	
	public Column end() {
		return ends[Dir.r].next();
	}
	
	public void forEachColumn(Consumer<Column> cons) {

		for(Column c = start(); c != end(); c = c.next()) {
			cons.accept(c);
		}
	}
	
	public void moveToColumn(int xIndex) {
		//keep this order! It prevents overtaking for generating world windows.
		//increase size
		for(int end = 0; end < Dir.s.length; end++) {
			while(Dir.s[end]*(ends[end].xIndex - xIndex) < radius && dirOkay(end) && ends[end].next(end) != null) {
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
	
	/**
	 * Is overridden by GeneratingWorldWindow
	 * @param index
	 * @return
	 */
	protected boolean dirOkay(int index) {
		return ends[index].next(index) != null;
	}
}
