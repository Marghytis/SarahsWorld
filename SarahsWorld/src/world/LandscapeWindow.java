package world;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

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
	
	public float darknessDistance = 600;

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
		drawPatches(0, false);//premultiplied
	}
	public void drawTransitionQuads(){
		drawPatches(pointsX*pointsY*indicesPerQuad, true);
	}
	
	void drawPatches(int indicesOffset, boolean alpha){
		Res.landscapeShader.set("transition", alpha);
		//for each material every patch of this material gets rendered.
		int oneBeforeStart = (indexShift+columns.length-1)%columns.length;
		for(int i = 0; i < Material.values().length-1; i++){
			Material.values()[i].tex.file.bind();
			for(int y = pointsY-1; y >= 0; y--){
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
						int pos1 = (y*pointsX + started)*indicesPerQuad,
							size1 = (column - started)*indicesPerQuad,
							pos2 = 0, size2 = 0;
						if(column < started){
							size1 = (columns.length - started)*indicesPerQuad;//Yes, not -1, because linke this, the last and the first point will be connected.
							pos2 = (y*pointsX + 0)*indicesPerQuad;
							size2 = (column - 0)*indicesPerQuad;
						}
						GL20.glUniform1i(Res.landscapeShader.uniformLoc("matSlot"), index);
						/*draw patch*/  GL11.glDrawElements(Settings.DRAW, size1, GL11.GL_UNSIGNED_INT, (pos1 + indicesOffset)*Integer.BYTES);
						if(size2 != 0)	GL11.glDrawElements(Settings.DRAW, size2, GL11.GL_UNSIGNED_INT, (pos2 + indicesOffset)*Integer.BYTES);
						started = -1;
					}
					column = (column+1)%columns.length;
				} while(column != indexShift);
			}
		}
	}
	public void drawDarkness(){
//		GL11.glDrawElements(GL11.GL_TRIANGLES, (pointsX-indexShift)*indicesPerQuad, GL11.GL_UNSIGNED_INT, indexShift*indicesPerQuad*Integer.BYTES);
//		if(indexShift != 0)
//			GL11.glDrawElements(GL11.GL_TRIANGLES, (indexShift-1)*indicesPerQuad,		GL11.GL_UNSIGNED_INT, 0*Integer.BYTES);
//		
//		GL11.glDrawElements(GL11.GL_TRIANGLES, (pointsX-indexShift)*indicesPerQuad, GL11.GL_UNSIGNED_INT, (indexShift + pointsX)*indicesPerQuad*Integer.BYTES);
//		if(indexShift != 0)
//			GL11.glDrawElements(GL11.GL_TRIANGLES, (indexShift-1)*indicesPerQuad,		GL11.GL_UNSIGNED_INT, pointsX*indicesPerQuad*Integer.BYTES);
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
	byte[] light = {127, 127, 127, 127}, dark = {0, 0, 0, 127};
	public void putPointDataDarkness(ByteBuffer buffer, Column c){
		buffer.putFloat((float)c.xReal);
		buffer.putFloat((float)c.vertices[c.collisionVec].y);
		buffer.put(light);
		
		buffer.putFloat((float)c.xReal);
		buffer.putFloat((float)(c.vertices[c.collisionVec].y - darknessDistance));
		buffer.put(dark);
		
		buffer.putFloat((float)c.xReal);
		buffer.putFloat(-Float.MAX_VALUE+1);
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
