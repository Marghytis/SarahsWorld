package world;

import item.Nametag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import main.Framebuffer;
import main.Main;
import main.Shader20;
import menu.Settings;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import quest.ActiveQuest;
import render.TexFile;
import render.Texture;
import things.Thing;
import things.ThingType;
import util.Color;
import util.Render;
import util.math.Vec;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.generation.Biome;
import core.Renderer;
import core.Updater;
import core.Window;
import effects.Effect;

public class WorldWindow implements Updater, Renderer{
	public WorldData world;
	public Column rightEnd, leftEnd;
	public int xIndex, r;
	
	public List<Thing> deletionRequested = new ArrayList<>();
	

	public List<Effect> effects = new ArrayList<>();
	public List<Effect> toAdd = new ArrayList<>();
	
	public Framebuffer framebuffer;
	
	/**
	*For this, the Layer must contain startX, otherwise you'll get a NPE somewhere.
	*/
	public WorldWindow(WorldData l, int startX, int startRadius) {
		this.world = l;
		this.rightEnd = l.get(startX);
		this.leftEnd = rightEnd;
		setRadius(startRadius);
		this.framebuffer = new Framebuffer(Window.WIDTH, Window.HEIGHT);
		GL11.glClearColor(0.7f, 0.7f, 0.9f, 0);
		GL11.glClearStencil(0);
		effects.add(new Nametag());
	}
	
	public static double darknessDistance = 600;
	
	public static VertexRenderer defaultRenderer = (cursor, i, matIndex, tex) -> {

		double texX = cursor.xReal/tex.w + tex.texCoords[0];
		double texY1 = cursor.vertices[i].y/tex.h + tex.texCoords[1];
		double texY2 = cursor.vertices[i+1].y/tex.h + tex.texCoords[1];

		GL11.glColor4d(Color.boundR, Color.boundG, Color.boundB, Color.boundAlpha*cursor.vertices[i].alphas[matIndex]);
			GL11.glTexCoord2d(texX, texY1);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i].y);

			GL11.glTexCoord2d(texX, texY2);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i+1].y);
	},
	blenderer = (cursor, i, matIndex, tex) -> {

		double texX = cursor.xReal/tex.w + tex.texCoords[0];
		double texY1 = cursor.vertices[i+1].y/tex.h + tex.texCoords[1];
		double texY2 = (cursor.vertices[i+1].y - cursor.vertices[i].transitionHeight)/tex.h;


		GL11.glColor4d(Color.boundR, Color.boundG, Color.boundB, Color.boundAlpha*cursor.vertices[i].alphas[matIndex]);
			GL11.glTexCoord2d(texX, texY1);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[i+1].y);

		GL11.glColor4d(Color.boundR, Color.boundG, Color.boundB, 0);
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
	
	List<Thing> thingsAt = new ArrayList<>(), objectsAt = new ArrayList<>();
	
	public Thing[] livingsAt(Vec loc){
		thingsAt.clear();
		for(int type = 0; type < ThingType.types.length; type++)
		for(Thing t = leftEnd.left.things[type]; t != rightEnd.things[type];t = t.right){
			if(t.type.life != null && !t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y, t.box.size.x, t.box.size.y)){
				thingsAt.add(t);
			}
		}
		thingsAt.sort((t1, t2) -> t1.behind > t2.behind ? 1 : t1.behind < t2.behind ?  -1 : 0);
		return thingsAt.toArray(new Thing[thingsAt.size()]);
	}
	
	public Thing[] objectsAt(Vec loc){
		objectsAt.clear();
		for(int type = 0; type < ThingType.types.length; type++)
		for(Thing t = leftEnd.left.things[type]; t != rightEnd.things[type];t = t.right){
			if(t.type.life == null && t.type != ThingType.DUMMY && !t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y, t.box.size.x, t.box.size.y)){
				objectsAt.add(t);
			}
		}
		objectsAt.sort((t1, t2) -> t1.behind > t2.behind ? 1 : t1.behind < t2.behind ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}
	
	public boolean update(final double delta){
//		delta *= Settings.timeScale;

		//Delete dead things
		for(Thing t : deletionRequested){
			if(t.type != ThingType.DUMMY){
				t.disconnect();
				t.remove();
			}
		}
		deletionRequested.clear();
		
		//generate terrain
		int radius = (int)(Window.WIDTH_HALF/Column.step) + 2;
		Main.world.generator.borders(Main.world.avatar.pos.x - (radius*Column.step), Main.world.avatar.pos.x + (radius*Column.step));
		
		//update window position
		setPos((int)(Main.world.avatar.pos.x/Column.step));
		
		//update all things
		forEachThing((t) -> t.link.copyReal(t));
		forEachThing((t) -> {
			t.update(delta);
			t.link.add2(t);//only the temp neighbors get set, so that in this loop the order doesn't change
		});//loop the things via temporary connections, to repair the connections which only got set in the left direction before (hope this doesn't create problems
		for(int i = 0; i < ThingType.types.length; i++) {
			for(Thing cursor = leftEnd.right.things[i]; cursor != null && cursor != rightEnd.left.things[i]; cursor = cursor.rightTemp){
				if(cursor.type != ThingType.DUMMY) cursor.link.copyTemp(cursor);
			}
		}
		
		effects.addAll(toAdd);
		toAdd.clear();

		for(int i = 0; i < effects.size(); i++){
			effects.get(i).update(delta);
			if(!effects.get(i).living()){
				effects.remove(i);
				i--;
			}
		}
		
		for(ActiveQuest aq : world.quests){
			aq.update(delta);
		}
		return false;
	}
	
	public void draw(){
		GL11.glLoadIdentity();
		if(Settings.DRAW == GL11.GL_LINE_STRIP) GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glTranslated(Window.WIDTH_HALF - Main.world.avatar.pos.x, Window.HEIGHT_HALF - Main.world.avatar.pos.y, 0);

		GL11.glColor4f(1, 1, 1, 1);
		//things even behind the landscape
		for(int i = -5; i < 0; i++){
			int i2 = i;//effectively final...
			renderThings((t) -> t.behind == i2);
		}
		
		renderLandscape();
		
		//auras
//		renderAuras();
		
		//Things
		GL11.glColor4f(1, 1, 1, 1);
		renderThings((t) -> t.behind == 0);
		
		//draw water on top
		renderWater();

		framebuffer.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glColor4f(1, 1, 1, 1);
			//render things that are in front of everything else
			for(int i = 1; i <= 5; i++){
				int i2 = i;//effectively final...
				renderThings((t) -> t.behind == i2);
			}
			//living things can be seen through other things
			GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Shader20.OUTLINE.bind();
			GL20.glUniform4f(GL20.glGetUniformLocation(Shader20.OUTLINE.handle, "color"), 0, 0f, 0.3f, 0.5f);
			renderThings((t) -> t.type == ThingType.SARAH);
			Shader20.bindNone();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		framebuffer.release();
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(0, 0);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(Window.WIDTH, 0);
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(Window.WIDTH, Window.HEIGHT);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(0, Window.HEIGHT);
		GL11.glEnd();
		GL11.glPopMatrix();

		//draw the darkness which is crouching out of the earth
		if(Settings.DARKNESS){
			renderDarkness();
		}

		//draw bounding boxes of all things and their anchor points
		if(Settings.SHOW_BOUNDING_BOX){
			renderBoundingBoxes();
		}
		for(Effect effect : effects){
			effect.render();
		}

		for(ActiveQuest aq : world.quests){
			aq.render();
		}
	}
	
	public void renderLandscape(){
		/*I sadly had to split up the rendering to left and right from the origin,
		 * because the generator generates from the middle to the outside
		 * I could've changed the generator to do it's task left and right differently, but that would've been a lot more to do.
		*/
		if(rightEnd.xIndex < 0){
			renderLayers(true, rightEnd, leftEnd);
		} else if(leftEnd.xIndex > 0){
			renderLayers(false, leftEnd, rightEnd);
		} else {
			Column middleColumn = leftEnd;
			while(middleColumn.xIndex < 0) middleColumn = middleColumn.right;
			renderLayers(true, middleColumn, leftEnd);
			renderLayers(false, middleColumn, rightEnd);
		}
	}
	
	public void renderWater(){
		TexFile.bindNone();
		waterColor.bind();
		for(Vertex v : water){
			renderWater(v, v.parent.xIndex < 0);
		}
		water.clear();
	}
	public interface Decider { public boolean decide(Thing t);}
	public void renderThings(Decider d){
		for(int i = 0; i < ThingType.types.length; i++) {
			
			//render Thing
			ThingType.types[i].file.file.bind();
			GL11.glBegin(GL11.GL_QUADS);
			//the pointer thing in the columns is always the dummy of its kind and is the one before the things of the next column
			for(Thing cursor = leftEnd.left.things[i]; cursor != rightEnd.things[i]; cursor = cursor.right){
				if(cursor.type != ThingType.DUMMY && d.decide(cursor)){
					cursor.render();
				}
			}
			GL11.glEnd();
			
			//render the
		}
	}
	
//	public void renderAuras(){
//		framebuffer.bind();
//		GL11.glClearColor(1, 1, 1, 0);
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//		Shader20.AURA.bind();
////		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
//		for(int i = 0; i < ThingType.values().length; i++) {
//			for(Thing cursor = leftEnd.left.things[i]; cursor != rightEnd.things[i]; cursor = cursor.right){
//				if(cursor.type != ThingType.DUMMY && cursor.aura != null){
//					cursor.aura.render(cursor.pos.x, cursor.pos.y);
//				}
//			}
//		}
//		Shader20.bindNone();
//		framebuffer.release();
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//
//		GL11.glPushMatrix();
//		GL11.glLoadIdentity();
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.texture);
//		GL11.glBegin(GL11.GL_QUADS);
//		GL11.glTexCoord2f(0, 0);	GL11.glVertex2i(0, 0);
//		GL11.glTexCoord2f(1, 0);	GL11.glVertex2i(Window.WIDTH, 0);
//		GL11.glTexCoord2f(1, 1);	GL11.glVertex2i(Window.WIDTH, Window.HEIGHT);
//		GL11.glTexCoord2f(0, 1);	GL11.glVertex2i(0, Window.HEIGHT);
//		GL11.glEnd();
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
//		GL11.glPopMatrix();
//	}
	
	public void renderDarkness(){
		TexFile.bindNone();
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for(Column cursor = leftEnd; cursor != rightEnd.right; cursor = cursor.right){
			GL11.glColor4d(0, 0, 0, 0);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[0].y);
			GL11.glColor4d(0, 0, 0, 1);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[0].y - darknessDistance);
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for(Column cursor = leftEnd; cursor != rightEnd.right; cursor = cursor.right){
			GL11.glColor4d(0, 0, 0, 1);
			GL11.glVertex2d(cursor.xReal, cursor.vertices[0].y - darknessDistance);
			GL11.glColor4d(0, 0, 0, 1);
			GL11.glVertex2d(cursor.xReal, -10000000000000000.0);
		}
		GL11.glEnd();
	}
	
	public void renderBoundingBoxes(){
		TexFile.bindNone();
		Color.WHITE.bind();
		for(int i = 0; i < ThingType.types.length; i++) {
			//the pointer thing in the columns is always the dummy of its kind and is the one before the things of the next column
			for(Thing cursor = leftEnd.left.things[i]; cursor != rightEnd.things[i]; cursor = cursor.right){
				if(cursor.type != ThingType.DUMMY){
					Render.bounds(cursor.ani.pixelCoords, cursor.pos.x, cursor.pos.y + cursor.yOffset);
					cursor.pos.drawPoint();
				}
			}
		}
	}
	
	public void renderLayers(boolean left, Column start, Column end){
		//normal layers (horizontal transitions already included)
		for(int i = 0; i < Biome.layerCount-1; i++){
			renderLayer(i, defaultRenderer, left, start, end, true);
		}
		//transition layers
		for(int i = Biome.layerCount-1; i >= 0; i--){
			renderLayer(i, blenderer, left, start, end, false);
		}
	}
	
	public List<Vertex> water = new ArrayList<>();
	
	public void renderLayer(int i, VertexRenderer renderer, boolean left, Column start, Column end, boolean markWater){

		//when going along the layer to render, this var recieves the point, where the next mat starts
		Column newMatStart = null;
		int newMatIndex = 0;
		
		//The material which is currently being rendered
		Material currentMat = start.vertices[i].mats.read.data;
		int currentMatIndex = currentMat == null ? -1 : start.vertices[i].mats.read.index;
		if(markWater && currentMat == Material.WATER){
			water.add(start.vertices[i]);
		}
		//actual fields
		if(currentMat != null){
			currentMat.tex.file.bind();
			if(currentMat.tex.file.handle == 0) currentMat.color.bind();
			else Color.WHITE.bind();
			GL11.glBegin(Settings.DRAW);
		}
		Column cursor = start;
		while(cursor != end){

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
					
					if(markWater && currentMat == Material.WATER){
						water.add(cursor.vertices[i]);
					}
					
					currentMat.tex.file.bind();
					if(currentMat.tex.file.handle == 0) currentMat.color.bind();
					else Color.WHITE.bind();
					GL11.glBegin(Settings.DRAW);
					
					//draw vertices
					renderer.renderVertex(cursor, i, currentMatIndex, currentMat.tex);
				}
			}
			cursor = left ? cursor.left : cursor.right;
		}
		GL11.glEnd();
	}
	
	public static Color waterColor = new Color(0.4f, 0.4f, 1f, 0.75f);
	
	public void renderWater(Vertex vert, boolean left){
		GL11.glBegin(Settings.DRAW);
			for(Column cursor = vert.parent; cursor != null && !cursor.vertices[vert.yIndex].mats.empty() && cursor.vertices[vert.yIndex].mats.read.data == Material.WATER; cursor = left ? cursor.left : cursor.right){
				defaultRenderer.renderVertex(cursor, vert.yIndex, vert.mats.read.index, Material.WATER.tex);
			}
		GL11.glEnd();
		GL11.glBegin(Settings.DRAW);
			for(Column cursor = vert.parent; cursor != null && !cursor.vertices[vert.yIndex].mats.empty() && cursor.vertices[vert.yIndex].mats.read.data == Material.WATER; cursor = left ? cursor.left : cursor.right){
				blenderer.renderVertex(cursor, vert.yIndex, vert.mats.read.index, Material.WATER.tex);
			}
		GL11.glEnd();
	}
	
	public interface VertexRenderer {
		public void renderVertex(Column column, int index, int matIndex, Texture tex);
	}
	
	public void setRadius(int r){
		if(r <= 0 || xIndex + r > world.mostRight.xIndex || xIndex - r < world.mostLeft.xIndex){
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
		if(xIndex + r > world.mostRight.xIndex || xIndex - r < world.mostLeft.xIndex){
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
	
	public void forEach(ThingType type, Consumer<Thing> cons){
		for(Thing cursor = leftEnd.right.things[type.ordinal]; cursor != rightEnd.left.things[type.ordinal]; cursor = cursor.right){
			if(cursor.type != ThingType.DUMMY) cons.accept(cursor);
		}
		
	}
	
	public void forEachThing(Consumer<Thing> cons){
		for(int i = 0; i < ThingType.types.length; i++) {
			for(Thing cursor = leftEnd.right.things[i]; cursor != rightEnd.left.things[i]; cursor = cursor.right){
				if(cursor.type != ThingType.DUMMY) cons.accept(cursor);
			}
		}
	}

	public static String getDayTime() {
		return "evening";
	}
}
