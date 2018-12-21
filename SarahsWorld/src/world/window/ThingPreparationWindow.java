package world.window;

import java.nio.ByteBuffer;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

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
	
	boolean start;

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
	
	@Override
	/**
	*	Moves this window to the specified column index
	*/
	public void moveToColumn(int xIndex) {
		super.moveToColumn(xIndex);
		setCorrectStates();
	}
	
	/**
	 * Loops over all things in the window and sets their appropriate state
	 */
	private void setCorrectStates() {
		//remove any things that were missed by the window because of too high speed
		for(int ttype = 0; ttype < ThingType.types.length; ttype++) {
			vaos[ttype].free(ends[Dir.l].xReal, ends[Dir.l].xReal + (Column.COLUMN_WIDTH*dxPreparation));
			vaos[ttype].free(ends[Dir.l].xReal + (Column.COLUMN_WIDTH*dxPreparation2), ends[Dir.r].xReal);
		}
		int xStart = start().xIndex;
		for(Column c = start(); c != end(); c = c.next()) {
			int dx = c.xIndex - xStart; 
			if( dx < dxPreparation) {//not prepared l
				setFree(c, Dir.l);
			} else if( dx < dxVisibility) {//prepared l
				setPrepared(c, Dir.l);
			} else if( dx < dxDontCare) {//visible l
				setVisible(c, Dir.l);
			} else if( dx <= dxDontCare2) {//visible r
//				prepareAddition(c, Dir.l);//l is arbitrary
//				setVisible(c, Dir.r);//don't care ;)
			} else if( dx <= dxVisibility2) {//visible r
				setVisible(c, Dir.r);
			} else if( dx <= dxPreparation2) {//prepared r
				setPrepared(c, Dir.r);
			} else if( dx <= dxFree2){//not prepared r
				setFree(c, Dir.r);
			}
			//things that can't normally be seen can still hold items or effects
			if(dx > dxVisibility && dx < dxVisibility2)
				addInvisibles(c);
			else
				removeInvisibles(c);
		}
	}
	
	/**
	 * Remove the things on this column from the toAdd-list and prepares them to be removed
	 * @param c
	 * @param end
	 */
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
	private void setPrepared(Column c, int end) {
		for(int ttype = 0; ttype < ThingType.types.length; ttype++) {
			if(ThingType.types[ttype].ani == null) continue;
			for(Thing t = c.firstThing(ttype); t != null; t = t.next()){
				if(!isVisible(t)) {
					prepareAddition(t, end);
				}//else no need to be added
				cancelRemoval(t);
			}
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
		
		for(int ttype = 0; ttype < ThingType.types.length; ttype++)
			if(ThingType.types[ttype].ani == null)
				for(Thing t = c.firstThing(ttype); t != null; t = t.next())
					t.onVisibilityChange(true);
	}
	private void removeInvisibles(Column c) {

		for(int ttype = 0; ttype < ThingType.types.length; ttype++)
			if(ThingType.types[ttype].ani == null)
				for(Thing t = c.firstThing(ttype); t != null; t = t.next())
					t.onVisibilityChange(false);
	}
	private boolean isPreparedToBeAdded(Thing t) {
		return toAdd[t.getTypeOrdinal()][Dir.l].contains(t) || toAdd[t.getTypeOrdinal()][Dir.r].contains(t); 
	}
	private boolean isVisible(Thing t) {
		return t.addedToVAO;
	}

	private void prepareRemoval(Thing t) {
		t.freeToMakeInvisible = true;
	}
	public void loadCenter() {
		start = true;
		for(Column c = start(); c != end(); c = c.next()) {
			setPrepared(c, Dir.l);
		}
		for(int type = 0; type < ThingType.types.length; type++) {
			addPreparedThings(type, Dir.l);
		}
		start = false;
	}
	private boolean isPreparedToBeRemoved(Thing t) {
		return t.freeToMakeInvisible;
	}
	
	private void cancelRemoval(Thing t) {
		t.freeToMakeInvisible = false;
	}
	
	protected boolean prepareAddition(Thing t, int iDir) {
		
		int type = t.getTypeOrdinal();

		if(toAdd[type][iDir].contains(t)) return false;//already listed
		if(!start && toAdd[type][1-iDir].contains(t)) {
			throw new RuntimeException("Thing is added at other end!?");
		}

		//assign ticket and add thing to array
		toAdd[type][iDir].add(t);
		
		//in case the array is full, set all things visible and print an exception
		if(toAdd[type][iDir].isFull()) {
			addPreparedThings(t.getTypeOrdinal(), iDir);
			System.err.println("Lazy list is too small! " + t.type.name);
		}
		return true;
	}
	
	protected void cancelAddition(Thing t, int iDir) {

		int type = t.getTypeOrdinal();
		
		toAdd[type][iDir].remove(t);
	}
	
	List<Thing> sideToAdd = new ArrayList<>();
	
	protected void addPreparedThings(int type, int iDir) {
		if(toAdd[type][iDir].size() <= 0) return;//nothing to add. may happen if the animating is null
//		System.out.println("Adding " + nToAdd[type][iDir] + " prepared " + ThingType.types[type].name + "s!");
		
		
		//We have to make space before adding in a batch, as the indices might change when adding otherwise.
		if(toAdd[type][iDir].size() > vaos[type].spaceLeft()) {
			vaos[type].removeFreedThings();
		}

		for(int side = 0; side <= 1; side++) {
			
			//add things to vao (not yet to vbos)
			sideToAdd.clear();
			for(Thing t : toAdd[type][iDir]) {
				if(DoubleThingVAO.whichSide(t) == side && !t.addedToVAO) {
					
					//add Thing to VAO management
					vaos[type].add(t, true, false);
					if(side == DoubleThingVAO.lowerSide) {
						sideToAdd.add(t);
					} else {
						sideToAdd.add(0, t);
					}
				}
			}
			
			//fill buffers
			ByteBuffer[] buffers = {BufferUtils.createByteBuffer(DoubleThingVAO.bytesUpdated[0]*sideToAdd.size()),
					BufferUtils.createByteBuffer(DoubleThingVAO.bytesUpdated[1]*sideToAdd.size())};//TODO very inefficient!!
			
			
//			vaos[type].clearBuffers();//TODO maybe don't use internal buffers, because they're very large
			buffers[0].clear(); buffers[1].clear();
			for(Thing t : sideToAdd) {
				//fill buffers with thing data
//				vaos[type].fillBuffers(t);
				vaos[type].fillBuffers(t, buffers[0], buffers[1]);
			}
//			vaos[type].flipBuffers();
			buffers[0].flip(); buffers[1].flip();

			//put thing data into VBOs as a block
			if(sideToAdd.size() > 0) {//means no thing on this side
//				vaos[type].updateVBOs(sideToAdd.get(0));
				vaos[type].updateVBOs(sideToAdd.get(0), buffers[0], buffers[1]);
			} else {
				System.out.println("nothing to add on " + Dir.names[side] + " side.");
			}
		}
		//clear toAdd list
		toAdd[type][iDir].clear();
	}
}
