package newStuff;

import java.awt.Font;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

import core.Renderer;
import core.Window;
import main.Main;
import render.Shader;
import render.TexFile;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import util.TrueTypeFont;
import world.Material;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.generation.Biome;

public class StaticInit implements Renderer {
	
	Chunk chunk;

	public Shader shader;
	public VAO vao;
	
	public float[] transform = {
			0.75f/Window.WIDTH_HALF,	0,						0,	0,
			0,						0.75f/Window.HEIGHT_HALF,	0,	0,
			0,						0,						1,	0,
			0,						0,						0,	1
		};
	FloatBuffer transformBuffer = BufferUtils.createFloatBuffer(transform.length);
	int 	vertexColumns = Chunk.size,
			quadColumns = vertexColumns - 1,
			quadRows = Biome.layerCount - 1,
			floatsPerVertex = 4 + Vertex.maxMatCount,
			verticesPerQuad = 3,
			indicesPerQuad = 6;
	public StaticInit(Chunk chunk){
		vertexColumns = Chunk.size;
		quadColumns = vertexColumns - 1;
		quadRows = Biome.layerCount-1;
		floatsPerVertex = 4 + Vertex.maxMatCount;
		verticesPerQuad = 3;
		indicesPerQuad = 6;
		
		this.chunk = chunk;
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(vertexColumns*quadRows*verticesPerQuad*floatsPerVertex);
		for(int i = 0; i < quadRows; i++){
			for(Column column : chunk.columns){
				float 	x0 = (float)column.xReal,
						y0 = (float)column.vertices[i].y,
						y1 = (float)column.vertices[i+1].y,
						y2 = y1 - (float)column.vertices[i].transitionHeight;
				
				buffer.put(x0);
				buffer.put(y0);
				buffer.put(x0/Material.CANDY.tex.w);
				buffer.put(y0/Material.CANDY.tex.h);
				for(int j = 0; j < Vertex.maxMatCount; j++){
					buffer.put((float)column.vertices[i].alphas[j]);
				}
				buffer.put(x0);
				buffer.put(y1);
				buffer.put(x0/Material.CANDY.tex.w);
				buffer.put(y1/Material.CANDY.tex.h);
				for(int j = 0; j < Vertex.maxMatCount; j++){
					buffer.put((float)column.vertices[i].alphas[j]);
				}
				buffer.put(x0);
				buffer.put(y2);
				buffer.put(x0/Material.CANDY.tex.w);
				buffer.put(y2/Material.CANDY.tex.h);
				for(int j = 0; j < Vertex.maxMatCount; j++){
					buffer.put(0);
				}
			}
		}
		buffer.flip();

		IntBuffer indexBuffer = BufferUtils.createIntBuffer(quadColumns*quadRows*indicesPerQuad*2);
		//normal quads with horizontalTransitions
		for(int y = 0; y < quadRows; y++){
			for(int i = 0; i < quadColumns; i++){
				int index0 = getVertexIndex(i, y);
				indexBuffer.put(index0);
				indexBuffer.put(index0 + 1);
				indexBuffer.put(index0 + 3);

				indexBuffer.put(index0 + 1);
				indexBuffer.put(index0 + 4);
				indexBuffer.put(index0 + 3);
			}
		}
		//vertical transition quads
		for(int y = 0; y < quadRows; y++){
			for(int i = 0; i < quadColumns; i++){
				int index0 = getVertexIndex(i, y);
				
				indexBuffer.put(index0 + 1);
				indexBuffer.put(index0 + 2);
				indexBuffer.put(index0 + 4);
				
				indexBuffer.put(index0 + 2);
				indexBuffer.put(index0 + 5);
				indexBuffer.put(index0 + 4);
			}
		}
		indexBuffer.flip();
		
		vao = new VAO(
				new VBO(indexBuffer, GL15.GL_STATIC_READ, 0),
				new VBO(buffer, GL15.GL_STATIC_READ, floatsPerVertex*Float.BYTES,
						new VAP(2, GL11.GL_FLOAT, false, 0),
						new VAP(2, GL11.GL_FLOAT, false, 2*Float.BYTES),
						new VAP(Vertex.maxMatCount, GL11.GL_FLOAT, false, 4*Float.BYTES)
				));
		
		shader = Shader.create("res/shader/material.vert", "res/shader/material.frag", "in_Position", "in_TextureCoords", "in_Alphas");
	}
	
	public int getVertexIndex(int column, int y){
		return verticesPerQuad*(y*vertexColumns + column);
	}
	public int getIndexIndex(int column, int y){
		return indicesPerQuad*(y*quadColumns + column);
	}
	
	public void draw() {
		transform[3] = -0.75f*(float)Main.world.avatar.pos.x/Window.WIDTH_HALF;
//		System.out.println("Avatar position: " + Main.world.avatar.pos.x);
		transformBuffer.put(transform);
		transformBuffer.flip();
		shader.bind();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		vao.bindStuff();
		GL20.glUniformMatrix4(shader.uniformLoc("transform"), true, transformBuffer);
	    
		//draw normal quads
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
		drawPatches(0);
		//draw quads for vertcal transition
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		drawPatches(quadColumns*quadRows*indicesPerQuad);

		vao.unbindStuff();
		TexFile.bindNone();
		Shader.bindNone();
		
	}
	
	
	
	public void drawPatches(int indicesOffset){
		for(int y = quadRows-1; y >= 0; y--){
			for(int i = 0; i < Material.values().length-1; i++){
			Material.values()[i].tex.file.bind();
			
				int started = -1, index = 0;
				for(Column cursor : chunk.columns){
					if(started == -1){
						for(int j = 0; j < Vertex.maxMatCount; j++){
							if(cursor.vertices[y].mats()[j].ordinal() == i && cursor.vertices[y].alphas[j] > 0){
								started = cursor.xIndex;
								index = j;
								break;//There might rise problems, if the same material appears twice in a single vertex...
							}
						}
					} else if(cursor.vertices[y].mats()[index].ordinal() != i || cursor.vertices[y].alphas[index] <= 0){
						GL20.glUniform1i(shader.uniformLoc("matSlot"), index);
						int size = (cursor.xIndex - started)*indicesPerQuad;
						int pos = getIndexIndex(started - (chunk.xIndex*(Chunk.size-1)), y) + indicesOffset;
						GL11.glDrawElements(GL11.GL_TRIANGLES, size, GL11.GL_UNSIGNED_INT, pos*Integer.BYTES);
						started = -1;
					}
				}
				if(started != -1){
					int size = (((chunk.xIndex+1)*(Chunk.size-1)) - started)*indicesPerQuad;
					int pos = getIndexIndex(started - (chunk.xIndex*(Chunk.size-1)), y) + indicesOffset;
					GL20.glUniform1i(shader.uniformLoc("matSlot"), index);
					GL11.glDrawElements(GL11.GL_TRIANGLES, size, GL11.GL_UNSIGNED_INT, pos*Integer.BYTES);
				}
			}
		}
	}
	
	public static int errorValue;
	public static void checkGLErrors(boolean exit){
		boolean foundSomething = false;
		 while ((errorValue = GL11.glGetError()) != GL11.GL_NO_ERROR) {
	            String errorString = GLU.gluErrorString(errorValue);
	            System.err.println("ERROR - " + "BLA" + ": " + errorString);
	            foundSomething = true;
        }
		if(foundSomething && exit){
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}
	
}
