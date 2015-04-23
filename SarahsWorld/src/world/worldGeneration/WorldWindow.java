package world.worldGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import render.Texture;
import util.math.Vec;
import world.Material;
import world.worldGeneration.WorldData.Column;
import world.worldGeneration.objects.ai.Thing;
import world.worldGeneration.objects.ai.ThingType;
import core.Renderer;
import core.Updater;

public class WorldWindow implements Updater, Renderer{
	public WorldData layer;
	public Column rightEnd, leftEnd;
	public int xIndex, r;
	
	public List<Thing> deletionRequested = new ArrayList<>();
	
	/**
	*For this, the Layer must contain startX, otherwise you'll get a NPE somewhere.
	*/
	public WorldWindow(WorldData l, int startX, int startRadius) {
		this.layer = l;
		this.rightEnd = l.get(startX);
		this.leftEnd = rightEnd;
		setRadius(startRadius);
	}
	
	public VertexRenderer defaultRenderer = (cursor, i, matIndex, tex) -> {

		double texX = cursor.xReal/tex.file.pixelBox.size.x + tex.file.sectorPos[tex.x][tex.y].x;
		double texY1 = cursor.vertices[i].y/tex.file.pixelBox.size.y + tex.file.sectorPos[tex.x][tex.y].y;
		double texY2 = cursor.vertices[i+1].y/tex.file.pixelBox.size.y + tex.file.sectorPos[tex.x][tex.y].y;
		GL11.glColor4d(1, 1, 1, cursor.vertices[i].alphas[matIndex]);
			GL11.glTexCoord2d(texX, texY1);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i].y);
			
			GL11.glTexCoord2d(texX, texY2);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i+1].y);
	},
	blenderer = (cursor, i, matIndex, tex) -> {

		double texX = cursor.xReal/tex.file.pixelBox.size.x + tex.file.sectorPos[tex.x][tex.y].x;
		double texY1 = cursor.vertices[i+1].y/tex.file.pixelBox.size.y + tex.file.sectorPos[tex.x][tex.y].y;
		double texY2 = (cursor.vertices[i+1].y - cursor.vertices[i].transition)/tex.file.pixelBox.size.y;
		
		GL11.glColor4d(1, 1, 1, cursor.vertices[i].alphas[matIndex]);
			GL11.glTexCoord2d(texX, texY1);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i+1].y);
		
		GL11.glColor4d(1, 1, 1, 0);
			GL11.glTexCoord2d(texX, texY2);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i+1].y - cursor.vertices[i].transition);
	};
	
	public void execute(Consumer<Column> cons){
		for(Column cursor = leftEnd; cursor != null; cursor = cursor.right){
			cons.accept(cursor);
		}
	}
	
	public Thing[] livingsAt(Vec loc){
		List<Thing> things = new ArrayList<>();
		for(int type = 0; type < ThingType.values().length; type++)
		for(Thing t = leftEnd.left.things[type]; t != rightEnd.things[type];t = t.right){
			if(t.life != null && !t.equals(this) && loc.containedBy(t.ani.box.pos.x + t.pos.p.x, t.ani.box.pos.y + t.pos.p.y, t.ani.box.size.x, t.ani.box.size.y)){
				things.add(t);
			}
		}
		things.sort((t1, t2) -> t1.ani.behind > t2.ani.behind ? 1 : t1.ani.behind < t2.ani.behind ?  -1 : 0);
		return things.toArray(new Thing[things.size()]);
	}
	
	public boolean update(double delta){
		for(Thing t : deletionRequested){
			t.disconnect();
		}
		for(int type = 0; type < ThingType.values().length; type++)
		for(Thing t = leftEnd.left.things[type]; t != rightEnd.things[type];t = t.right){
			t.update(delta);
		}
		return false;
	}
	
	public void draw(){
		//normal layers
		for(int i = 0; i < leftEnd.vertices.length-1; i++){
			renderLayer(i, defaultRenderer);
		}
		//transition layers
		for(int i = 0; i < leftEnd.vertices.length-1; i++){
			renderLayer(i, blenderer);
		}
		//things
		for(int i = 0; i < ThingType.values().length; i++) {
			ThingType.values()[i].file.bind();
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			//the pointer thing in the columns is always the dummy of its kind and is the one before the things of the next column 
			for(Thing cursor = leftEnd.left.things[i].right; cursor != rightEnd.things[i].right; cursor = cursor.right){
				cursor.render();
			}
			GL11.glEnd();
		}
	}
	
	public void renderLayer(int i, VertexRenderer renderer){

		Column newMatStart = null;
		int newMatIndex = 0;
		
		Material currentMat = leftEnd.vertices[i].mats.first.data;
		int currentMatIndex = 0;
		
		//actual fields
		currentMat.tex.file.bind();
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		Column cursor = leftEnd;
		while(cursor != null){
			
			//check if current material hasn't vanished
			if(cursor.vertices[i].mats.get(currentMatIndex).data == currentMat){	
				if(newMatStart == null && cursor.vertices[i].mats.get(currentMatIndex).next.data != Material.NO){
					newMatStart = cursor;
					newMatIndex = currentMatIndex + 1;
				}
				//draw vertices
				renderer.renderVertex(cursor, i, currentMatIndex, currentMat.tex);
			} else {
				cursor = newMatStart;
				currentMatIndex = newMatIndex;
				currentMat = newMatStart.vertices[i].mats.get(newMatIndex).data;
				newMatStart = null;
				GL11.glEnd();
				currentMat.tex.file.bind();
				GL11.glBegin(GL11.GL_QUAD_STRIP);
			}
			cursor = cursor.right;
		}
		GL11.glEnd();
	}
	
	public interface VertexRenderer {
		public void renderVertex(Column column, int index, int matIndex, Texture tex);
	}
	
	public void setRadius(int r){
		if(r <= 0 || xIndex + r > layer.mostRight.xIndex || xIndex - r < layer.mostLeft.xIndex){
			(new Exception("Can't change radius, not enough lines generated. OR Radius may not be less than 1.")).printStackTrace();
		} else {
			for(; this.r > r; this.r--){
				leftEnd = leftEnd.right;
				rightEnd = rightEnd.left;
			}
			for(; this.r < r; this.r++){
				leftEnd = leftEnd.left;
				rightEnd = rightEnd.right;
			}
		}
	}
	public void setPos(int xIndex){
		if(xIndex + r > layer.mostRight.xIndex || xIndex - r < layer.mostLeft.xIndex){
			(new Exception("Can't change radius, not enough lines generated. OR Radius may not be less than 1.")).printStackTrace();
		} else {
			for(; this.xIndex > xIndex; this.xIndex--){
				if(leftEnd.left != null && rightEnd.left != null){
					leftEnd = leftEnd.left;
				} else break;
			}
			for(; this.xIndex < xIndex; this.xIndex++){
				if(rightEnd.right != null && leftEnd.right != null){
					rightEnd = rightEnd.right;
					leftEnd = leftEnd.right;
				} else break;
			}
		}
	}
}
