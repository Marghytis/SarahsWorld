package world.render;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import render.VAO;
import render.VBO;
import render.VBO.VAP;
import things.aiPlugins.Animating.AnimatingPlugin;
import world.window.ThingWindow;

public class ThingVAO {
	
	public static final int USUAL = 0, UNUSUAL = 1;
	public static int[] 		bytesUpdated = new int[2];
	private static ByteBuffer[] changer = new ByteBuffer[2];
	protected VBO[] 				vbo = new VBO[2];
	private VBOContent[][]		contents = new VBOContent[2][];
	
	private VAO vao;
	protected int capacity;
	private short lastUsedIndex = -1;
	protected AnimatingPlugin[] things;
	
	protected int size() {
		return lastUsedIndex + 1;
	}
	
	protected int end() {
		return lastUsedIndex;
	}
	
	protected int start() {
		return 0;
	}
	
	protected int nextUsedIndex(int i) {
		return i+1;
	}
	
	protected boolean empty() {
		return lastUsedIndex <= -1;
	}
	
	public void bindStuff() {
		vao.bindStuff();
	}
	
	public void unbindStuff() {
		vao.unbindStuff();
	}
	
	protected AnimatingPlugin getThing(int index) {
		return things[index];
	}
	
	public ThingVAO(int capacity){
		this.capacity = capacity;
		this.things = new AnimatingPlugin[capacity];

		//Create VBO with data usually updated every tick
		VBO vboUsual = createVBO(new VBOContent[]{
			new VBOContent(2, GL11.GL_FLOAT, false,//vec2 in_position
					(t) -> (float)t.pos().x,
					(t) -> (float)(t.pos().y + t.getYOffset() + t.getYOffsetToBalanceRotation())),
			
			new VBOContent(1, GL11.GL_FLOAT, false,//float in_rotation
					(t) -> (float)(t.getRotation() + t.getAniRotation())),
			
			new VBOContent(2, GL11.GL_SHORT, true,//vec2 in_texCoords
					(t)-> (short)(Short.MAX_VALUE*t.getAnimator().tex.texCoords[0]),
					(t)-> (short)(Short.MAX_VALUE*t.getAnimator().tex.texCoords[1])),
			
			new VBOContent(1, GL11.GL_FLOAT, false,//float in_mirror
					(t) -> (float)((t.getOrientation() ? t.getAnimator().tex.w : 0)/(float)t.getAnimator().tex.file.width))}, 0);

		//Create VBO with data not usually updated
		VBO vboUnusual = createVBO(new VBOContent[]{
			new VBOContent(4, GL11.GL_BYTE, true,//vec4 in_color
					(t) -> (byte)(Byte.MAX_VALUE*t.getColor().r),
					(t) -> (byte)(Byte.MAX_VALUE*t.getColor().g),
					(t) -> (byte)(Byte.MAX_VALUE*t.getColor().b),
					(t) -> (byte)(Byte.MAX_VALUE*t.getColor().a)),
			new VBOContent(1, GL11.GL_FLOAT, false,//float in_z
					(t) -> (float)t.getZ()),
			new VBOContent(1, GL11.GL_FLOAT, false,//float in_size
					(t) -> (float)t.getThing().getSize()),
			new VBOContent(4, GL11.GL_SHORT, false,//vec4 in_box
					(t) -> (short)t.getRenderBox().pos.x,
					(t) -> (short)t.getRenderBox().pos.y,
					(t) -> (short)(t.getRenderBox().pos.x + t.getRenderBox().size.x),
					(t) -> (short)(t.getRenderBox().pos.y + t.getRenderBox().size.y)),
			new VBOContent(2, GL11.GL_FLOAT, true,//vec2 in_texWH
					(t) -> (float)((float)t.getAnimator().tex.w/t.getAnimator().tex.file.width),
					(t) -> (float)((float)t.getAnimator().tex.h/t.getAnimator().tex.file.height)
					)}, 1);
		
		//combine VBOs into VAO
		vao = new VAO(null, vboUsual, vboUnusual);
	}
	
	/**
	 * Create a VBO with the given data types
	 * @param params {amount[], type[], normalized[], changer[]}
	 */
	private VBO createVBO(VBOContent[] params, int type){
		contents[type] = params;
		VAP[] vaps = new VAP[params.length];
		int shift = 0;
		for(int i = 0; i < vaps.length; i++){
			vaps[i] = new VAP(params[i].amount, params[i].type, params[i].normalized, shift);
			shift += params[i].getBytes()*params[i].amount;
		}
		bytesUpdated[type] = shift;
		changer[type] = BufferUtils.createByteBuffer(bytesUpdated[type]);
		ByteBuffer buffer = BufferUtils.createByteBuffer(capacity*bytesUpdated[type]);
		vbo[type] = new VBO(buffer,  type == 0 ? GL15.GL_STREAM_DRAW : GL15.GL_DYNAMIC_DRAW, bytesUpdated[type], vaps);
		
		return vbo[type];
	}
	
	public interface Getter {
		public Object get(AnimatingPlugin t);
	}

	public void changeUsual(AnimatingPlugin t){
		changeUsual(t, false);
	}
	public void changeUnusual(AnimatingPlugin t){
		changeUnusual(t, false);
	}
	public void changeUsual(AnimatingPlugin t, boolean inBatch) {
		change(t, 0, inBatch);
	}
	public void changeUnusual(AnimatingPlugin t, boolean inBatch) {
		change(t, 1, inBatch);
	}

	private void change(AnimatingPlugin t, int index, boolean inBatch){
		if(!inBatch)
			startBatchAdder(index);
		
		changeNoBinding(t, index);
		
		if(!inBatch)
			endBatchAdder();
	}
	
	public void updateVBO(AnimatingPlugin firstThing, ByteBuffer buffer, int index) {
		startBatchAdder(index);
			updateVBOBatch(firstThing, buffer, index);
		endBatchAdder();
	}
	
	public void updateVBOs(AnimatingPlugin firstThing) {
		updateVBOs(firstThing, vbo[0].buffer, vbo[1].buffer);
	}
	
	public void updateVBOs(AnimatingPlugin firstThing, ByteBuffer usual, ByteBuffer unusual) {
		startBatchAdder(0);
			updateVBOBatch(firstThing, usual, 0);
		startBatchAdder(1);
			updateVBOBatch(firstThing, unusual, 1);
		endBatchAdder();
	}
	
	public void updateVBOBatch(AnimatingPlugin firstThing, ByteBuffer buffer, int index) {
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, firstThing.getIndex()*bytesUpdated[index], buffer);	
	}
	
	public void fillBuffer(AnimatingPlugin t, ByteBuffer buffer, int index) {
		for(int i = 0; i < contents[index].length; i++){
			contents[index][i].change(buffer, t);
		}
	}
	
	public void clearBuffers() {
		vbo[0].buffer.clear();
		vbo[1].buffer.clear();
	}
	
	public void flipBuffers() {
		vbo[0].buffer.flip();
		vbo[1].buffer.flip();
	}
	
	public void fillBuffers(AnimatingPlugin t) {
		fillBuffers(t, vbo[0].buffer, vbo[1].buffer);
	}
	
	public void fillBuffers(AnimatingPlugin t, ByteBuffer bufferUsual, ByteBuffer bufferUnusual) {
		fillBuffer(t, bufferUsual, USUAL);
		fillBuffer(t, bufferUnusual, UNUSUAL);
	}
	
	private void changeNoBinding(AnimatingPlugin t, int index) {
		if(t != null && t.getIndex() == -1){
			(new Exception("Thing is not registered")).printStackTrace();
			return;
		}
		changer[index].clear();
			fillBuffer(t, changer[index], index);
		changer[index].flip();
		updateVBOBatch(t, changer[index], index);
	}

	public void add(AnimatingPlugin t, boolean inBatch){

		if(lastUsedIndex+1 >= capacity){
			removeFreedThings();
		}
		lastUsedIndex++;
		if(lastUsedIndex >= capacity){//yes, same if!!
			System.err.println("Not enough space for " + t.getType().name + "s! Current capacity: " + capacity + " quads. Default: " + t.getType().maxVisible);
			enlarge();
		}
		t.onVisibilityChange(true);
		things[lastUsedIndex] = t;
		t.setIndex(lastUsedIndex);
		t.setAddedToVAO(true);
		if(!inBatch) {
			changeUsual(t);
			changeUnusual(t);
		}
	}
	
	public void startBatchAdderUsual() {
		startBatchAdder(0);
	}
	public void startBatchAdderUnusual() {
		startBatchAdder(1);
	}
	
	private void startBatchAdder(int type) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo[type].handle);
	}
	
	public void endBatchAdder() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void remove(AnimatingPlugin t){
		remove(t, true);
	}
	
	public void remove(AnimatingPlugin t, boolean vboAsWell) {
		if(lastUsedIndex < 0){
			new Exception("You removed one " + t.getType().name + " too much!!!!").printStackTrace();
			return;
		}
		if(t.getIndex() == -1) {
			new Exception("This " + t.getType().name + " is already deleted in the VAO!!");
			return;
		}
		t.onVisibilityChange(false);
		
		//move last thing in the list to t's location and update lastUsedIndex
		moveThing(lastUsedIndex, t.getIndex(), vboAsWell);
		lastUsedIndex--;
		
		t.setIndex((short)-1);
		t.setAddedToVAO(false);
		t.setFreeToMakeInvisible(false);//reset this flag
	}
	
	protected void copyVBOdata(int iFrom, int iTo) {
		for(int i = 0; i < vbo.length; i++){
			//move data from last thing to the removed things position
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo[i].handle);
			GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, iFrom*bytesUpdated[i], changer[i]);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, iTo*bytesUpdated[i], changer[i]);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
	}
	
	public void refillBuffers() {
		for(int type = 0; type <= 1; type++) {
			vbo[type].buffer.clear();
			if(type == 1 && ThingWindow.print) System.out.print("[");
		
			for(int index = 0; index < things.length; index++) {
				fillBuffer(things[index], vbo[type].buffer, type);
				if(type == 1 && ThingWindow.print) System.out.print((things[index] == null ? "-" : things[index].getIndex()) + ",");
			}
			if(type == 1 && ThingWindow.print) System.out.println("]");
			vbo[type].buffer.flip();
			vbo[type].update();
		}
	}
	
	protected void copyThing(int iFrom, int iTo, boolean vboAsWell) {
		if(vboAsWell) copyVBOdata(iFrom, iTo);
		int index = iTo;
		things[index] = things[iFrom];
		things[index].setIndex((short) iTo);
	}
	
	protected void moveThing(int iFrom, int iTo, boolean vboAsWell) {
		copyThing(iFrom, iTo, vboAsWell);
		things[iFrom] = null;
	}
	
	public void removeFreedThings() {
		for(int i = 0; i < things.length; i++){
			if(things[i] != null && things[i].freeToMakeInvisible()) {
				remove(things[i], false);
			}
		}
		refillBuffers();
	}
	
	public void enlarge(){
		capacity *= 1.5;
		for(int i = 0; i < vbo.length; i++){
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo[i].handle);
			GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vbo[i].buffer);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			ByteBuffer temp = vbo[i].buffer;
			vbo[i].buffer = BufferUtils.createByteBuffer((int)(capacity*bytesUpdated[i]));
			vbo[i].buffer.put(temp);
			vbo[i].buffer.put(new byte[vbo[i].buffer.capacity()-temp.capacity()]);
			vbo[i].buffer.flip();
			vbo[i].update();
		}
		
		AnimatingPlugin[] newThings = new AnimatingPlugin[capacity];
		System.arraycopy(things, 0, newThings, 0, things.length);
		things = newThings;
	}
	
	private class VBOContent {
		public int amount, type;
		public boolean normalized;
		Getter[] getters;
		
		public VBOContent(int amount, int type, boolean normalized, Getter... getters) {
			this.amount = amount;
			this.type = type;
			this.normalized = normalized;
			this.getters = getters;
			if(amount != getters.length){
				new Exception("Number of elements doesn't match the specified amount!").printStackTrace();
				System.exit(-1);
			}
		}
		
		public void change(ByteBuffer buffer, AnimatingPlugin t){
			
			if(t == null) {
				fillWith0(buffer);
			} else {
				fillWithThing(buffer, t);
			}
		}
		
		private void fillWithThing(ByteBuffer buffer, AnimatingPlugin t) {
			switch(type){
			case GL11.GL_FLOAT:
				for(int i = 0; i < getters.length; i++)		buffer.putFloat((float)get(t, i)); break;
				
			case GL11.GL_BYTE:
				for(int i = 0; i < getters.length; i++)		buffer.put((byte)get(t, i)); break;
				
			case GL11.GL_SHORT:
				for(int i = 0; i < getters.length; i++)		buffer.putShort((short)get(t, i)); break;
				
			case GL11.GL_INT:
				for(int i = 0; i < getters.length; i++)		buffer.putInt((int)get(t, i)); break;
				
			default:
				new Exception("Unknown data type!!!").printStackTrace();
			}
		}
		
		private void fillWith0(ByteBuffer buffer) {
			switch(type){
			case GL11.GL_FLOAT:
				for(int i = 0; i < getters.length; i++)		buffer.putFloat((float)0); break;
				
			case GL11.GL_BYTE:
				for(int i = 0; i < getters.length; i++)		buffer.put((byte)0); break;
				
			case GL11.GL_SHORT:
				for(int i = 0; i < getters.length; i++)		buffer.putShort((short)0); break;
				
			case GL11.GL_INT:
				for(int i = 0; i < getters.length; i++)		buffer.putInt((int)0); break;
				
			default:
				new Exception("Unknown data type!!!").printStackTrace();
			}
		}
		
		private Object get(AnimatingPlugin t, int i) {
			return getters[i].get(t);
		}
		
		public int getBytes(){
			switch(type){
			case GL11.GL_FLOAT: return Float.BYTES;
			case GL11.GL_BYTE: return Byte.BYTES;
			case GL11.GL_SHORT: return Short.BYTES;
			case GL11.GL_INT: return Integer.BYTES;
			default: return 0;
			}
		}
	}
}
