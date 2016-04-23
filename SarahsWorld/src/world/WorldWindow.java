package world;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;
import core.Window;
import effects.Effect;
import effects.particles.Fog;
import effects.particles.ParticleEffect;
import effects.particles.ParticleEmitter;
import item.ItemType;
import item.Nametag;
import main.Framebuffer;
import main.Main;
import main.Res;
import menu.Settings;
import quest.ActiveQuest;
import render.Render;
import render.Shader;
import render.TexAtlas;
import render.TexFile;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import things.Thing;
import things.ThingType;
import util.Color;
import util.math.Vec;
import world.WorldData.Column;

public class WorldWindow implements Updater, Renderer{
	public WorldData world;
//	public Chunk[] loadedChunks = new Chunk[3];
	public LandscapeWindow landscape;
	
	public List<Thing> deletionRequested = new ArrayList<>();
	

	public static List<Effect> effects = new ArrayList<>();
	public static List<Effect> toAdd = new ArrayList<>();
	
	public Framebuffer framebuffer;
	public double darknessDistance = 600;
	public ThingVAO[] vaos = new ThingVAO[ThingType.types.length];
	public VAO background; ByteBuffer backgroundColors = BufferUtils.createByteBuffer(4*4);
	List<Thing> thingsAt = new ArrayList<>(), objectsAt = new ArrayList<>();
	List<Thing> thingsChange = new ArrayList<>();
	public int radius;
	public float scaleX, scaleY, offsetX, offsetY, zoom = 1;
	
	public WorldWindow(WorldData l, Column anchor, double startX, int radius) {
		this.world = l;
		this.radius = radius;
		for(int i = 0; i < ThingType.types.length; i++){
			vaos[i] = new ThingVAO(ThingType.types[i]);
		}
				
		while(anchor.xReal > startX) anchor = anchor.left;
		while(anchor.xReal < startX) anchor = anchor.right;
		landscape = new LandscapeWindow(world, anchor, radius, (int)Math.floor(startX/Column.step));
		this.framebuffer = new Framebuffer(Window.WIDTH, Window.HEIGHT);
//		GL11.glClearColor(0.2f, 0.6f, 0.7f, 0);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClearStencil(0);
		effects.add(new Nametag());
		background = new VAO(
				new VBO(Render.standardIndex, GL15.GL_STATIC_READ),
				new VBO(Render.createBuffer(new byte[]{
						-1, -1,
						+1, -1,
						+1, +1,
						-1, +1}), GL15.GL_STATIC_DRAW, 2*Byte.BYTES,
						new VAP(2, GL11.GL_BYTE, false, 0)),
				new VBO(backgroundColors, GL15.GL_STREAM_DRAW, 4*Byte.BYTES,
						new VAP(4, GL11.GL_BYTE, true, 0)));
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
			}
		}
		
		for(ActiveQuest aq : world.quests){
			aq.update(delta);
		}
		scaleX = zoom/Window.WIDTH_HALF;
		scaleY = zoom/Window.HEIGHT_HALF;
		offsetX = (float)-world.world.avatar.pos.x;
		offsetY = (float)-world.world.avatar.pos.y;
		ParticleEffect.wind.set((Listener.getMousePos().x - Window.WIDTH_HALF)*60f/Window.WIDTH_HALF, 0);
		return false;
	}
	byte[] color = new byte[4];
	public void draw(){
		
		renderBackground();
		
//		GL11.glLoadIdentity();
//		if(Settings.DRAW == GL11.GL_LINE_STRIP) GL11.glClearColor(0, 0, 0, 1);
//		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
//		GL11.glTranslated(Window.WIDTH_HALF - Main.world.avatar.pos.x, Window.HEIGHT_HALF - Main.world.avatar.pos.y, 0);
//
//		GL11.glColor4f(1, 1, 1, 1);
//		//things even behind the landscape
//		for(int i = -5; i < 0; i++){
//			int i2 = i;//effectively final...
//			renderThings((t) -> t.behind == i2);
//		}
//		
		renderLandscape();
		
		
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
		renderThings((t) -> true);
		Core.checkGLErrors(true, true, "after");
		
		renderWater();
		
		renderDarkness();
		
//		renderItemsInHand();
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
//		GL11.glVertex2d(Window.WIDTH, 0);
//		GL11.glTexCoord2d(1, 1);
//		GL11.glVertex2d(Window.WIDTH, Window.HEIGHT);
//		GL11.glTexCoord2d(0, 1);
//		GL11.glVertex2d(0, Window.HEIGHT);
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
		ParticleEmitter.offset.set(- Main.world.avatar.pos.x - Window.WIDTH_HALF, - Main.world.avatar.pos.y - Window.HEIGHT_HALF);
		for(Effect effect : effects){
			if(effect instanceof Fog){
				effect.render(scaleX, scaleY);
			}
		}
//
//		for(ActiveQuest aq : world.quests){
//			aq.render();
//		}
	}
	
	public void renderBackground(){
		Column rightBorder = landscape.right;
		while(rightBorder.xReal > world.world.avatar.pos.x + Window.WIDTH_HALF) rightBorder = rightBorder.left;
		Column leftBorder = landscape.left;
		while(leftBorder.xReal < world.world.avatar.pos.x - Window.WIDTH_HALF) leftBorder = leftBorder.right;
		rightBorder.lowColor.bytes(color);
		backgroundColors.put(color);
		leftBorder.lowColor.bytes(color);
		backgroundColors.put(color);
		rightBorder.topColor.bytes(color);
		backgroundColors.put(color);
		leftBorder.topColor.bytes(color);
		backgroundColors.put(color);
		backgroundColors.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, background.vbos[1].handle);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, backgroundColors);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		Res.darknessShader.bind();
		Res.darknessShader.set("transform", 0, 0, 1, 1);
		background.bindStuff();
			GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0);
		background.unbindStuff();
		Shader.bindNone();
	}
	
	public void renderLandscape(){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		LandscapeWindow.layersDrawn = 0;

		//LANDSCAPE
		Res.landscapeShader.bind();
		Res.landscapeShader.set("transform", offsetX, offsetY, scaleX, scaleY);
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
	
	public interface Decider { public boolean decide(Thing t);}
	public void renderThings(Decider d){

		//add things to the buffer that should be visible
		for(int type = 0; type < ThingType.types.length; type++)
		for(int col = 0; col < landscape.columns.length; col++)
		for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next)
			if(t.pos.x >= Main.world.avatar.pos.x - radius*Column.step && t.pos.x <= Main.world.avatar.pos.x + radius*Column.step)
				t.setVisible(true);
		//remove things from the buffer that should not be visible
		for(int type = 0; type < ThingType.types.length; type++) {
			for(int t = 0; t <= vaos[type].lastUsedIndex; t++){
				if(vaos[type].things[t].pos.x < Main.world.avatar.pos.x - radius*Column.step || vaos[type].things[t].pos.x > Main.world.avatar.pos.x + radius*Column.step){
					vaos[type].things[t].setVisible(false);
				}
			}
		}

		Res.thingShader.bind();
		Res.thingShader.set("scale", scaleX, scaleY);
		Res.thingShader.set("offset", -(float)Main.world.avatar.pos.x, -(float)Main.world.avatar.pos.y);
		
		for(int type = 0; type < ThingType.types.length; type++) {
			//render Thing
			TexAtlas tex = ThingType.types[type].file;
			tex.file.bind();
			Res.thingShader.set("box", tex.pixelCoords[0], tex.pixelCoords[1], tex.pixelCoords[2], tex.pixelCoords[3]);
			Res.thingShader.set("texWH", tex.w2, tex.h2);

			for(int col = 0; col < landscape.columns.length; col++)
			for(Thing t = landscape.columns[col].things[type]; t != null; t = t.next){
				if(d.decide(t)){
					t.prepareRender();
				}
			}

			vaos[type].vao.bindStuff();
				GL11.glDrawArrays(GL11.GL_POINTS, 0, vaos[type].lastUsedIndex+1);
			vaos[type].vao.unbindStuff();
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
		landscape.vaoDarkness.bindStuff();
			landscape.drawDarkness();
		landscape.vao.unbindStuff();
		Shader.bindNone();
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
	
	public void setPos(double x){
//		int chunk = (int)Math.floor(x/Chunk.realSize);
//		//find the chunk at that location
//		Chunk current = loadedChunks[1];
//		while(current.xIndex > chunk && current.left != null){
//			current = current.left;
//		}
//		while(current.xIndex < chunk && current.right != null){
//			current = current.right;
//		}
//		//set the found chunks to be loaded
//		loadedChunks[0] = current.left;//Neighbors cannot be null at this point
//		loadedChunks[1] = current;
//		loadedChunks[2] = current.right;

		landscape.moveTo((int)Math.floor(x/Column.step));
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
		thingsAt.sort((t1, t2) -> t1.behind > t2.behind ? 1 : t1.behind < t2.behind ?  -1 : 0);
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
		objectsAt.sort((t1, t2) -> t1.behind > t2.behind ? 1 : t1.behind < t2.behind ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}
	
	public void forEach(ThingType type, Consumer<Thing> cons){

		for(int chunk = 0; chunk < loadedChunks.length; chunk++)
		for(int col = 0; col < loadedChunks[chunk].columns.length; col++)
		for(Thing cursor = loadedChunks[chunk].columns[col].things[type.ordinal]; cursor != null; cursor = cursor.next){
			cons.accept(cursor);
		}
		
	}
	
	public void forEachThing(Consumer<Thing> cons){

		for(int type = 0; type < ThingType.types.length; type++)
		for(int chunk = 0; chunk < loadedChunks.length; chunk++)
		for(int col = 0; col < loadedChunks[chunk].columns.length; col++)
		for(Thing cursor = loadedChunks[chunk].columns[col].things[type]; cursor != null; cursor = cursor.next){
			cons.accept(cursor);
		}
	}

	public static String getDayTime() {
		return "evening";
	}

	public String debugName() {
		return "World Window";
	}
}
