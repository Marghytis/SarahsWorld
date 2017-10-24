package world;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import core.Core;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import things.Thing;
import things.ThingType;

public class ThingVAO {
	
	static int[] 		bytesUpdated = new int[2];
	static ByteBuffer[] changer = new ByteBuffer[2];
	VBO[] 				vbo = new VBO[2];
	Changer[]			changerMethod = new Changer[2];
	VBOContent[][]		contents = new VBOContent[2][];
	
	ThingType type;
	public VAO vao;
	int capacity;
	short lastUsedIndex = -1;
	Thing[] things;
	
	private interface Changer {
		public void change(Thing t);
	}
	
	public ThingVAO(ThingType type){
		this.type = type;
		this.capacity = type.maxVisible;
		this.things = new Thing[capacity];

		//Create VBO with data usually updated every tick
		VBO vboUsual = createVBO(new VBOContent[]{
			new VBOContent(2, GL11.GL_FLOAT, false,//vec2 in_position
					(t) -> (float)t.pos.x,
					(t) -> (float)(t.pos.y + t.yOffset)),
			
			new VBOContent(1, GL11.GL_FLOAT, false,//float in_rotation
					(t) -> (float)(t.rotation + t.aniRotation)),
			
			new VBOContent(2, GL11.GL_SHORT, true,//vec2 in_texCoords
					(t)-> (short)(Short.MAX_VALUE*t.ani.tex.texCoords[0]),
					(t)-> (short)(Short.MAX_VALUE*t.ani.tex.texCoords[1])),
			
			new VBOContent(1, GL11.GL_FLOAT, false,//float in_mirror
					(t) -> (float)((t.dir ? t.ani.tex.w : 0)/(float)t.ani.tex.file.width))}, 0);

		//Create VBO with data not usually updated
		VBO vboUnusual = createVBO(new VBOContent[]{
			new VBOContent(4, GL11.GL_BYTE, true,//vec4 in_color
					(t) -> (byte)(Byte.MAX_VALUE*t.color.r),
					(t) -> (byte)(Byte.MAX_VALUE*t.color.g),
					(t) -> (byte)(Byte.MAX_VALUE*t.color.b),
					(t) -> (byte)(Byte.MAX_VALUE*t.color.a)),
			new VBOContent(1, GL11.GL_FLOAT, false,//float in_z
					(t) -> (float)t.z),
			new VBOContent(1, GL11.GL_FLOAT, false,//float in_size
					(t) -> (float)t.size),
			new VBOContent(4, GL11.GL_SHORT, false,//vec4 in_box
					(t) -> (short)t.box.pos.x,
					(t) -> (short)t.box.pos.y,
					(t) -> (short)(t.box.pos.x + t.box.size.x),
					(t) -> (short)(t.box.pos.y + t.box.size.y)),
			new VBOContent(2, GL11.GL_FLOAT, true,//vec2 in_texWH
					(t) -> (float)((float)t.ani.tex.w/t.ani.tex.file.width),
					(t) -> (float)((float)t.ani.tex.h/t.ani.tex.file.height)
					)}, 1);
		
		//combine VBOs into VAO
		vao = new VAO(null, vboUsual, vboUnusual);
	}
	
	/**
	 * Create a VBO with the given data types
	 * @param params {amount[], type[], normalized[], changer[]}
	 */
	private VBO createVBO(VBOContent[] params, int index){
		contents[index] = params;
		VAP[] vaps = new VAP[params.length];
		int shift = 0;
		for(int i = 0; i < vaps.length; i++){
			vaps[i] = new VAP(params[i].amount, params[i].type, params[i].normalized, shift);
			shift += params[i].getBytes()*params[i].amount;
		}
		bytesUpdated[index] = shift;
		changer[index] = BufferUtils.createByteBuffer(bytesUpdated[index]);
		ByteBuffer bufferTemp = BufferUtils.createByteBuffer(capacity*bytesUpdated[index]);
		vbo[index] = new VBO(bufferTemp,  index == 0 ? GL15.GL_STREAM_DRAW : GL15.GL_DYNAMIC_DRAW, bytesUpdated[index], vaps);
		Core.checkGLErrors(true, true, "debug 0");
		
		return vbo[index];
	}
	
	public interface Getter {
		public Object get(Thing t);
	}

	public void changeUsual(Thing t){
		change(t, 0);
	}
	public void changeUnusual(Thing t){
		change(t, 1);
	}
	
	private void change(Thing t, int index){
		if(index == 1 && t.type == ThingType.SARAH){
			System.out.println("test: " + ((float)t.ani.tex.w/t.ani.tex.file.width));
		}
		if(t.index == -1){
			(new Exception("Thing is not registered")).printStackTrace();
			return;
		}
		changer[index].clear();
		for(int i = 0; i < contents[index].length; i++){
			contents[index][i].change(changer[index], t);
		}
		changer[index].flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo[index].handle);
		Core.checkGLErrors(true, true, "debug 1");
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, t.index*bytesUpdated[index], changer[index]);
		Core.checkGLErrors(true, true, t.index + "  " + capacity + "  " + (changer[index].capacity()/bytesUpdated[index]) + "  " + (GL15.glGetBufferParameter(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE)/bytesUpdated[index]));
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void add(Thing t){

		lastUsedIndex++;
		if(lastUsedIndex >= capacity){
			System.err.println("Not enough space for " + type.name + "s! Current capacity: " + capacity + " quads. Default: " + type.maxVisible);
			enlarge();
		}
		things[lastUsedIndex] = t;
		t.index = lastUsedIndex;
		changeUsual(t);
		changeUnusual(t);
	}
	
	public void remove(Thing t){
		if(lastUsedIndex < 0){
			new Exception("You removed one Thing too much!!!!").printStackTrace();
			System.exit(-1);
		}
		for(int i = 0; i < vbo.length; i++){
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo[i].handle);
			GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, lastUsedIndex*bytesUpdated[i], changer[i]);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, t.index*bytesUpdated[i], changer[i]);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		short index = t.index;
		things[index] = things[lastUsedIndex];
		things[index].index = index;
		things[lastUsedIndex] = null;
		t.index = -1;
		
		lastUsedIndex--;
		Core.checkGLErrors(true, true, "debug 0");
	}
	
	public void enlarge(){
		capacity *= 1.5;
		for(int i = 0; i < vbo.length; i++){
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo[i].handle);
			GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vbo[i].buffer);
			ByteBuffer bufferTemp = BufferUtils.createByteBuffer((int)(capacity*bytesUpdated[i]));
			bufferTemp.put(vbo[i].buffer);
			bufferTemp.put(new byte[bufferTemp.capacity()-vbo[i].buffer.capacity()]);
			bufferTemp.flip();
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferTemp, i == 0 ? GL15.GL_STREAM_DRAW : GL15.GL_DYNAMIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			vbo[i].buffer = bufferTemp;
		}
		
		Thing[] newThings = new Thing[capacity];
		System.arraycopy(things, 0, newThings, 0, things.length);
		things = newThings;
		Core.checkGLErrors(true, true, "debug 0");
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
		
		public void change(ByteBuffer buffer, Thing t){
			switch(type){
			case GL11.GL_FLOAT:
				for(int i = 0; i < getters.length; i++)		buffer.putFloat((float)getters[i].get(t)); break;
				
			case GL11.GL_BYTE:
				for(int i = 0; i < getters.length; i++)		buffer.put((byte)getters[i].get(t)); break;
				
			case GL11.GL_SHORT:
				for(int i = 0; i < getters.length; i++)		buffer.putShort((short)getters[i].get(t)); break;
				
			case GL11.GL_INT:
				for(int i = 0; i < getters.length; i++)		buffer.putInt((int)getters[i].get(t)); break;
				
			default:
				new Exception("Unknown data type!!!").printStackTrace();
			}
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
