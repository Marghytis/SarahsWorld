package menu;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import core.Core;
import core.Window;
import render.Render;
import render.Shader;
import render.TexFile;
import render.Texture;
import render.VAO;
import util.Color;
import util.math.Vec;

public class Element {

	public double relX1, relY1, relX2, relY2;
	public int x1O, y1O, x2O, y2O;
	public int x1, y1, x2, y2, w, h;
	public Color color;
	public Texture tex;
	/**
	 * By default consists of an index buffer and a vertex buffer with coords and texCoords intertwined (as shorts).
	 */
	public VAO vao;
	
	public Element(double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color, Texture tex){
		this.relX1 = relX1;
		this.relY1 = relY1;
		this.relX2 = relX2;
		this.relY2 = relY2;
		this.x1O = x1;
		this.y1O = y1;
		this.x2O = x2;
		this.y2O = y2;
		this.color = color;
		this.tex = tex;
		setCoords();
		this.vao = Render.quadInScreen((short)(this.x1-Window.WIDTH_HALF), (short)(this.y1-Window.HEIGHT_HALF), (short)(this.x2-Window.WIDTH_HALF), (short)(this.y2-Window.HEIGHT_HALF));
	}
	
	public void setCoords(){
		this.x1 = (int)(Window.WIDTH*relX1) + x1O;
		this.y1 = (int)(Window.HEIGHT*relY1) + y1O;
		this.x2 = (int)(Window.WIDTH*relX2) + x2O;
		this.y2 = (int)(Window.HEIGHT*relY2) + y2O;
		this.w = x2 - x1;
		this.h = y2 - y1;
	}
	
	public void updateVertexBuffer(){
		ByteBuffer newCoords = BufferUtils.createByteBuffer(16*Float.BYTES);
		newCoords.putShort((short)(x1-Window.WIDTH_HALF));
		newCoords.putShort((short)(y1-Window.HEIGHT_HALF));
		newCoords.putShort((short)(0));
		newCoords.putShort((short)(0));
		newCoords.putShort((short)(x2-Window.WIDTH_HALF));
		newCoords.putShort((short)(y1-Window.HEIGHT_HALF));
		newCoords.putShort((short)(1));
		newCoords.putShort((short)(0));
		newCoords.putShort((short)(x2-Window.WIDTH_HALF));
		newCoords.putShort((short)(y2-Window.HEIGHT_HALF));
		newCoords.putShort((short)(1));
		newCoords.putShort((short)(1));
		newCoords.putShort((short)(x1-Window.WIDTH_HALF));
		newCoords.putShort((short)(y2-Window.HEIGHT_HALF));
		newCoords.putShort((short)(0));
		newCoords.putShort((short)(1));
		newCoords.flip();
		vao.vbos[0].update(0, newCoords);
	}
	
	public boolean contains(Vec vec){
		return vec.x >= x1 && vec.x <= x2 && vec.y >= y1 && vec.y <= y2;
	}
	
	public void update(double delta){}
	public void render(){
		Shader.singleQuad.bind();
			if(color != null) color.bind();
			else Color.WHITE.bind();
			if(tex != null){
				tex.file.bind();
				Shader.singleQuad.set("texCoords", tex.texCoords[0], tex.texCoords[1], tex.texCoords[2] - tex.texCoords[0], tex.texCoords[3] - tex.texCoords[1]);
			}
			Shader.singleQuad.set("texture", tex != null);
			Shader.singleQuad.set("scale", 1f/Window.WIDTH_HALF, 1f/Window.HEIGHT_HALF);
			Shader.singleQuad.set("offset", 0f, 0f);
			Shader.singleQuad.set("size", 1f);
			Shader.singleQuad.set("z", 0f);
			
			vao.bindStuff();
				GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0);
			vao.unbindStuff();
		
		TexFile.bindNone();
		Shader.bindNone();
		Core.checkGLErrors(true, true, "after rendering this menu element with texture " + (tex != null ? tex.file.path : "null") + " and color " + (color != null ? color.toString() : "null"));
	}
	
	public void pressed(int button, Vec mousePos){}
	
	public boolean released(int button, Vec mousePos, Vec pathSincePress){return false;}
	
	public boolean keyPressed(int key){return false;}
}
