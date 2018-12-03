package world.window;

import things.Thing;
import things.ThingSet;
import things.ThingType;
import world.data.Column;
import world.data.Dir;
import world.render.DoubleThingVAO;


/**
 * A larger window than ThingWindow that lists the things it encounters so they can be added together at a later time.
 */
public class ThingPreparationWindow extends RealWorldWindow {
	
	private static int addBatchSize = 70;

	private DoubleThingVAO[] vaos;
	@SuppressWarnings("unchecked")
	private ThingSet<Thing>[][] toAdd = new ThingSet[ThingType.types.length][2];
	@SuppressWarnings("unchecked")
	private ThingSet<Thing>[] toRemove = new ThingSet[ThingType.types.length];
	
	private int dxPreparation, dxVisibility, dxDontCare, dxPreparation2, dxVisibility2, dxDontCare2, dxFree2;

	public ThingPreparationWindow(Column anchor, int rObservation, int rPreparation, int rVisibility, int rDontCare, DoubleThingVAO[] vaos) {
		super(anchor, rObservation);
		this.dxPreparation = rObservation - rPreparation;
		this.dxVisibility = rObservation - rVisibility;
		this.dxDontCare = rObservation - rDontCare;
		this.dxDontCare2 = rObservation+1 + rDontCare;
		this.dxVisibility2 = rObservation+1 + rVisibility;
		this.dxPreparation2 = rObservation+1 + rPreparation;
		this.dxFree2 = rObservation+1 + rObservation;
		this.vaos = vaos;
		for(int i = 0; i < ThingType.types.length; i++){
			int maxVisible = ThingType.types[i].maxVisible;
			toAdd[i][0] = new ThingSet<Thing>(0, addBatchSize);//TODO use fraction of 'max visible' number instead of addBatchSize
			toAdd[i][1] = new ThingSet<Thing>(1, addBatchSize);
			toRemove[i] = new ThingSet<Thing>(2, maxVisible);
		}
	}
	
	private void setCorrectStates() {
		int xStart = start().xIndex;
		for(Column c = start(); c != end(); c = c.next()) {
			int dx = c.xIndex - xStart; 
			if( dx < dxPreparation) {//not prepared l
				setFree(c, Dir.l);
			} else if( dx < dxVisibility) {//prepared l
				setPrepared(c, Dir.l);
			} else if( dx < dxDontCare) {//visible l
				setVisible(c, Dir.l);
			} else if( dx < dxDontCare2) {//visible r
//				prepareAddition(c, Dir.l);//l is arbitrary
//				setVisible(c, Dir.r);//don't care ;)
			} else if( dx < dxVisibility2) {//visible r
				setVisible(c, Dir.r);
			} else if( dx < dxPreparation2) {//prepared r
				setPrepared(c, Dir.r);
			} else if( dx < dxFree2){//not prepared r
				setFree(c, Dir.r);
			}
			if(dx > dxVisibility && dx < dxVisibility2)
				addInvisibles(c);
			else
				removeInvisibles(c);
		}
	}
	
	private void setVisible(Column c, int end) {
		for(int ttype = 0; ttype < ThingType.types.length; ttype++) {
			if(ThingType.types[ttype].ani == null) continue;
			
			boolean addNeeded = false;
			for(Thing t = c.firstThing(ttype); t != null; t = t.next()){
				if(!isVisible(t)) {
					prepareAddition(t, end);
					addNeeded = true;
				}
				cancelRemoval(t);
			}
			if(addNeeded)
				addPreparedThings(ttype, end);
		}
	}
	private void addInvisibles(Column c) {
		for(int ttype = 0; ttype < ThingType.types.length; ttype++) {
			if(ThingType.types[ttype].ani != null) continue;

			for(Thing t = c.firstThing(ttype); t != null; t = t.next()){
				t.onVisibilityChange(true);
			}
			
		}
	}
	private void removeInvisibles(Column c) {

		for(int ttype = 0; ttype < ThingType.types.length; ttype++) {
			if(ThingType.types[ttype].ani != null) continue;

			for(Thing t = c.firstThing(ttype); t != null; t = t.next()){
				t.onVisibilityChange(false);
			}
			
		}
	}
	public void loadCenter() {
		for(Column c = start(); c != end(); c = c.next()) {
			setPrepared(c, Dir.l);
		}
		for(int type = 0; type < ThingType.types.length; type++) {
			addPreparedThings(type, Dir.l);
		}
	}
	private void setPrepared(Column c, int end) {
		for(int ttype = 0; ttype < ThingType.types.length; ttype++) {
			if(ThingType.types[ttype].ani == null) continue;
			for(Thing t = c.firstThing(ttype); t != null; t = t.next()){
				if(isVisible(t)) {
//					prepareRemoval(t);
				} else {
					prepareAddition(t, end);
				}
			}
		}
	}
	private void setFree(Column c, int end) {
		for(int ttype = 0; ttype < ThingType.types.length; ttype++) {
			if(ThingType.types[ttype].ani == null) continue;
			for(Thing t = c.firstThing(ttype); t != null; t = t.next()){
				if(isVisible(t)) {
					prepareRemoval(t);
				} else if(isPreparedToBeAdded(t)){
					cancelAddition(t, end);
				}
			}
		}
	}
	private boolean isVisible(Thing t) {
		return t.addedToVAO;
	}
	private boolean isPreparedToBeAdded(Thing t) {
		return toAdd[t.getTypeOrdinal()][Dir.l].contains(t) || toAdd[t.getTypeOrdinal()][Dir.r].contains(t); 
	}
	private boolean isPreparedToBeRemoved(Thing t) {
		return t.freeToMakeInvisible;
	}
	
	@Override
	public void moveToColumn(int xIndex) {
		super.moveToColumn(xIndex);
		setCorrectStates();
	}
	
	private void prepareRemoval(Thing t) {
		if(t.selected && !t.freeToMakeInvisible) System.out.println("Preparing removal...");
		t.freeToMakeInvisible = true;
//		prepareAddition(t, iDir);
//		freeToDelete(t);
//		removePreparation(t, iDir);
	}
	
	private void cancelRemoval(Thing t) {
		if(t.selected && t.freeToMakeInvisible) System.out.println("Cancelling removal...");
		t.freeToMakeInvisible = false;
	}
	
	public void freeToDelete(Thing t) {
	}
	
	protected boolean prepareAddition(Thing t, int iDir) {
		
		int type = t.getTypeOrdinal();
		
		if(toAdd[type][iDir].contains(t)) return false;//already listed

		//assign ticket and add thing to array
		toAdd[type][iDir].add(t);
		
		//in case the array is full, set all things visible and print an exception
		if(toAdd[type][iDir].isFull()) {
			addPreparedThings(t.getTypeOrdinal(), iDir);
			System.err.println("Lazy list is too small!");
		}
		if(t.selected) System.out.println("Preparing addition...");
		return true;
	}
	
	protected void cancelAddition(Thing t, int iDir) {

		int type = t.getTypeOrdinal();
		
		toAdd[type][iDir].remove(t);
		if(t.selected) System.out.println("Cancelling addition...");
	}
	
	private void cancel(Thing t, int iDir) {
		if(t.visibilityTicket == -1) {
			if(t.addedToVAO) {
			}
			freeToDelete(t);
			return;
		}
		int type = t.getTypeOrdinal();
		if(toAdd[type][iDir].isEmpty()) {
			System.err.print("There is no thing listed that could be cancelled!");
			return;
		}
		toAdd[type][iDir].remove(t);
		
//		if(t == firstThing) {
//			System.out.println("canceling thing....");
//		}
	}
	
	protected void addPreparedThings(int type, int iDir) {
		if(toAdd[type][iDir].size() <= 0) return;//nothing to add. may happen if the animating is null
//		System.out.println("Adding " + nToAdd[type][iDir] + " prepared " + ThingType.types[type].name + "s!");

		for(int side = 0; side <= 1; side++) {
			
			Thing first = null;
			vaos[type].clearBuffers();//TODO maybe don't use internal buffers, because they're very large
			for(int i = 0 ; i < toAdd[type][iDir].size(); i++) {
				if(DoubleThingVAO.whichSide(toAdd[type][iDir].get(i)) == side && !toAdd[type][iDir].get(i).addedToVAO) {
					
					//add Thing to VAO management
					vaos[type].add(toAdd[type][iDir].get(i), true);
					if(toAdd[type][iDir].get(i).selected) System.out.println("Adding...");
					
					//fill buffers with thing data
					vaos[type].fillBuffers(toAdd[type][iDir].get(side, i));
					
					//find first thing for byte offset
					if(first == null)
						first = toAdd[type][iDir].get(side, i);
				}
			}
			vaos[type].flipBuffers();
			
			//put thing data into VBOs as a block
			if(first != null) {//means no thing on this side
				vaos[type].updateVBOs(first);
			}
		}
		//clear toAdd list
		toAdd[type][iDir].clear();
	}
}
