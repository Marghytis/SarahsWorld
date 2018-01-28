package world;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import core.*;
import effects.*;
import effects.particles.*;
import item.*;
import main.*;
import menu.Settings;
import quest.ActiveQuest;
import render.*;
import render.VBO.VAP;
import things.*;
import util.Color;
import util.math.Vec;
import world.data.*;

public class WorldWindow implements Updater, Renderer{
	public WorldData world;
//	public Chunk[] loadedChunks = new Chunk[3];
	public LandscapeWindow landscape;
	Framebuffer landscapeBuffer;
	
	public List<Thing> deletionRequested = new ArrayList<>();
	

	public static Weather weather = new Weather();
	private static List<Effect> effects = new ArrayList<>();
	public static List<Effect> toAdd = new ArrayList<>();
	
	VAO completeWindow;
	public double darknessDistance = 600;
	public ThingVAO[] vaos = new ThingVAO[ThingType.types.length];
	public VAO background; ByteBuffer backgroundColorsTop, backgroundColorsBottom;
	List<Thing> thingsAt = new ArrayList<>(), objectsAt = new ArrayList<>();
	List<Thing> thingsChange = new ArrayList<>();
	public int radius;
	public float scaleX, scaleY, offsetX, offsetY, zoom = 1;
	public List<Thing> selected = new ArrayList<>();
	
	public WorldWindow(WorldData l, Column anchor, double startX, int radius) {
		this.world = l;
		World.world.window = this;
		this.radius = radius;
		for(int i = 0; i < ThingType.types.length; i++){
			vaos[i] = new ThingVAO(ThingType.types[i]);
		}
				
		while(anchor.xReal > startX) anchor = anchor.left;
		while(anchor.xReal < startX) anchor = anchor.right;
		landscape = new LandscapeWindow(world, anchor, radius, (int)Math.floor(startX/Column.COLUMN_WIDTH));
		landscapeBuffer = new Framebuffer("Landscape", Main.SIZE.w, Main.SIZE.h);
		this.completeWindow = Render.quadInScreen(-Main.HALFSIZE.w, Main.HALFSIZE.h, Main.HALFSIZE.w, -Main.HALFSIZE.h);
//		this.backgroundColors = BufferUtils.createByteBuffer(4*4);
		this.backgroundColorsTop = BufferUtils.createByteBuffer((Main.SIZE.w/100 + 1)*4);
		this.backgroundColorsBottom = BufferUtils.createByteBuffer((Main.SIZE.w/100 + 1)*4);
//		GL11.glClearColor(0.2f, 0.6f, 0.7f, 0);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClearStencil(0);
		effects.add(new Nametag());
		weather.addEffects();
//		background = new VAO(
//				new VBO(Render.standardIndex, GL15.GL_STATIC_READ),
//				new VBO(Render.createBuffer(new byte[]{
//						-1, -1,
//						+1, -1,
//						+1, +1,
//						-1, +1}), GL15.GL_STATIC_DRAW, 2*Byte.BYTES,
//						new VAP(2, GL11.GL_BYTE, false, 0)),
//				new VBO(backgroundColors, GL15.GL_STREAM_DRAW, 4*Byte.BYTES,
//						new VAP(4, GL11.GL_BYTE, true, 0)));
		background = new VAO(
				new VBO(Render.standardIndex, GL15.GL_STATIC_READ),
				new VBO(Render.createBuffer(new byte[]{
						-1, -1,
						+1, -1,
						+1, +1,
						-1, +1}), GL15.GL_STATIC_DRAW, 2*Byte.BYTES,
						new VAP(2, GL11.GL_BYTE, false, 0)/*Positions*/));
	}
	
	public boolean update(final double delta){
//		delta *= Settings.timeScale;

		//Delete dead things
		for(Thing t : deletionRequested){
			t.disconnectFrom(t.link);
			t.remove();
		}
		deletionRequested.clear();
		
		//generate terrain
		Main.world.generator.borders(Main.world.avatar.pos.x - Main.world.generator.genRadius, Main.world.avatar.pos.x + Main.world.generator.genRadius);

		
		//update window position
		setPos(Main.world.avatar.pos.x);

		//update all things
		for(int type = 0; type < ThingType.types.length; type++)
		for(int col = 0; col < landscape.columns.length; col++)
		for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next){
			t.update(delta);
			if(t.oldLink != t.link){
				thingsChange.add(t);
			}
		}
		thingsChange.forEach((t)-> t.link.add(t));
		thingsChange.clear();
		
		effects.addAll(toAdd);
		toAdd.clear();

		for(int i = 0; i < effects.size(); i++){
			effects.get(i).update(delta);
			if(!effects.get(i).living()){
				effects.remove(i);
				i--;
			} else if(effects.get(i) instanceof WorldEffect){
				((WorldEffect)effects.get(i)).checkInside(landscape);
			}
		}
		
		for(ActiveQuest aq : world.quests){
			aq.update(delta);
		}
		scaleX = zoom/Main.HALFSIZE.w;
		scaleY = zoom/Main.HALFSIZE.h;
		offsetX = (float)-world.world.avatar.pos.x;
		offsetY = (float)-world.world.avatar.pos.y;
		ParticleEmitter.offset.set(offsetX, offsetY);
		ParticleEffect.wind.set((Listener.getMousePos(Main.WINDOW).x - Main.HALFSIZE.w)*60f/Main.HALFSIZE.w, 0);
		return false;
	}
	byte[] color = new byte[4];
	public void draw(){
		
		renderBackground();
		
//		GL11.glLoadIdentity();
//		if(Settings.DRAW == GL11.GL_LINE_STRIP) GL11.glClearColor(0, 0, 0, 1);
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
//		GL11.glTranslated(Main.HALFSIZE.w - Main.world.avatar.pos.x, Main.HALFSIZE.h - Main.world.avatar.pos.y, 0);
		
		landscapeBuffer.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		renderLandscape();
		
		Framebuffer.bindNone();
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.4f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
//		renderThings((type) -> type == ThingType.GRAVE);
//		red.render(new Vec(50, 0), -0.1, 1);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		Render.drawSingleQuad(completeWindow, Color.WHITE, landscapeBuffer.getTex(), 0, 0, scaleX, scaleY, true, 0);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		renderThings((type) -> type != ThingType.CLOUD);
		renderItemsInHand();
		GL11.glAlphaFunc(GL11.GL_ALWAYS, 1.0f);
		renderThings((type) -> type == ThingType.CLOUD);
//		white.render(new Vec(-50, 0), +0.1, 1);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		renderWater();

		//Outlines of living things
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_GREATER);
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		renderThings((type) -> type.life != null, Res.thingOutlineShader);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
//		renderDarkness();
		
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
//		
//		//auras
////		renderAuras();
//		
//		//Things
//		GL11.glColor4f(1, 1, 1, 1);
//		renderThings((t) -> t.behind == 0);
//		
//		//draw water on top
//		renderWater();
//
//		framebuffer.bind();
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//			GL11.glColor4f(1, 1, 1, 1);
//			//render things that are in front of everything else
//			for(int i = 1; i <= 5; i++){
//				int i2 = i;//effectively final...
//				renderThings((t) -> t.behind == i2);
//			}
//			//living things can be seen through other things
//			GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//			Shader20.OUTLINE.bind();
//			GL20.glUniform4f(GL20.glGetUniformLocation(Shader20.OUTLINE.handle, "color"), 0, 0f, 0.3f, 0.5f);
//			renderThings((t) -> t.type == ThingType.SARAH);
//			Shader20.bindNone();
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//		framebuffer.release();
//		GL11.glPushMatrix();
//		GL11.glLoadIdentity();
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.texture);
//		GL11.glBegin(GL11.GL_QUADS);
//		GL11.glTexCoord2d(0, 0);
//		GL11.glVertex2d(0, 0);
//		GL11.glTexCoord2d(1, 0);
//		GL11.glVertex2d(Main.SIZE.w, 0);
//		GL11.glTexCoord2d(1, 1);
//		GL11.glVertex2d(Main.SIZE.w, Main.SIZE.h);
//		GL11.glTexCoord2d(0, 1);
//		GL11.glVertex2d(0, Main.SIZE.h);
//		GL11.glEnd();
//		GL11.glPopMatrix();
//
		//draw the darkness which is crouching out of the earth
		if(Settings.DARKNESS){
			renderDarkness();
		}
//
//		//draw bounding boxes of all things and their anchor points
//		if(Settings.SHOW_BOUNDING_BOX){
//			renderBoundingBoxes();
//		}
		for(Effect effect : effects){
			effect.render(scaleX, scaleY);
		}
//
		for(ActiveQuest aq : world.quests){
			aq.render();
		}

	}
	
	public void renderBackground(){

		//BACKGROUND
		Res.darknessShader.bind();
		Res.darknessShader.set("transform", offsetX, 0, scaleX, 1);
		landscape.vaoColor.bindStuff();
			landscape.drawBackground();
		landscape.vao.unbindStuff();
		Shader.bindNone();
//		Column rightBorder = landscape.right;
//		while(rightBorder.xReal > world.world.avatar.pos.x + Main.HALFSIZE.w) rightBorder = rightBorder.left;
//		Column leftBorder = landscape.left;
//		while(leftBorder.xReal < world.world.avatar.pos.x - Main.HALFSIZE.w) leftBorder = leftBorder.right;
////		rightBorder.lowColor.bytes(color);
////		backgroundColors.put(color);
////		leftBorder.lowColor.bytes(color);
////		backgroundColors.put(color);
////		rightBorder.topColor.bytes(color);
////		backgroundColors.put(color);
////		leftBorder.topColor.bytes(color);
////		backgroundColors.put(color);
////		backgroundColors.flip();
////		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, background.vbos[1].handle);
////		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, backgroundColors);
////		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//
//		backgroundColorsTop.flip();
//		backgroundColorsBottom.flip();
//		int width = rightBorder.xIndex - leftBorder.xIndex, step = width/(Main.SIZE.w/100);
//		for(Column c = leftBorder; c != rightBorder.right;){
//			c.topColor.bytes(color);
//			backgroundColorsTop.put(color);
//			c.lowColor.bytes(color);
//			backgroundColorsBottom.put(color);
//			for(int i = 0; i < step; i++)
//				c = c.right;
//		}
//		
//		Res.backgroundShader.bind();
//		Res.backgroundShader.set("transform", 0, 0, 1, 1);
//		Res.backgroundShader.set
//		background.bindStuff();
//			GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0);
//		background.unbindStuff();
//		Shader.bindNone();
	}
	
	public void renderLandscape(){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		LandscapeWindow.layersDrawn = 0;

		//LANDSCAPE
		Res.landscapeShader.bind();//Yes, don't use scaleX, scaleY here, because the landscape gets rendered into a framebuffer
		Res.landscapeShader.set("transform", offsetX, offsetY, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h);
		landscape.vao.bindStuff();
			//draw normal quads
			landscape.drawNormalQuads();
			//draw quads for vertcal transition
			if(Settings.DRAW_TRANSITIONS)
			landscape.drawTransitionQuads();
		landscape.vao.unbindStuff();
		TexFile.bindNone();
		Shader.bindNone();
	}
	
	public static Color waterColor = new Color(0.4f, 0.4f, 1f, 0.75f);
	
	public void renderWater(){
		//WATER
		Res.landscapeShader.bind();
		Res.landscapeShader.set("transform", offsetX, offsetY, scaleX, scaleY);
		landscape.vao.bindStuff();
			//draw normal quads
			landscape.drawWater();
		landscape.vao.unbindStuff();
		TexFile.bindNone();
		Shader.bindNone();
	}
	
	public interface Decider { public boolean decide(ThingType type);}
	public void renderThings(Decider d){
		renderThings(d, Res.thingShader);
	}
	
	public void renderThings(Decider d, Shader shader){

		//add things to the buffer that should be visible
		for(int type = 0; type < ThingType.types.length; type++)
		for(int col = 0; col < landscape.columns.length; col++)
		for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next)
			if(t.pos.x >= Main.world.avatar.pos.x - radius*Column.COLUMN_WIDTH && t.pos.x <= Main.world.avatar.pos.x + radius*Column.COLUMN_WIDTH)
				t.setVisible(true);
		//remove things from the buffer that should not be visible
		for(int type = 0; type < ThingType.types.length; type++) {
			for(int t = 0; t <= vaos[type].lastUsedIndex; t++){
				if(vaos[type].things[t].pos.x < Main.world.avatar.pos.x - radius*Column.COLUMN_WIDTH || vaos[type].things[t].pos.x > Main.world.avatar.pos.x + radius*Column.COLUMN_WIDTH){
					vaos[type].things[t].setVisible(false);
				}
			}
		}

		shader.bind();
		shader.set("scale", scaleX, scaleY);
		shader.set("offset", -(float)Main.world.avatar.pos.x, -(float)Main.world.avatar.pos.y);
		
		for(int type = 0; type < ThingType.types.length; type++) {
			if(vaos[type].lastUsedIndex == -1 || !d.decide(ThingType.types[type])) continue;
			//render Thing
			ThingType.types[type].file.file.bind();

			for(int col = 0; col < landscape.columns.length; col++)
			for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next){
				if(d.decide(ThingType.types[type])){
					t.prepareRender();
				}
			}

			vaos[type].vao.bindStuff();
				GL11.glDrawArrays(GL11.GL_POINTS, 0, vaos[type].lastUsedIndex+1);
			vaos[type].vao.unbindStuff();
			
			if(ThingType.types[type].ani.secondFile != null){
				ThingType.types[type].ani.secondFile.bind();

				for(int col = 0; col < landscape.columns.length; col++)
				for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next){
					if(d.decide(ThingType.types[type])){
						t.prepareSecondRender();
					}
				}

				vaos[type].vao.bindStuff();
					GL11.glDrawArrays(GL11.GL_POINTS, 0, vaos[type].lastUsedIndex+1);
				vaos[type].vao.unbindStuff();
			}
		}
		TexFile.bindNone();
		Shader.bindNone();
	}
	public void renderItemsInHand(){
		Res.thingShader.bind();
		ItemType.handheldTex.bind();
		for(int type = 0; type < ThingType.types.length; type++)
		if(ThingType.types[type].inv != null)
		for(int col = 0; col < landscape.columns.length; col++)
		for(Thing cursor = landscape.columns[col].things[type]; cursor != null; cursor = cursor.next){
			cursor.itemStacks[cursor.selectedItem].item.renderHand(cursor, cursor.itemAni);
		}
		Shader.bindNone();
		TexFile.bindNone();

		//   ||    ||    ||
		//   \/    \/    \/
//		public void partRender(Thing t){
//			ItemType selected = getSelectedItem(t);
//			if(selected == null) return;
//			if(selected.texHand != null && !selected.texHand.equals(itemAnimator.ani)){
//				itemAnimator.setTexture(selected.texHand);
//			}
	//
//			GL11.glEnd();
//			itemAnimator.bindTex();
//			
//			selected.renderHand(t, itemAnimator);
//			
//			t.ani.bindTex();
//			GL11.glBegin(GL11.GL_QUADS);
//		}
	}
	
	public void renderDarkness(){
		//DARKNESS
		Res.darknessShader.bind();
		Res.darknessShader.set("transform", offsetX, offsetY, scaleX, scaleY);
		landscape.vaoColor.bindStuff();
			landscape.drawDarkness();
		landscape.vao.unbindStuff();
		Shader.bindNone();
	}
	
	Consumer<Thing> boundingBoxRenderer = (t) -> {
//		Render.bounds(t.ani.tex.pixelCoords, t.pos.x, t.pos.y + t.yOffset);TODO
//		t.pos.drawPoint();
	};
	public void renderBoundingBoxes(){
		forEachThing(boundingBoxRenderer);
	}
	
	public void setPos(double x){
		landscape.moveTo((int)Math.floor(x/Column.COLUMN_WIDTH));
	}

	public Thing[] livingsAt(Vec loc){
		thingsAt.clear();
		for(int type = 0; type < ThingType.types.length; type++)
		for(int col = 0; col < landscape.columns.length; col++)
		for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next){
			if(t.type.life != null && !t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y + t.yOffset, t.box.size.x, t.box.size.y)){
				thingsAt.add(t);
			}
		}
		thingsAt.sort((t1, t2) -> t1.z > t2.z ? 1 : t1.z < t2.z ?  -1 : 0);
		return thingsAt.toArray(new Thing[thingsAt.size()]);
	}
	
	public Thing[] objectsAt(Vec loc){
		objectsAt.clear();
		for(int type = 0; type < ThingType.types.length; type++)
		for(int col = 0; col < landscape.columns.length; col++)
		for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next){
			if(t.type.life == null && !t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y + t.yOffset, t.box.size.x, t.box.size.y)){
				objectsAt.add(t);
			}
		}
		objectsAt.sort((t1, t2) -> t1.z > t2.z ? 1 : t1.z < t2.z ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}
	
	public Thing[] thingsAt(Vec loc){
		objectsAt.clear();
		for(int type = 0; type < ThingType.types.length; type++)
		for(int col = 0; col < landscape.columns.length; col++)
		for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next){
			if(!t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y + t.yOffset, t.box.size.x, t.box.size.y)){
				objectsAt.add(t);
			}
		}
		objectsAt.sort((t1, t2) -> t1.z > t2.z ? 1 : t1.z < t2.z ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}
	
	public static List<Effect> getEffects(){
		return effects;
	}
	
	public static void addEffect(Effect effect){
		toAdd.add(effect);
	}
	
	public void forEach(ThingType type, Consumer<Thing> cons){

		for(int col = 0; col < landscape.columns.length; col++)
			for(Thing thing = landscape.columns[col].things[type.ordinal]; thing != null; thing = thing.next){
				cons.accept(thing);
			}
	}
	
	public void forEachThing(Consumer<Thing> cons){

		for(int type = 0; type < ThingType.types.length; type++)
		for(int col = 0; col < landscape.columns.length; col++)
		for(Thing thing = landscape.columns[col].things[type]; thing != null; thing = thing.next){
			cons.accept(thing);
		}
	}

	public static String getDayTime() {
		return "evening";
	}

	public String debugName() {
		return "World Window";
	}
}
