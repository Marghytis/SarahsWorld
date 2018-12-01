package world.window;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import main.Main;
import things.Thing;
import things.ThingType;
import world.data.Column;
import world.render.DoubleThingVAO;
import world.render.ThingVAO;


/**
 * A larger window than ThingWindow that lists the things it encounters so they can be added together at a later time.
 */
public class ThingPreparationWindow extends RealWorldWindow {
	
	private static int addBatchSize = 70;

	private DoubleThingVAO[] vaos;
	private Thing[][][] toAdd = new Thing[ThingType.types.length][2][addBatchSize];
	private int[][] nToAdd = new int[toAdd.length][toAdd[0].length];
	private ByteBuffer[] buffersUsual = new ByteBuffer[toAdd.length];
	private ByteBuffer[] buffersUnusual = new ByteBuffer[toAdd.length];

	public ThingPreparationWindow(Column anchor, int radius, DoubleThingVAO[] vaos) {
		super(anchor, radius);
		this.vaos = vaos;	
		for(int i = 0; i < ThingType.types.length; i++){
			buffersUsual[i] = BufferUtils.createByteBuffer(addBatchSize*ThingVAO.bytesUpdated[ThingVAO.USUAL]);
			buffersUnusual[i] = BufferUtils.createByteBuffer(addBatchSize*ThingVAO.bytesUpdated[ThingVAO.UNUSUAL]);
		}
	}
	
	public Thing firstThing;
	protected void shiftOutwards(int iDir) {
		super.shiftOutwards(iDir);

		for(int i = 0; i < ThingType.types.length; i++)
		if(ThingType.types[i].ani != null)
		for(Thing t = ends[iDir].firstThing(i); t != null; t = t.next())
			
			prepare(t, iDir);
		
		
	}
	protected void shiftInwards(int end) {

		for(int i = 0; i < ThingType.types.length; i++)
		if(ThingType.types[i].ani != null)
		for(Thing t = ends[end].firstThing(i); t != null; t = t.next())
			
			cancel(t, end);
		
		super.shiftInwards(end);
	}
	
	protected boolean prepare(Thing t, int iDir) {
		if(t.visibilityTicket != -1) return false;//already listed
		
		if(firstThing == null) {
			firstThing = t;
			t.selected = true;
		}
		
		int type = t.getTypeOrdinal();

		//assign ticket and add thing to array
		t.visibilityTicket = nToAdd[type][iDir];
		toAdd[type][iDir][t.visibilityTicket] = t;
		nToAdd[type][iDir]++;
		
		//in case the array is full, set all things visible and print an exception
		if(nToAdd[type][iDir] >= addBatchSize) {
			addPreparedThings(t.getTypeOrdinal(), iDir);
			System.err.println("Lazy list ist too small!");
		}
		if(t == firstThing) {
			System.out.println("preparing thing.... nToAdd: " + nToAdd[type][iDir]);
		}
		return true;
	}
	
	private void cancel(Thing t, int iDir) {
		if(t.visibilityTicket == -1) {
			if(t.addedToVAO) {
				Main.world.thingWindow.remove(t);
			}
			return;
		}
		int type = t.getTypeOrdinal();
		if(nToAdd[type][iDir] <= 0) {
			System.err.print("There is no thing listed that could be cancelled!");
			return;
		}
		nToAdd[type][iDir]--;
		toAdd[type][iDir][t.visibilityTicket] = toAdd[type][iDir][nToAdd[type][iDir]];
		toAdd[type][iDir][nToAdd[type][iDir]].visibilityTicket = t.visibilityTicket;
		toAdd[type][iDir][nToAdd[type][iDir]] = null;
		t.visibilityTicket = -1;
		
		if(t == firstThing) {
			System.out.println("canceling thing....");
		}
	}
	
	protected void addPreparedThings(int type, int iDir) {
		if(nToAdd[type][iDir] <= 0) return;//nothing to add. may happen if the animating is null
		System.out.println("Adding " + nToAdd[type][iDir] + " prepared " + ThingType.types[type].name + "s!");

		for(int side = 0; side <= 1; side++) {
			Thing first = null, last = null;
			
			for(int i = 0 ; i < nToAdd[type][iDir]; i++) {
				if(DoubleThingVAO.whichSide(toAdd[type][iDir][i]) == side) {
					if(first == null)
						first = toAdd[type][iDir][i];
					last = toAdd[type][iDir][i];

					if(toAdd[type][iDir][i] == firstThing) {
						System.out.println("adding thing....");
					}
					vaos[type].add(toAdd[type][iDir][i], true);
//					System.out.println(toAdd[type][iDir][i].index);
				}
			}
			buffersUsual[type].clear();
			buffersUnusual[type].clear();
			for(int i = 0 ; i < nToAdd[type][iDir]; i++) {
				if(DoubleThingVAO.whichSide(toAdd[type][iDir][i]) == side) {
					if(side == DoubleThingVAO.lowerSide) {
						vaos[type].fillBuffer(toAdd[type][iDir][i], buffersUsual[type], ThingVAO.USUAL);
						vaos[type].fillBuffer(toAdd[type][iDir][i], buffersUnusual[type], ThingVAO.UNUSUAL);
					} else {
						vaos[type].fillBuffer(toAdd[type][iDir][nToAdd[type][iDir] - 1 - i], buffersUsual[type], ThingVAO.USUAL);
						vaos[type].fillBuffer(toAdd[type][iDir][nToAdd[type][iDir] - 1 - i], buffersUnusual[type], ThingVAO.UNUSUAL);
					}
				}
			}
			buffersUsual[type].flip();
			buffersUnusual[type].flip();
//			buffersUsual[type].limit(nToAdd[type]*ThingVAO.bytesUpdated[ThingVAO.USUAL]);
//			buffersUnusual[type].limit(nToAdd[type]*ThingVAO.bytesUpdated[ThingVAO.UNUSUAL]);
			if(first != null) {//means no thing on this side
				vaos[type].updateVBO(side == DoubleThingVAO.lowerSide ? first : last, buffersUsual[type], ThingVAO.USUAL);
				vaos[type].updateVBO(side == DoubleThingVAO.lowerSide ? first : last, buffersUnusual[type], ThingVAO.UNUSUAL);
			}
		}
		
		for(int i = 0; i < nToAdd[type][iDir]; i++) {
			toAdd[type][iDir][i].onVisibilityChange(true);
			toAdd[type][iDir][i].visibilityTicket = -1;
			toAdd[type][iDir][i] = null;
		}
		nToAdd[type][iDir] = 0;
	}
}
