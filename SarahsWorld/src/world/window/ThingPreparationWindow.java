package world.window;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import basis.entities.Entity;
import basis.entities.EntitySet;
import basis.entities.Species;
import moveToLWJGLCore.Dir;
import world.data.Column;
import world.data.ColumnListElement;
import world.render.DoubleThingVAO;


/**
 * A larger window than ThingWindow that lists the things it encounters so they can be added together at a later time.
 */
public class ThingPreparationWindow extends RealWorldWindow {
	
	private static int addBatchSize = 70;
	
	boolean start;

	private DoubleThingVAO[] vaos;
	@SuppressWarnings("unchecked")
	private EntitySet<Entity>[][] toAdd = new EntitySet[Species.types.length][2];
	@SuppressWarnings("unchecked")
	private EntitySet<Entity>[] toRemove = new EntitySet[Species.types.length];
	
	private int dxPreparation, dxVisibility, dxDontCare, dxPreparation2, dxVisibility2, dxDontCare2, dxFree2;

	public ThingPreparationWindow(ColumnListElement anchor, int rObservation, int rPreparation, int rVisibility, int rDontCare, DoubleThingVAO[] vaos) {
		super(anchor, rObservation);
		this.dxPreparation = rObservation - rPreparation;
		this.dxVisibility = rObservation - rVisibility;
		this.dxDontCare = rObservation - rDontCare;
		this.dxDontCare2 = rObservation+1 + rDontCare;
		this.dxVisibility2 = rObservation+1 + rVisibility;
		this.dxPreparation2 = rObservation+1 + rPreparation;
		this.dxFree2 = rObservation+1 + rObservation;
		this.vaos = vaos;
		for(int i = 0; i < Species.types.length; i++){
			int maxVisible = Species.types[i].maxVisible;
			toAdd[i][0] = new EntitySet<Entity>(0, addBatchSize);//TODO use fraction of 'max visible' number instead of addBatchSize
			toAdd[i][1] = new EntitySet<Entity>(1, addBatchSize);
			toRemove[i] = new EntitySet<Entity>(2, maxVisible);
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
		for(int ttype = 0; ttype < Species.types.length; ttype++) {
			vaos[ttype].free(ends[Dir.l].column().getX(), ends[Dir.l].column().getX() + (Column.COLUMN_WIDTH*dxPreparation));
			vaos[ttype].free(ends[Dir.l].column().getX() + (Column.COLUMN_WIDTH*dxPreparation2), ends[Dir.r].column().getX());
		}
		int xStart = start().getIndex();
		for(ColumnListElement c = start(); c != end(); c = c.next()) {
			int dx = c.getIndex() - xStart; 
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
	private void setFree(ColumnListElement c, int end) {
		for(int ttype = 0; ttype < Species.types.length; ttype++) {
			if(Species.types[ttype].ani == null) continue;
			for(Entity t = c.column().firstThing(ttype); t != null; t = t.next()){
				if(isVisible(t)) {
					prepareRemoval(t);
				} else if(isPreparedToBeAdded(t)){
					cancelAddition(t, end);
				}
			}
		}
	}
	private void setPrepared(ColumnListElement c, int end) {
		for(int ttype = 0; ttype < Species.types.length; ttype++) {
			if(Species.types[ttype].ani == null) continue;
			for(Entity t = c.column().firstThing(ttype); t != null; t = t.next()){
				if(!isVisible(t)) {
					prepareAddition(t, end);
				}//else no need to be added
				cancelRemoval(t);
			}
		}
	}
	private void setVisible(ColumnListElement c, int end) {
		for(int ttype = 0; ttype < Species.types.length; ttype++) {
			if(Species.types[ttype].ani == null) continue;
			
			boolean addNeeded = false;
			for(Entity t = c.column().firstThing(ttype); t != null; t = t.next()){
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
	private void addInvisibles(ColumnListElement c) {
		
		//the attachements of things that aren't rendered are updated
		//rendered things have an Animating plugin, which completes this task
		for(int ttype = 0; ttype < Species.types.length; ttype++)
			if(Species.types[ttype].ani == null && Species.types[ttype].attachment != null)
				for(Entity t = c.column().firstThing(ttype); t != null; t = t.next())
					t.attachment.onVisibilityChange(true);
	}
	private void removeInvisibles(ColumnListElement c) {

		//the attachements of things that aren't rendered are updated
		for(int ttype = 0; ttype < Species.types.length; ttype++)
			if(Species.types[ttype].ani == null && Species.types[ttype].attachment != null)
				for(Entity t = c.column().firstThing(ttype); t != null; t = t.next())
					t.attachment.onVisibilityChange(false);
	}
	private boolean isPreparedToBeAdded(Entity t) {
		return toAdd[t.type.ordinal][Dir.l].contains(t) || toAdd[t.type.ordinal][Dir.r].contains(t); 
	}
	private boolean isVisible(Entity t) {
		return t.aniPlug.addedToVAO();
	}

	private void prepareRemoval(Entity t) {
		t.aniPlug.setFreeToMakeInvisible( true);
	}
	public void loadCenter() {
		start = true;
		for(ColumnListElement c = start(); c != end(); c = c.next()) {
			setPrepared(c, Dir.l);
		}
		for(int type = 0; type < Species.types.length; type++) {
			addPreparedThings(type, Dir.l);
		}
		start = false;
	}
	
	private void cancelRemoval(Entity t) {
		t.aniPlug.setFreeToMakeInvisible( false);
	}
	
	protected boolean prepareAddition(Entity t, int iDir) {
		
		int type = t.type.ordinal;

		if(toAdd[type][iDir].contains(t)) return false;//already listed
		if(!start && toAdd[type][1-iDir].contains(t)) {
			throw new RuntimeException("Thing is added at other end!?");
		}

		//assign ticket and add thing to array
		toAdd[type][iDir].add(t);
		
		//in case the array is full, set all things visible and print an exception
		if(toAdd[type][iDir].isFull()) {
			addPreparedThings(t.type.ordinal, iDir);
			System.err.println("Lazy list is too small! " + t.type.name);
		}
		return true;
	}
	
	protected void cancelAddition(Entity t, int iDir) {

		int type = t.type.ordinal;
		
		toAdd[type][iDir].remove(t);
	}
	
	List<Entity> sideToAdd = new ArrayList<>();
	
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
			for(Entity t : toAdd[type][iDir]) {
				if(DoubleThingVAO.whichSide(t.aniPlug) == side && !t.aniPlug.addedToVAO()) {
					
					//add Thing to VAO management
					vaos[type].add(t.aniPlug, true, false);
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
			for(Entity t : sideToAdd) {
				//fill buffers with thing data
//				vaos[type].fillBuffers(t);
				vaos[type].fillBuffers(t.aniPlug, buffers[0], buffers[1]);
			}
//			vaos[type].flipBuffers();
			buffers[0].flip(); buffers[1].flip();

			//put thing data into VBOs as a block
			if(sideToAdd.size() > 0) {//means no thing on this side
//				vaos[type].updateVBOs(sideToAdd.get(0));
				vaos[type].updateVBOs(sideToAdd.get(0).aniPlug, buffers[0], buffers[1]);
			} else {
				System.out.println("nothing to add on " + Dir.names[side] + " side.");
			}
		}
		//clear toAdd list
		toAdd[type][iDir].clear();
	}
}
