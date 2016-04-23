package world;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import core.Core;
import main.Res;
import menu.Settings;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import things.Thing;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.generation.Biome;

public class LandscapeWindow {
	
	public float darknessDistance = 1000;

	public Column[] columns;
	public int indexShift;
	public int columnRadius, center, index;
	public Column left, right;
	
	public VAO vao, vaoDarkness;
	//I consider a Point to be an object of the Vertex class, because the name "vertex" is already in use...
	int 	pointsX,
			pointsY = Biome.layerCount - 1,
			bytesPerVertex = 2*Float.BYTES + 2*Float.BYTES + 4*Byte.BYTES + 1*Byte.BYTES,
			bytesPerVertexDarkness = 2*Float.BYTES + 4*Byte.BYTES,
			verticesPerPoint = 3,
			indicesPerQuad = 6;
		ByteBuffer changer = BufferUtils.createByteBuffer(bytesPerVertex*verticesPerPoint);
		ByteBuffer changerDarkness = BufferUtils.createByteBuffer(bytesPerVertexDarkness*verticesPerPoint);
		
	int maxTextureUnits;

	public LandscapeWindow(WorldData data, Column anchor, int columnRadius, int firstIndex){
		this.columnRadius = columnRadius;
		columns = new Column[2*columnRadius+1];
		this.center = firstIndex;
		pointsX = columns.length;
		left = anchor;
		right = left;
		//create Column array and find borders
		insertColumn( left);
		while(left.xIndex > firstIndex-columnRadius && left.left != null){
			left = left.left;
			insertColumn( left);
		}
		while(right.xIndex < firstIndex+columnRadius && right.right != null){
			right = right.right;
			insertColumn( right);
		}
		if(left.xIndex > firstIndex-columnRadius || right.xIndex < firstIndex + columnRadius){
			new Exception("World data is not large enough yet").printStackTrace();
			Display.destroy();
			System.exit(-1);
		}
		vao = new VAO(
				new VBO(createIndexBuffer(pointsY), GL15.GL_STATIC_DRAW),
				new VBO(createVertexBuffer(), GL15.GL_DYNAMIC_DRAW, bytesPerVertex,
						new VAP(2, GL11.GL_FLOAT, false, 0),//in_Position
						new VAP(2, GL11.GL_FLOAT, true, 2*Float.BYTES),//in_TextureCoords
						new VAP(Vertex.maxMatCount, GL11.GL_BYTE, true, 2*Float.BYTES + 2*Float.BYTES),//in_Alphas
						new VAP(1, GL11.GL_BYTE, true, 2*Float.BYTES + 2*Float.BYTES + Vertex.maxMatCount)
				));
		vaoDarkness = new VAO(
				new VBO(createIndexBuffer(1), GL15.GL_STATIC_DRAW),
				new VBO(createVertexBufferDarkness(), GL15.GL_DYNAMIC_DRAW, bytesPerVertexDarkness,
						new VAP(2, GL11.GL_FLOAT, false, 0),//in_Position
						new VAP(4, GL11.GL_BYTE, true, 2*Float.BYTES)));//in_Color
		
		maxTextureUnits = GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS);
	}
	void insertColumn(Column c){
		int index = c.xIndex - (center - columnRadius); 
		columns[index] = c;
		if(index > 0 && columns[index-1] != null){
			c.left = columns[index-1];
			columns[index-1].right = c;
		}
		if(index < columns.length-1 && columns[index+1] != null){
			c.right = columns[index+1];
			columns[index+1].left = c;
		}
	}
	
	public void moveTo(int xIndex){
		while(center < xIndex && right.right != null){
			
			for(int i = 0; i < left.things.length; i++){
				for(Thing t = left.things[i]; t != null; t = t.next){
					t.setVisible(false);
				}
			}
			right = right.right;
			left = left.right;
			addRight(right);
			center++;
			
			for(int i = 0; i < right.things.length; i++){
				for(Thing t = right.things[i]; t != null; t = t.next){
					t.setVisible(true);
				}
			}
		}
		while(center > xIndex && left.left != null){
			
			for(int i = 0; i < right.things.length; i++){
				for(Thing t = right.things[i]; t != null; t = t.next){
					t.setVisible(false);
				}
			}
			left = left.left;
			right = right.left;
			addLeft(left);
			center--;

			for(int i = 0; i < left.things.length; i++){
				for(Thing t = left.things[i]; t != null; t = t.next){
					t.setVisible(true);
				}
			}
		}
	}

	public void addRight(Column c){
		columns[indexShift] = c;
		addToVBOAtIndexShift(c);
		indexShift = (indexShift+1)%columns.length;
	}
	
	public void addLeft(Column c){
		indexShift = (indexShift+columns.length-1)%columns.length;//this has to run prior to the other two statements
		columns[indexShift] = c;
		addToVBOAtIndexShift(c);
	}
	
	public void addToVBOAtIndexShift(Column c){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vao.vbos[0].handle);
			for(int yIndex = 0; yIndex < pointsY; yIndex++){
				putPointData(changer, c, yIndex);
				changer.flip();
				GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, (yIndex*pointsX + indexShift)*verticesPerPoint*bytesPerVertex, changer);
			}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vaoDarkness.vbos[0].handle);
			putPointDataDarkness(changerDarkness, c);
			changerDarkness.flip();
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, indexShift*verticesPerPoint*bytesPerVertexDarkness, changerDarkness);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void drawNormalQuads(){
		drawPatches2(0, false);
	}
	public void drawTransitionQuads(){
		drawPatches2(pointsX*pointsY*indicesPerQuad, false);
	}
	public void drawWater(){
		drawPatches(0, true);
		drawPatches(pointsX*pointsY*indicesPerQuad, true);
	}
	
	public static int layersToDraw = (Biome.layerCount-1)*6;
	public static int layersDrawn = 0;
	
	void drawPatches(int indicesOffset, boolean water){
		//for each material every patch of this material gets rendered.
		int oneBeforeStart = (indexShift+columns.length-1)%columns.length;
		for(int i = 0; i < Material.values().length-1; i++){
			if(i == Material.WATER.ordinal() && !water) continue;
			else if(i != Material.WATER.ordinal() && water) continue;
			Material.values()[i].tex.file.bind();
			for(int y = pointsY-1; y >= 0 && layersDrawn + pointsY-y < layersToDraw; y--){//draw from the bottom up
				int started = -1, index = 0, column = indexShift;
				do {//while(column != indexShift), loop through all columns
					if(started == -1){
						for(int j = 0; j < Vertex.maxMatCount; j++){
							if(columns[column].vertices[y].mats()[j].ordinal() == i){//a new patch starts here
								started = column;
								index = j;
								break;//There might rise problems, if the same material appears twice in a single vertex...
							}
						}

						//the current patch ends here. if there is an open patch it gets rendered cut
					} else if(columns[column].vertices[y].mats()[index].ordinal() != i || column == oneBeforeStart){
						drawPatch(y, started, column, indicesOffset, index);
						started = -1;
					}
					column = (column+1)%columns.length;
				} while(column != indexShift);
			}
		}
		layersDrawn += pointsY;
	}
	
	void drawPatches2(int indicesOffset, boolean water){
		//for each material every patch of this material gets rendered.
		int oneBeforeStart = (indexShift+columns.length-1)%columns.length;
		Patch[] currentPatches = new Patch[Vertex.maxMatCount];
		for(int y = pointsY-1; y >= 0 && layersDrawn + pointsY-y < layersToDraw; y--){//draw from the bottom up
			int  column = indexShift, matIndex = columns[column].vertices[y].firstMatIndex;
			do {//while(column != indexShift), loop through all columns
				for(int i = 0; i < Vertex.maxMatCount; i++){
					if(currentPatches[i] != null && currentPatches[i].end == column){
						currentPatches[i] = null;
					}
					if((currentPatches[i] == null || currentPatches[i].end == -1) && columns[column].vertices[y].mats[i] != Material.AIR){
						currentPatches[i] = new Patch(y, i, column, columns[column].vertices[y].mats[i]);
					}
				}
				int matIndex2 = matIndex;
				do {
					if(currentPatches[matIndex2] != null){
						for(int column2 = column; currentPatches[matIndex2].end == -1; column2 = (column2+1)%pointsX){//if end != 1, the patch has been drawn
							//the current patch ends here. if there is an open patch it gets rendered cut (after one layer currentPatches[i] = null for all i)
							if(columns[column2].vertices[y].mats[matIndex2] != currentPatches[matIndex2].mat || column2 == oneBeforeStart)
								drawPatch(currentPatches[matIndex2], column2, indicesOffset, water);
						}
					}
					matIndex2 = (matIndex2+1)%Vertex.maxMatCount;
				} while(matIndex2 != matIndex);//matIndex2 returns back to matIndex, if no patch is here
				matIndex = matIndex2;
				column = (column+1)%columns.length;
			} while(column != indexShift);
			Arrays.fill(currentPatches, null);
		}
		layersDrawn += pointsY;
	}
	public class Patch{
		public int start, end = -1, yIndex, matIndex;
		public Material mat;
		public Patch(int yIndex, int matIndex, int start, Material mat) {
			this.yIndex = yIndex;
			this.matIndex = matIndex;
			this.start = start;
			this.mat = mat;
		}
	}
	ArrayList<Patch> waterPatches = new ArrayList<>();
	
	public void drawPatch(Patch patch, int end, int indicesOffset, boolean water){
		patch.end = end;
		if(patch.mat == Material.WATER){
			waterPatches.add(patch);
		} else {
			patch.mat.tex.file.bind();
			drawPatch(patch.yIndex, patch.start, patch.end, indicesOffset, patch.matIndex);
		}
	}
	
	public void drawWater(int indicesOffset){
		Material.WATER.tex.file.bind();
		for(Patch patch : waterPatches){
			drawPatch(patch.yIndex, patch.start, patch.end, indicesOffset, patch.matIndex);
		}
		waterPatches.clear();
	}
	
	public void drawPatch(int yIndex, int start, int end, int indicesOffset, int index){
		int pos1 = (yIndex*pointsX + start)*indicesPerQuad,
				size1 = (end - start)*indicesPerQuad,
				pos2 = 0, size2 = 0;
			if(end < start){
				size1 = (columns.length - start)*indicesPerQuad;//Yes, not -1, because linke this, the last and the first point will be connected.
				pos2 = (yIndex*pointsX + 0)*indicesPerQuad;
				size2 = (end - 0)*indicesPerQuad;
			}
			GL20.glUniform1i(Res.landscapeShader.uniformLoc("matSlot"), index);
			/*draw patch*/  GL11.glDrawElements(Settings.DRAW, size1, GL11.GL_UNSIGNED_INT, (pos1 + indicesOffset)*Integer.BYTES);
			if(size2 != 0)	GL11.glDrawElements(Settings.DRAW, size2, GL11.GL_UNSIGNED_INT, (pos2 + indicesOffset)*Integer.BYTES);
	}
	public void drawDarkness(){
		int realShift = (indexShift+pointsX-1)%pointsX;
		//Transition part
		GL11.glDrawElements(Settings.DRAW, realShift*indicesPerQuad, GL11.GL_UNSIGNED_INT, 0*indicesPerQuad*Integer.BYTES);
		if(realShift != pointsX-1)
		GL11.glDrawElements(Settings.DRAW, (pointsX - 1 - realShift)*indicesPerQuad, GL11.GL_UNSIGNED_INT, (realShift+1)*indicesPerQuad*Integer.BYTES);
		//complete dark part
		GL11.glDrawElements(Settings.DRAW, realShift*indicesPerQuad, GL11.GL_UNSIGNED_INT, pointsX*indicesPerQuad*Integer.BYTES);
		if(realShift != pointsX-1)
		GL11.glDrawElements(Settings.DRAW, (pointsX - 1 - realShift)*indicesPerQuad, GL11.GL_UNSIGNED_INT, (realShift+1+pointsX)*indicesPerQuad*Integer.BYTES);
	}
	/**
	 * Puts vertices in the buffer like this for every row: (vertexColumns = 4)
	 * |0	|3	|6	|9
	 * |1	|4	|7	|10
	 * |2	|5	|8	|11
	 * @return data of vertexColumns*quadRows*verticesPerPoint vertices
	 */
	ByteBuffer createVertexBuffer(){
		ByteBuffer buffer = BufferUtils.createByteBuffer(pointsX*pointsY*verticesPerPoint*bytesPerVertex);
		for(int yIndex = 0; yIndex < pointsY; yIndex++){// Put Vertices in Buffer:
			for(Column column : columns){
				putPointData(buffer, column, yIndex);
			}
		}
		buffer.flip();
		return buffer;
	}
	ByteBuffer createVertexBufferDarkness(){
		ByteBuffer buffer = BufferUtils.createByteBuffer(pointsX*verticesPerPoint*bytesPerVertexDarkness);
		for(Column column : columns){
			putPointDataDarkness(buffer, column);
		}
		buffer.flip();
		return buffer;
	}
	public void putPointData(ByteBuffer buffer, Column column, int yIndex){
		float 	x0 = (float)column.xReal,
				y0 = (float)column.vertices[yIndex].y,
				y1 = (float)column.vertices[yIndex+1].y,
				y2 = y1 - (float)column.vertices[yIndex].transitionHeight;
		if(!column.vertices[yIndex].prepared){
			column.vertices[yIndex].texCoordsPrepared[0] = x0/Material.CANDY.tex.w;//(short)(Short.MAX_VALUE*(... - Math.floor(x0/Material.CANDY.tex.w)))
			
			column.vertices[yIndex].texCoordsPrepared[1] = y0/Material.CANDY.tex.h;
			column.vertices[yIndex].texCoordsPrepared[2] = y1/Material.CANDY.tex.h;
			column.vertices[yIndex].texCoordsPrepared[3] = y2/Material.CANDY.tex.h;
			column.vertices[yIndex].prepared = true;
		}
		
		buffer.putFloat(x0);
		buffer.putFloat(y0);
		buffer.putFloat(column.vertices[yIndex].texCoordsPrepared[0]);
		buffer.putFloat(column.vertices[yIndex].texCoordsPrepared[1]);
		for(int j = 0; j < Vertex.maxMatCount; j++)
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices[yIndex].alphas[j]));
		buffer.put((byte)Byte.MAX_VALUE);
		
		buffer.putFloat(x0);
		buffer.putFloat(y1);
		buffer.putFloat(column.vertices[yIndex].texCoordsPrepared[0]);
		buffer.putFloat(column.vertices[yIndex].texCoordsPrepared[2]);
		for(int j = 0; j < Vertex.maxMatCount; j++)
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices[yIndex].alphas[j]));
		buffer.put((byte)Byte.MAX_VALUE);
		
		buffer.putFloat(x0);
		buffer.putFloat(y2);
		buffer.putFloat(column.vertices[yIndex].texCoordsPrepared[0]);
		buffer.putFloat(column.vertices[yIndex].texCoordsPrepared[3]);
		for(int j = 0; j < Vertex.maxMatCount; j++)
//			buffer.put((byte)0);
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices[yIndex].alphas[j]));
		buffer.put((byte)0);
	}
	byte[] light = {0, 0, 0, 0}, dark = {0, 0, 0, 127};
	public void putPointDataDarkness(ByteBuffer buffer, Column c){
		buffer.putFloat((float)c.xReal);
		buffer.putFloat((float)c.vertices[c.collisionVec].y);
		buffer.put(light);
		
		buffer.putFloat((float)c.xReal);
		buffer.putFloat((float)(c.vertices[c.collisionVec].y - darknessDistance));
		buffer.put(dark);
		
		buffer.putFloat((float)c.xReal);
		buffer.putFloat(-1000);
		buffer.put(dark);
	}
	/**
	 * Puts indices in the buffer like this for every row: (vertexColumns = 4)
	 * 		V0         V3		  V6		 V9	 (Main quads)
	 * 20,23|00   02,05|06	 08,11|12   14,17|18
	 *    22|01,03   04|07,09   10|13,15   16|19,21
	 *      |          |          |          |
	 *      
	 *      V0         V3		  V6		 V9	 (Transition)
	 *      |  		   |          |          |
	 * 20,23|00   02,05|06	 08,11|12   14,17|18
	 *    22|01,03   04|07,09   10|13,15   16|19,21
	 * @return creates vertexColumns*quadRows*indicesPerQuad*2 indices
	 */
	IntBuffer createIndexBuffer(int layers){
		//type == 0 : normal quads with horizontalTransitions
		//type == 1 : vertical transition quads
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(pointsX*layers*indicesPerQuad*2);
		for(int type = 0, index0; type <= 1; type++){
			for(int y = 0; y < layers; y++){
				for(int x = 0; x < pointsX-1; x++){
					index0 = (y*pointsX + x)*verticesPerPoint + type;
					
					indexBuffer.put(index0);
					indexBuffer.put(index0 + 1);
					indexBuffer.put(index0 + 3);
	
					indexBuffer.put(index0 + 1);
					indexBuffer.put(index0 + 4);
					indexBuffer.put(index0 + 3);
				}
				index0 = (y*pointsX + pointsX-1)*verticesPerPoint + type;
				int index00 = (y*pointsX + 0)*verticesPerPoint + type;
				indexBuffer.put(new int[]{
						index0,		index0+1,	index00,
						index0+1,	index00+1,	index00});
			}
		}
		indexBuffer.flip();
		return indexBuffer;
	}
}
