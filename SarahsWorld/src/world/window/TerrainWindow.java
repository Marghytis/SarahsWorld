package world.window;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import exceptions.WorldTooSmallException;
import main.Res;
import menu.Settings;
import render.Render;
import render.Shader;
import render.TexFile;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import world.data.Column;
import world.data.Vertex;
import world.generation.Biome;
import world.generation.Material;

public class TerrainWindow extends ArrayWorldWindow {

	private static int 	indicesPerQuad = 6,
				verticesPerPoint = 3,
				bytesPerVertex = 2*Float.BYTES + 2*Float.BYTES + 4*Byte.BYTES + 1*Byte.BYTES;
	
	private VAO vao;//vaoColor contains the data for the darkness and the background, because they have the same format
	//I consider a Point to be an object of the Vertex class, because the name "vertex" is already in use...
	private final int pointsX,
				pointsY = Biome.layerCount - 1;
	private ByteBuffer changer = BufferUtils.createByteBuffer(bytesPerVertex*verticesPerPoint);
		
	//Variables that are local in nature, but are defined once to save time.
	int layersDrawn = 0;
	ArrayList<Patch> waterPatches = new ArrayList<>();
	Patch[] currentPatches;

	public TerrainWindow(Column anchor, int columnRadius) throws WorldTooSmallException {
		super(anchor, columnRadius);
		pointsX = columns.length;
		vao = new VAO(
				new VBO(createIndexBuffer(pointsY), GL15.GL_STATIC_DRAW),
				new VBO(createVertexBuffer(), GL15.GL_DYNAMIC_DRAW, bytesPerVertex,
						new VAP(2, GL11.GL_FLOAT, false, 0),//in_Position
						new VAP(2, GL11.GL_FLOAT, true, 2*Float.BYTES),//in_TextureCoords
						new VAP(Vertex.maxMatCount, GL11.GL_BYTE, true, 2*Float.BYTES + 2*Float.BYTES),//in_Alphas
						new VAP(1, GL11.GL_BYTE, true, 2*Float.BYTES + 2*Float.BYTES + Vertex.maxMatCount)
				));
		
		currentPatches = new Patch[Vertex.maxMatCount];
		for(int i = 0; i < currentPatches.length; i++) {
			currentPatches[i] = new Patch(0, 0, 0, null);
		}
	}
	
	public void reload() {
//		try {
//			loadAllColumns(ends[Dir.l], (int)Math.floor(Main.world.avatar.pos.x/Column.COLUMN_WIDTH));
//		} catch (WorldTooSmallException e) {
//			e.printStackTrace();
//			throw new RuntimeException("World is too small?!?!?!");
//		}
	}
	
	public void renderLandscape(){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		layersDrawn = 0;

		//LANDSCAPE
		Res.landscapeShader.bind();//Yes, don't use scaleX, scaleY here, because the landscape gets rendered into a framebuffer
		Res.landscapeShader.set("transform", Render.offsetX, Render.offsetY, Render.scaleX, Render.scaleY);
		vao.bindStuff();

			if(Settings.getInt("DRAW") == 0) {//(0 = GL_POINTS)
				GL32.glEnable(GL32.GL_PROGRAM_POINT_SIZE);
				Res.landscapeShader.set("points", true);
			} else {
				GL32.glDisable(GL32.GL_PROGRAM_POINT_SIZE);
				Res.landscapeShader.set("points", false);
			}
			//draw normal quads
			drawNormalQuads();
			//draw quads for vertcal transition
			if(Settings.getBoolean("DRAW_TRANSITIONS"))
				drawTransitionQuads();
		vao.unbindStuff();
		TexFile.bindNone();
		Shader.bindNone();
	}
	
	public void renderWater(){
		//WATER
		Res.landscapeShader.bind();
		Res.landscapeShader.set("transform", Render.offsetX, Render.offsetY, Render.scaleX, Render.scaleY);
		vao.bindStuff();
		
			if(Settings.getInt("DRAW") == 0) {//(0 = GL_POINTS)
				GL32.glEnable(GL32.GL_PROGRAM_POINT_SIZE);
				Res.landscapeShader.set("points", true);
			} else {
				GL32.glDisable(GL32.GL_PROGRAM_POINT_SIZE);
				Res.landscapeShader.set("points", false);
			}
		
			//draw normal quads
			drawWater();
		vao.unbindStuff();
		TexFile.bindNone();
		Shader.bindNone();
	}
	
	protected void addAt(Column c, int index) {
		//landscape
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vao.vbos[0].handle);
			for(int yIndex = 0; yIndex < pointsY; yIndex++){
				putPointData(changer, c, yIndex);
				changer.flip();
				GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, (yIndex*pointsX + index)*verticesPerPoint*bytesPerVertex, changer);
			}
		//Unbind buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void drawNormalQuads(){
		drawPatches2(0, false);
	}
	private void drawTransitionQuads(){
		drawPatches2(pointsX*pointsY*indicesPerQuad, false);
	}
	private void drawWater(){
		drawPatches(0, true);
		if(Settings.getBoolean("DRAW_TRANSITIONS"))
			drawPatches(pointsX*pointsY*indicesPerQuad, true);
	}
	
	private void drawPatches(int indicesOffset, boolean water){
		//for each material every patch of this material gets rendered.
		int oneBeforeStart = (startIndexLeft()+pointsX-1)%columns.length;
		for(int i = 0; i < Material.values().length-1; i++){
			if(i == Material.WATER.ordinal() && !water) continue;
			else if(i != Material.WATER.ordinal() && water) continue;
			Material.values()[i].tex.file.bind();
			for(int y = pointsY-1; y >= 0 && layersDrawn + pointsY-y < Settings.getInt("LAYERS_TO_DRAW"); y--){//draw from the bottom up
				int started = -1, index = 0, column = startIndexLeft();
				do {//while(column != indexShift), loop through all columns
					if(started == -1){
						for(int j = 0; j < Vertex.maxMatCount; j++){
							if(columns[column].vertices(y).mats()[j].ordinal() == i){//a new patch starts here
								started = column;
								index = j;
								break;//There might rise problems, if the same material appears twice in a single vertex...
							}
						}

						//the current patch ends here. if there is an open patch it gets rendered cut
					} else if(columns[column].vertices(y).mats()[index].ordinal() != i || column == oneBeforeStart){
						drawPatch(y, started, column, indicesOffset, index);
						started = -1;
					}
					column = (column+1)%columns.length;
				} while(column != startIndexLeft());
			}
		}
		layersDrawn += pointsY;
	}
	
	private void drawPatches2(int indicesOffset, boolean water){
		//for each material every patch of this material gets rendered.
		int oneBeforeStart = (startIndexLeft()+pointsX-1)%columns.length;
		for(Patch p : currentPatches)
			p.deactivate();
		for(int y = pointsY-1; y >= 0 && layersDrawn + pointsY-y < Settings.getInt("LAYERS_TO_DRAW"); y--){//draw from the bottom up
			drawPatchesLayer(indicesOffset, water, y, oneBeforeStart);
			for(Patch p : currentPatches)
				p.deactivate();
		}
		layersDrawn += pointsY;
	}
	
	private void drawPatchesLayer(int indicesOffset, boolean water, int iLayer, int oneBeforeStart) {
		int  column = startIndexLeft(), matIndex = columns[column].vertices(iLayer).firstMatIndex, endColumn = column;
		do {//while(column != indexShift), loop through all columns
			int matIndex2 = matIndex;
			do {
				if(currentPatches[matIndex2].active() && currentPatches[matIndex2].end == column){
					currentPatches[matIndex2].deactivate();
				}
				if((!currentPatches[matIndex2].active() || currentPatches[matIndex2].end == -1) && columns[column].vertices(iLayer).mats[matIndex2] != Material.AIR){
					currentPatches[matIndex2].set(iLayer, matIndex2, column, columns[column].vertices(iLayer).mats[matIndex2]);
				}
				if(currentPatches[matIndex2].active()){
					for(int column2 = column; currentPatches[matIndex2].end == -1; column2 = add1To(column2)){//if end != -1, the patch has been drawn
						//the current patch ends here. if there is an open patch it gets rendered cut (after one layer currentPatches[i] = null for all i)
						if(columns[column2].vertices(iLayer).mats[matIndex2] != currentPatches[matIndex2].mat || column2 == oneBeforeStart) {
							drawPatch(currentPatches[matIndex2], column2, indicesOffset, water);
							break;
						}
					}
				}
				matIndex2 = (matIndex2+1)%Vertex.maxMatCount;
			} while(matIndex2 != matIndex);//matIndex2 returns back to matIndex, if no patch is here
			
			column = add1To(column);
		} while(column != endColumn);
	}
	
	private static class Patch {
		public int start, end, yIndex, matIndex;
		public Material mat;
		boolean active = false;
		public Patch(int yIndex, int matIndex, int start, Material mat) {
			this.yIndex = yIndex;
			this.matIndex = matIndex;
			this.start = start;
			this.mat = mat;
			this.end = -1;
		}
		
		public void set(int yIndex, int matIndex, int start, Material mat) {
			this.yIndex = yIndex;
			this.matIndex = matIndex;
			this.start = start;
			this.mat = mat;
			this.end = -1;
			active = true;
		}
		
		public boolean active() {
			return active;
		}
		
		public void deactivate() {
			active = false;
		}
	}
	
	private void drawPatch(Patch patch, int end, int indicesOffset, boolean water){
		patch.end = end;
		if(patch.mat == Material.WATER){
			waterPatches.add(patch);
		} else {
			patch.mat.tex.file.bind();
			drawPatch(patch.yIndex, patch.start, patch.end, indicesOffset, patch.matIndex);
		}
	}
	
	private void drawPatch(int yIndex, int start, int end, int indicesOffset, int index){
		int pos1 = (yIndex*pointsX + start)*indicesPerQuad,
				size1 = (end - start)*indicesPerQuad,
				pos2 = 0, size2 = 0;
			if(end < start){
				size1 = (columns.length - start)*indicesPerQuad;//Yes, not -1, because linke this, the last and the first point will be connected.
				pos2 = (yIndex*pointsX + 0)*indicesPerQuad;
				size2 = (end - 0)*indicesPerQuad;
			}
			GL20.glUniform1i(Res.landscapeShader.uniformLoc("matSlot"), index);
			/*draw patch*/  GL11.glDrawElements(Settings.getInt("DRAW"), size1, GL11.GL_UNSIGNED_INT, (pos1 + indicesOffset)*Integer.BYTES);
			if(size2 != 0)	GL11.glDrawElements(Settings.getInt("DRAW"), size2, GL11.GL_UNSIGNED_INT, (pos2 + indicesOffset)*Integer.BYTES);
	}
	
	/**
	 * Puts vertices in the buffer like this for every row: (vertexColumns = 4)
	 * |0	|3	|6	|9
	 * |1	|4	|7	|10
	 * |2	|5	|8	|11
	 * @return data of vertexColumns*quadRows*verticesPerPoint vertices
	 */
	private ByteBuffer createVertexBuffer(){
		ByteBuffer buffer = BufferUtils.createByteBuffer(pointsX*pointsY*verticesPerPoint*bytesPerVertex);
		for(int yIndex = 0; yIndex < pointsY; yIndex++){// Put Vertices in Buffer:
			for(Column column : columns){
				putPointData(buffer, column, yIndex);
			}
		}
		buffer.flip();
		return buffer;
	}
	
	private void putPointData(ByteBuffer buffer, Column column, int yIndex){
		float 	x0 = (float)column.xReal,
				y0 = (float)column.vertices(yIndex).y,
				y1 = (float)column.vertices(yIndex+1).y,
				y2 = y1 - (float)column.vertices(yIndex).transitionHeight;
		if(!column.vertices(yIndex).prepared){
			column.vertices(yIndex).texCoordsPrepared[0] = x0/Material.CANDY.tex.w;//(short)(Short.MAX_VALUE*(... - Math.floor(x0/Material.CANDY.tex.w)))
			
			column.vertices(yIndex).texCoordsPrepared[1] = y0/Material.CANDY.tex.h;
			column.vertices(yIndex).texCoordsPrepared[2] = y1/Material.CANDY.tex.h;
			column.vertices(yIndex).texCoordsPrepared[3] = y2/Material.CANDY.tex.h;
			column.vertices(yIndex).prepared = true;
		}
		
		buffer.putFloat(x0);
		buffer.putFloat(y0);
		buffer.putFloat(column.vertices(yIndex).texCoordsPrepared[0]);
		buffer.putFloat(column.vertices(yIndex).texCoordsPrepared[1]);
		for(int j = 0; j < Vertex.maxMatCount; j++)
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices(yIndex).alphas[j]));
		buffer.put((byte)Byte.MAX_VALUE);
		
		buffer.putFloat(x0);
		buffer.putFloat(y1);
		buffer.putFloat(column.vertices(yIndex).texCoordsPrepared[0]);
		buffer.putFloat(column.vertices(yIndex).texCoordsPrepared[2]);
		for(int j = 0; j < Vertex.maxMatCount; j++)
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices(yIndex).alphas[j]));
		buffer.put((byte)Byte.MAX_VALUE);
		
		buffer.putFloat(x0);
		buffer.putFloat(y2);
		buffer.putFloat(column.vertices(yIndex).texCoordsPrepared[0]);
		buffer.putFloat(column.vertices(yIndex).texCoordsPrepared[3]);
		for(int j = 0; j < Vertex.maxMatCount; j++)
//			buffer.put((byte)0);
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices(yIndex).alphas[j]));
		buffer.put((byte)0);
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
	static IntBuffer createIndexBuffer(int pointsX, int pointsY){
		//type == 0 : normal quads with horizontalTransitions
		//type == 1 : vertical transition quads
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(pointsX*pointsY*indicesPerQuad*2);
		for(int type = 0, index0; type <= 1; type++){
			for(int y = 0; y < pointsY; y++){
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
	private IntBuffer createIndexBuffer(int layers){
		return createIndexBuffer(pointsX, layers);
	}
}
