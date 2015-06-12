package world.worldGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import main.Main;

import org.lwjgl.opengl.GL11;

import render.Texture;
import util.math.Vec;
import world.Material;
import world.worldGeneration.WorldData.Column;
import world.worldGeneration.WorldData.Vertex;
import world.worldGeneration.objects.ai.Thing;
import world.worldGeneration.objects.ai.ThingType;
import core.Renderer;
import core.Updater;
import core.Window;

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
		double texY2 = (cursor.vertices[i+1].y - cursor.vertices[i].transitionHeight)/tex.file.pixelBox.size.y;
		
		GL11.glColor4d(1, 1, 1, cursor.vertices[i].alphas[matIndex]);
			GL11.glTexCoord2d(texX, texY1);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i+1].y);
		
		GL11.glColor4d(1, 1, 1, 0);
			GL11.glTexCoord2d(texX, texY2);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i+1].y - cursor.vertices[i].transitionHeight);
	};

	public Column get(int x){
		if(x < (rightEnd.xIndex + leftEnd.xIndex)/2){
			Column cursor = leftEnd;
			for(; cursor.xIndex < x; cursor = cursor.right);
			return cursor;
		} else {
			Column cursor = rightEnd;
			for(; cursor.xIndex > x; cursor = cursor.left);
			return cursor;
		}
	}
	
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
		//Delete dead things
		for(Thing t : deletionRequested){
			t.disconnect();
		}
		
		//generate terrain
		int radius = (int)(Window.WIDTH_HALF/Column.step) + 2;
		Main.world.generator.borders(Main.world.avatar.pos.p.x - (radius*Column.step), Main.world.avatar.pos.p.x + (radius*Column.step));
		
		//update window position
		setPos((int)(Main.world.avatar.pos.p.x/Column.step));
		
		//update all things
		for(int type = 0; type < ThingType.values().length; type++)//cursor should never be null
		for(Thing cursor = leftEnd.left.things[type]; cursor != null && cursor != rightEnd.things[type];cursor = cursor.right){//could go further to the left, but who cares? :D
			if(cursor.type != ThingType.DUMMY){
				cursor.update(delta);
			}
		}
		return false;
	}
	
	public void draw(){
		GL11.glClearColor(0.7f, 0.7f, 9f, 1);
		GL11.glTranslated(Window.WIDTH_HALF - Main.world.avatar.pos.p.x, Window.HEIGHT_HALF - Main.world.avatar.pos.p.y, 0);
		
		/*I sadly had to split up the rendering to left and right from the origin,
		 * because the generator generates from the middle to the outside
		 * I could've changed the generator to do it's task left and right differently, but that would've been a lot more to do.
		*/
		if(rightEnd.xIndex < 0){
			renderLayers(true, rightEnd);
		} else if(leftEnd.xIndex > 0){
			renderLayers(false, leftEnd);
		} else {
			Column middleColumn = leftEnd;
			while(middleColumn.xIndex < 0) middleColumn = middleColumn.right;
			renderLayers(true, middleColumn);
			renderLayers(false, middleColumn);
		}
		
		GL11.glColor4f(1, 1, 1, 1);
		//things
		for(int i = 0; i < ThingType.values().length; i++) {
			ThingType.values()[i].file.bind();
			GL11.glBegin(GL11.GL_QUADS);
			//the pointer thing in the columns is always the dummy of its kind and is the one before the things of the next column
			for(Thing cursor = leftEnd.left.things[i]; cursor != rightEnd.things[i]; cursor = cursor.right){
				if(cursor.type != ThingType.DUMMY){
					cursor.render();
				}
			}
			GL11.glEnd();
		}
	}
	
	public void renderLayers(boolean left, Column start){
		//normal layers
		for(int i = 0; i < World.layerCount-1; i++){
			renderLayer(i, defaultRenderer, left, start);
		}
		//transition layers
		for(int i = World.layerCount-1; i >= 0; i--){
			renderLayer(i, blenderer, left, start);
		}
	}
	
	public void renderLayer(int i, VertexRenderer renderer, boolean left, Column start){

		//when going along the layer to render, this var recieves the point, where the next mat starts
		Column newMatStart = null;
		int newMatIndex = 0;
		
		//The material which is currently being rendered
		Material currentMat = start.vertices[i].mats.read.data;
		int currentMatIndex = currentMat == null ? -1 : start.vertices[i].mats.read.index;
		
		//actual fields
		if(currentMat != null){
			currentMat.tex.file.bind();
			GL11.glBegin(GL11.GL_QUAD_STRIP);
		}
		Column cursor = start;
		while(cursor != null){

			//detect if a new mat starts here
			if(newMatStart == null){
				if((currentMatIndex == -1 || cursor.vertices[i].mats.get(currentMatIndex).data == null) && !cursor.vertices[i].mats.empty()){//current mat ends/ hasn't started yet
						newMatStart = cursor;
						newMatIndex = cursor.vertices[i].mats.read.index;
				} else if(cursor.vertices[i].mats.get((currentMatIndex+1)%Vertex.maxMatCount).data != null){//current mat continues
						newMatStart = cursor;
						newMatIndex = (currentMatIndex+1)%Vertex.maxMatCount;
				}
			}
			if(currentMat != null){
				//draw vertices
				renderer.renderVertex(cursor, i, currentMatIndex, currentMat.tex);
			}
			if(currentMatIndex == -1 || cursor.vertices[i].mats.get(currentMatIndex).data == null || cursor.right == null) {//if the mat ends or hasn't started yet, it has to switch
				if(currentMat != null){//end old mat
					GL11.glEnd();
				}
				currentMat = null;
				currentMatIndex = -1;
				if(newMatStart != null){//Vertex is empty
					//reset to the location of the oldest new mat start
					cursor = newMatStart;
					currentMatIndex = newMatIndex;
					currentMat = newMatStart.vertices[i].mats.get(newMatIndex).data;
					newMatStart = null;
					
					currentMat.tex.file.bind();
					GL11.glBegin(GL11.GL_QUAD_STRIP);
					//draw vertices
					renderer.renderVertex(cursor, i, currentMatIndex, currentMat.tex);
				}
			}
			cursor = left ? cursor.left : cursor.right;
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
					rightEnd = rightEnd.left;
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
