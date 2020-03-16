package world.window;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import basis.exceptions.WorldTooSmallException;
import extra.Res;
import menu.Settings;
import render.Render;
import render.Shader;
import render.TexFile;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import world.data.Column;
import world.data.ColumnListElement;
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
	List<Patch> existingPatches = new ArrayList<>();
	
	boolean patchesNeedUpdate = true;

	public TerrainWindow(ColumnListElement anchor, int columnRadius) throws WorldTooSmallException {
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
	}
	
	public void reload() {
//		try {
//			loadAllColumns(ends[Dir.l], (int)Math.floor(Main.game().world.avatar.pos.x/Column.COLUMN_WIDTH));
//		} catch (WorldTooSmallException e) {
//			e.printStackTrace();
//			throw new RuntimeException("World is too small?!?!?!");
//		}
	}
	
	public void renderLandscape(){
		if(patchesNeedUpdate)
			updatePatches();
		
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
		if(patchesNeedUpdate)
			updatePatches();
		
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
	
	private void drawNormalQuads(){
		drawPatches3(0, existingPatches);
	}
	private void drawTransitionQuads(){
		drawPatches3(pointsX*pointsY*indicesPerQuad, existingPatches);
	}
	private void drawWater(){
		drawPatches3(0, waterPatches);
		if(Settings.getBoolean("DRAW_TRANSITIONS"))
			drawPatches3(pointsX*pointsY*indicesPerQuad, waterPatches);
//		drawWaterPatches(0);
//		if(Settings.getBoolean("DRAW_TRANSITIONS"))
//			drawWaterPatches(pointsX*pointsY*indicesPerQuad);
	}
	
	private void drawPatches3(int indicesOffset, List<Patch> patches) {
		for(Patch patch : patches)
			drawPatch(patch, indicesOffset);
	}
	
	/**
	 * For each layer all patches are rendered from left to right, independent of their materials
	 */
	private void updatePatches(){
		waterPatches.clear();
		existingPatches.clear();
		//for each material every patch of this material gets rendered.
		int oneBeforeStart = substract1From(startIndexLeft());
		int[] patchEnds = new int[Vertex.maxMatCount]; 
		for(int i = 0; i < patchEnds.length; i++) patchEnds[i] = -1;
		//loop all layers
		for(int y = pointsY-1; y >= 0 && layersDrawn + pointsY-y < Settings.getInt("LAYERS_TO_DRAW"); y--){//draw from the bottom up
			//loop all columns in this layer
			int  column = startIndexLeft();
			int endColumn = column;
			do {
				//loop all material slots
				int matIndex = columns[column].column().vertices(y).getFirstMatIndex();
				int matIndex2 = 0;
				do {
					//if there is currently no patch in this material slot or the last one has just finished, find the next patch
					if(patchEnds[matIndex] == -1 || patchEnds[matIndex] == column)
						patchEnds[matIndex] = findAndAddSinglePatch(column, y, matIndex2, oneBeforeStart);
					
					
					matIndex2 = (matIndex2+1)%Vertex.maxMatCount;
				} while(matIndex2 != matIndex);//matIndex2 returns back to matIndex, if no patch is here
				//TODO: take care of the case where the same material appears twice in a single vertex...
				
				column = add1To(column);
			} while(column != endColumn);
			for(int i = 0; i < patchEnds.length; i++) patchEnds[i] = -1;
		}
		layersDrawn += pointsY;
		
		patchesNeedUpdate = false;
	}
	
	private int findAndAddSinglePatch(int iStartColumn, int iLayer, int iMat, int oneBeforeStart) {
		
		if(columns[iStartColumn].column().vertices(iLayer).mats(iMat) == Material.AIR){
			return -1;
			//start a new patch, if there is no AIR at this vertex
		} else {
			
			//search for patch end
			int end = iStartColumn;
			//count up end while the layer still contains this material and the landscape window didn't end.
			while(columns[end].column().vertices(iLayer).mats(iMat) == columns[iStartColumn].column().vertices(iLayer).mats(iMat) && end != oneBeforeStart) {
				end = add1To(end);
			}
			
			//create patch
			Patch patch = new Patch(iLayer, iMat, iStartColumn, end, columns[iStartColumn].column().vertices(iLayer).mats(iMat));
			
			//add patch to it's respective patch list
			if(patch.mat == Material.WATER)
				waterPatches.add(patch);
			else
				existingPatches.add(patch);
			
			//return the end index of the newly created patch
			return end;
		}
	}
	
	/**
	 * A Patch is a connected area that is to be filled with a single material (maybe varying opacity) and is contained in one layer
	 * @author Mario
	 *
	 */
	private static class Patch {
		/** yIndex of the layer this Patch belongs to */
		public int yIndex;
		/** Indices of the first and last column that make up this Patch (indices in the column array of this window)*/
		public int start, end;
		/** Index of this Patches material in the vertices material arrays*/
		public int matIndex;
		/** The corresponding material of this Patch*/
		public Material mat;
		
		public Patch(int iLayer, int iMat, int iStartColumn, int iEndColumn, Material mat) {
			this.yIndex = iLayer;
			this.matIndex = iMat;
			this.start = iStartColumn;
			this.mat = mat;
			this.end = iEndColumn;
		}
	}
	
	private void drawPatch(Patch patch, int indicesOffset){
		patch.mat.tex.file.bind();
		drawPatch(patch.yIndex, patch.start, patch.end, indicesOffset, patch.matIndex);
	}
	
	private void drawPatch(int yIndex, int start, int end, int indicesOffset, int index){
		//Position and size of the patch in the vertex buffer.
		//1 is the part up to the array end (may end before the array end, then no. 2 is ignored
		//2 is the part which is folded around to the beginning of the column array, if the Patch crosses the end
		int pos1 = (yIndex*pointsX + start)*indicesPerQuad,
				size1 = (end - start)*indicesPerQuad;
		int pos2 = 0, size2 = 0;
		
		//if the Patch is split by the array end, cut it into two parts
		if(end < start){
			size1 = (columns.length - start)*indicesPerQuad;//Yes, not -1, because like this, the last and the first point will be connected.
			pos2 = (yIndex*pointsX + 0)*indicesPerQuad;
			size2 = (end - 0)*indicesPerQuad;
		}
		
		//select the material in the shader
		GL20.glUniform1i(Res.landscapeShader.uniformLoc("matSlot"), index);
		
		/*draw patch*/  GL11.glDrawElements(Settings.getInt("DRAW"), size1, GL11.GL_UNSIGNED_INT, (pos1 + indicesOffset)*Integer.BYTES);
		if(size2 != 0)	GL11.glDrawElements(Settings.getInt("DRAW"), size2, GL11.GL_UNSIGNED_INT, (pos2 + indicesOffset)*Integer.BYTES);
	}
	
	protected void addAt(ColumnListElement c, int index) {
		//landscape
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vao.vbos[0].handle);
			for(int yIndex = 0; yIndex < pointsY; yIndex++){
				putPointData(changer, c.column(), yIndex);
				changer.flip();
				GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, (yIndex*pointsX + index)*verticesPerPoint*bytesPerVertex, changer);
			}
		//Unbind buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
			for(ColumnListElement column : columns){
				putPointData(buffer, column.column(), yIndex);
			}
		}
		buffer.flip();
		return buffer;
	}
	
	private void putPointData(ByteBuffer buffer, Column column, int yIndex){
		float 	x0 = (float)column.getX(),
				y0 = (float)column.vertices(yIndex).y(),
				y1 = (float)column.vertices(yIndex+1).y(),
				y2 = y1 - (float)column.vertices(yIndex).getTransitionHeight();
		if(!column.vertices(yIndex).isPrepared()){
			column.vertices(yIndex).prepare(
					x0/Material.CANDY.tex.w,
					y0/Material.CANDY.tex.h,
					y1/Material.CANDY.tex.h,
					y2/Material.CANDY.tex.h);
		}
		
		buffer.putFloat(x0);
		buffer.putFloat(y0);
		buffer.putFloat(column.vertices(yIndex).getPreparedTexCoord(0));
		buffer.putFloat(column.vertices(yIndex).getPreparedTexCoord(1));
		for(int j = 0; j < Vertex.maxMatCount; j++)
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices(yIndex).alpha(j)));
		buffer.put((byte)Byte.MAX_VALUE);
		
		buffer.putFloat(x0);
		buffer.putFloat(y1);
		buffer.putFloat(column.vertices(yIndex).getPreparedTexCoord(0));
		buffer.putFloat(column.vertices(yIndex).getPreparedTexCoord(2));
		for(int j = 0; j < Vertex.maxMatCount; j++)
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices(yIndex).alpha(j)));
		buffer.put((byte)Byte.MAX_VALUE);
		
		buffer.putFloat(x0);
		buffer.putFloat(y2);
		buffer.putFloat(column.vertices(yIndex).getPreparedTexCoord(0));
		buffer.putFloat(column.vertices(yIndex).getPreparedTexCoord(3));
		for(int j = 0; j < Vertex.maxMatCount; j++)
//			buffer.put((byte)0);
			buffer.put((byte)(Byte.MAX_VALUE*column.vertices(yIndex).alpha(j)));
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

	@Override
	protected void arrayChanged() {
		patchesNeedUpdate = true;		
	}
}
