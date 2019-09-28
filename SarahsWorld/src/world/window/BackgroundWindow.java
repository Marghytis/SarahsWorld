package world.window;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import basis.exceptions.WorldTooSmallException;
import extra.Res;
import render.Render;
import render.Shader;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import world.data.Column;
import world.data.ColumnListElement;

public class BackgroundWindow extends ArrayWorldWindow {

	private static int 	indicesPerQuad = 6,
			verticesPerPoint = 3, 
			bytesPerVertex = 2*Float.BYTES + 4*Byte.BYTES;
	private static byte[] light = {0, 0, 0, 0}, dark = {0, 0, 0, 127};

	private static float darknessDistance = 600;

	private VAO vaoColor;
	int pointsX;
	byte[] color = new byte[4];//use 'color' only cleared
	ByteBuffer changerColor = BufferUtils.createByteBuffer(bytesPerVertex*verticesPerPoint);
	
	public BackgroundWindow(ColumnListElement anchor, int radius) throws WorldTooSmallException {
		super(anchor, radius);
		this.pointsX = columns.length;
		vaoColor = new VAO(
				new VBO(TerrainWindow.createIndexBuffer(pointsX, 2), GL15.GL_STATIC_DRAW),
				new VBO(createVertexBufferColor(), GL15.GL_DYNAMIC_DRAW, bytesPerVertex,
						new VAP(2, GL11.GL_FLOAT, false, 0),//in_Position
						new VAP(4, GL11.GL_BYTE, true, 2*Float.BYTES)));//in_Color
	}
	
	public void renderBackground(){

		//BACKGROUND
		Res.darknessShader.bind();
		Res.darknessShader.set("transform",  Render.offsetX, 0, Render.scaleX, 1);
		vaoColor.bindStuff();
			drawBackground();
		vaoColor.unbindStuff();
		Shader.bindNone();
	}
	
	public void renderDarkness(){
		//DARKNESS
		Res.darknessShader.bind();
		Res.darknessShader.set("transform", Render.offsetX, Render.offsetY, Render.scaleX, Render.scaleY);
		vaoColor.bindStuff();
			drawDarkness();
		vaoColor.unbindStuff();
		Shader.bindNone();
	}
	public void drawDarkness(){
		int realShift = (startIndexLeft()+pointsX-1)%pointsX;
		//Transition part
		GL11.glDrawElements(GL11.GL_TRIANGLES, realShift*indicesPerQuad, GL11.GL_UNSIGNED_INT, 0*indicesPerQuad*Integer.BYTES);
		if(realShift != pointsX-1)
		GL11.glDrawElements(GL11.GL_TRIANGLES, (pointsX - 1 - realShift)*indicesPerQuad, GL11.GL_UNSIGNED_INT, (realShift+1)*indicesPerQuad*Integer.BYTES);
		//complete dark part
		GL11.glDrawElements(GL11.GL_TRIANGLES, realShift*indicesPerQuad, GL11.GL_UNSIGNED_INT, 2*pointsX*indicesPerQuad*Integer.BYTES);
		if(realShift != pointsX-1)
		GL11.glDrawElements(GL11.GL_TRIANGLES, (pointsX - 1 - realShift)*indicesPerQuad, GL11.GL_UNSIGNED_INT, (realShift+1+2*pointsX)*indicesPerQuad*Integer.BYTES);
	}
	
	public void drawBackground(){
		int realShift = (startIndexLeft()+pointsX-1)%pointsX;
		
		//only draw the upper part (lower part is just to fit in the vbo)
		GL11.glDrawElements(GL11.GL_TRIANGLES, realShift*indicesPerQuad, GL11.GL_UNSIGNED_INT, pointsX*indicesPerQuad*Integer.BYTES);
		if(realShift != pointsX-1)
		GL11.glDrawElements(GL11.GL_TRIANGLES, (pointsX - 1 - realShift)*indicesPerQuad, GL11.GL_UNSIGNED_INT, (realShift+1+pointsX)*indicesPerQuad*Integer.BYTES);
	}
	
	ByteBuffer createVertexBufferColor(){
		ByteBuffer buffer = BufferUtils.createByteBuffer(2*columns.length*verticesPerPoint*bytesPerVertex);
		for(ColumnListElement column : columns){
			putPointDataDarkness(buffer, column.column());
		}
		for(ColumnListElement column : columns){
			putPointDataBackground(buffer, column.column());
		}
		buffer.flip();
		return buffer;
	}
	
	public void putPointDataDarkness(ByteBuffer buffer, Column c){
		buffer.putFloat((float)c.getX());
		buffer.putFloat((float)c.getTopSolidVertex().y());
		buffer.put(light);
		
		buffer.putFloat((float)c.getX());
		buffer.putFloat((float)(c.getTopSolidVertex().y() - darknessDistance));
		buffer.put(dark);
		
		buffer.putFloat((float)c.getX());
		buffer.putFloat((float)(c.getTopSolidVertex().y() - darknessDistance - 5000));
		buffer.put(dark);
	}
	
	public void putPointDataBackground(ByteBuffer buffer, Column c){
		buffer.putFloat((float)c.getX());
		buffer.putFloat((float)1);//always the top of the screen
		c.getTopColor().bytes(color);
		buffer.put(color);
		
		buffer.putFloat((float)c.getX());
		buffer.putFloat((float)-1);//always the bottom of the screen
		c.getLowColor().bytes(color);
		buffer.put(color);

		//just to fit in the vbo:
		buffer.putFloat((float)c.getX());
		buffer.putFloat((float)-1.1f);//always the bottom of the screen -1
		c.getLowColor().bytes(color);
		buffer.put(color);
	}

	@Override
	protected void addAt(ColumnListElement c, int index) {

		//Darkness and Background
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vaoColor.vbos[0].handle);
			//darkness
			putPointDataDarkness(changerColor, c.column());
			changerColor.flip();
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, index*verticesPerPoint*bytesPerVertex, changerColor);
			//background
			putPointDataBackground(changerColor, c.column());
			changerColor.flip();
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, (index + pointsX)*verticesPerPoint*bytesPerVertex, changerColor);
		
		//Unbind buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

}
