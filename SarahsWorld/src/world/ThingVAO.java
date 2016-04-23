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
	
	static int usualBytesUpdated =	3*Float.BYTES + 3*Short.BYTES;
	static int unusualBytesUpdated = 4*Byte.BYTES + 2*Float.BYTES;
	static ByteBuffer changer1 = BufferUtils.createByteBuffer(usualBytesUpdated);
	static ByteBuffer changer2 = BufferUtils.createByteBuffer(unusualBytesUpdated);
	
	//in_position	: vec2
	//in_rotation	: float
	//in_texCoords	: vec2
	//in_mirror		: float
	//in_color		: vec4
	//in_z			: float
	//in_size		: float
	
	ThingType type;
	VBO vbo1, vbo2;
	public VAO vao;
	int capacity;
	short lastUsedIndex = -1;
	Thing[] things;
	
	ByteBuffer buffer1, buffer2;
	
	public ThingVAO(ThingType type){
		this.type = type;
		this.capacity = type.maxVisible;
		this.things = new Thing[capacity];
		buffer1 = BufferUtils.createByteBuffer(capacity*usualBytesUpdated);
		buffer2 = BufferUtils.createByteBuffer(capacity*unusualBytesUpdated);
		
		vbo1 = new VBO(buffer1, GL15.GL_DYNAMIC_DRAW, usualBytesUpdated,
				new VAP(2, GL11.GL_FLOAT, false, 0),//vec2 in_position
				new VAP(1, GL11.GL_FLOAT, false, 8),//float in_rotation
				new VAP(2, GL11.GL_SHORT, true, 12),//vec2 in_texCoords
				new VAP(1, GL11.GL_SHORT, true, 16));//float in_mirror
		vbo2 = new VBO(buffer2, GL15.GL_STATIC_DRAW, unusualBytesUpdated,
				new VAP(4, GL11.GL_BYTE, true, 0),//vec4 in_color
				new VAP(1, GL11.GL_FLOAT, false, 4),//float in_z
				new VAP(1, GL11.GL_FLOAT, false, 8)//float in_size
				);
		vao = new VAO(null, vbo1, vbo2);
	}
	
	public void changeUsual(Thing t){
		if(t.index == -1){
			(new Exception("Thing is not registered")).printStackTrace();
			return;
		}
		changer1.clear();
		changer1.putFloat((float)t.pos.x);
		changer1.putFloat((float)(t.pos.y + t.yOffset));
		changer1.putFloat((float)t.rotation);
		changer1.putShort((short)(Short.MAX_VALUE*t.ani.tex.texCoords[0]));
		changer1.putShort((short)(Short.MAX_VALUE*t.ani.tex.texCoords[1]));
		changer1.putShort((short)(Short.MAX_VALUE*(t.dir ? t.ani.tex.texCoords[2] - t.ani.tex.texCoords[0] : 0)));
		changer1.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo1.handle);
		Core.checkGLErrors(true, true, "debug 1");
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, t.index*usualBytesUpdated, changer1);
		Core.checkGLErrors(true, true, t.index + "  " + capacity + "  " + (changer1.capacity()/usualBytesUpdated) + "  " + (GL15.glGetBufferParameter(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE)/usualBytesUpdated));
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}
	
	public void changeUnusual(Thing t){
		changer2.clear();
		changer2.put((byte)(Byte.MAX_VALUE*t.color.r));
		changer2.put((byte)(Byte.MAX_VALUE*t.color.g));
		changer2.put((byte)(Byte.MAX_VALUE*t.color.b));
		changer2.put((byte)(Byte.MAX_VALUE*t.color.a));
		changer2.putFloat(t.behind);
		changer2.putFloat(t.size);
		changer2.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo2.handle);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, t.index*unusualBytesUpdated, changer2);
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
			new Exception("You removed one too much!!!!").printStackTrace();
			System.exit(-1);
		}
		//VBO 1
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo1.handle);
		GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, lastUsedIndex*usualBytesUpdated, changer1);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, t.index*usualBytesUpdated, changer1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		//VBO 2
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo2.handle);
		GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, lastUsedIndex*unusualBytesUpdated, changer2);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, t.index*unusualBytesUpdated, changer2);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		short index = t.index;
		things[index] = things[lastUsedIndex];
		things[index].index = index;
		things[lastUsedIndex] = null;
		t.index = -1;
		
		lastUsedIndex--;
	}
	
	public void enlarge(){
		capacity *= 1.1;
		//VBO 1
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo1.handle);
		GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0, this.buffer1);
		ByteBuffer buffer1 = BufferUtils.createByteBuffer((int)(capacity*usualBytesUpdated));
		buffer1.put(this.buffer1);
		buffer1.put(new byte[buffer1.capacity()-this.buffer1.capacity()]);
		buffer1.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer1, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		this.buffer1 = buffer1;
		//VBO 2
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo2.handle);
		GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0, this.buffer2);
		ByteBuffer buffer2 = BufferUtils.createByteBuffer((int)(capacity*unusualBytesUpdated));
		buffer2.put(this.buffer2);
		buffer2.put(new byte[buffer2.capacity()-this.buffer2.capacity()]);
		buffer2.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer2, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		this.buffer2 = buffer2;
		
		Thing[] newThings = new Thing[capacity];
		System.arraycopy(things, 0, newThings, 0, things.length);
		things = newThings;
	}
}
