package world.window;

import java.util.function.Consumer;

import util.Time;
import world.data.Column;
import world.data.ColumnListElement;
import world.data.Dir;

public class RealWorldWindow {

	protected int radius;
	protected ColumnListElement[] ends = new ColumnListElement[2];
	
	public RealWorldWindow(ColumnListElement anchor, int radius) {
		this.radius = radius;

		ends[Dir.r] = anchor;
		ends[Dir.l] = ends[Dir.r];
	}
	
	public ColumnListElement getEnd(int iDir) {
		return ends[iDir];
	}
	
	public ColumnListElement start() {
		return ends[Dir.l];
	}
	
	public ColumnListElement end() {
		return ends[Dir.r].next();
	}
	
	public ColumnListElement at(double x) {
		if(ends[0].column().getX() > x || ends[1].column().getX() < x) {
			return null;
		} else {
			for(ColumnListElement c = start(); c != end().prev(); c = c.next()) {
				if(c.next().column().getX() > x) {
					return c;
				}
			}
			return null;
		}
	}
	
	public void forEachColumn(Consumer<Column> cons) {

		for(ColumnListElement c = start(); c != end(); c = c.next()) {
			cons.accept(c.column());
		}
	}
	
	public void moveToColumn(int xIndex) {
		//keep this order! It prevents overtaking for generating world windows.
		//increase size
		Time.update(12);
		for(int end = 0; end < Dir.s.length; end++) {
			while(Dir.s[end]*(ends[end].getIndex() - xIndex) < radius && dirOkay(end) && ends[end].next(end) != null) {
				shiftOutwards(end);
			}
		}
		Time.update(12);
		Time.update(13);
		//decrease size
		for(int end = 0; end < Dir.s.length; end++) {
			while(Dir.s[end]*(ends[end].getIndex() - xIndex) > radius && ends[end].next(1-end) != null) {
				shiftInwards(end);
			}
		}
		Time.update(13);
		if(this instanceof ThingWindow) {
//			Main.world.engine.lastTimes[1][Main.world.engine.timeIndex] = Time.delta[12];
//			Main.world.engine.lastTimes[2][Main.world.engine.timeIndex] = Time.delta[13];
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
