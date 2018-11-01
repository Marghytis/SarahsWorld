package world.render;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import core.Listener;
import core.Renderer;
import core.Updater;
import effects.Effect;
import effects.WorldEffect;
import effects.particles.ParticleEffect;
import effects.particles.ParticleEmitter;
import item.Nametag;
import main.Main;
import menu.Settings;
import render.Framebuffer;
import render.Render;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import things.Thing;
import things.ThingType;
import util.Color;
import util.math.Vec;
import world.Weather;
import world.World;
import world.data.Column;
import world.data.Dir;
import world.data.WorldData;

public class WorldWindow implements Updater, Renderer{
	
	
	//quasi static variables that are used everywhere
	public LandscapeWindow landscape;
	public float scaleX, scaleY, offsetX, offsetY, zoom = 1;
	
	private WorldData world;
	private Weather weather = new Weather();
	
	//tracking
	private List<Effect> effects = new ArrayList<>();
	private List<Effect> toAdd = new ArrayList<>();
	private List<Effect> toRemove = new ArrayList<>();
	private List<Thing> selected = new ArrayList<>();
	public List<Thing> deletionRequested = new ArrayList<>();

	//rendering
	private VAO completeWindow;
	private VAO background; ByteBuffer backgroundColorsTop, backgroundColorsBottom;
	private Framebuffer landscapeBuffer;
	private ThingWindow things;
	
	//variables that should be local but are not, due to speed optimization
	List<Thing> thingsAt = new ArrayList<>(), objectsAt = new ArrayList<>();
	List<Thing> thingsChange = new ArrayList<>();
	
	public WorldWindow(WorldData l, Column anchor, double startX, int radius) {
		this.world = l;
		World.world.window = this;
		
		//move anchor to the correct location
		while(anchor.xReal > startX) anchor = anchor.left;
		while(anchor.xReal < startX) anchor = anchor.right;
		
		things = new ThingWindow(anchor, radius);
				
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
	
	public void requestDeletion(Thing t) {
		deletionRequested.add(t);
	}
	public void select(Thing t) {

		selected.add(t);
		t.selected = true;
		t.switchedSelected = true;
	}
	
	public void deselect(Thing t) {
		
		Main.world.window.selected.remove(t);
		t.selected = false;
		t.switchedSelected = true;
	}
	
	public Weather getWeather() {
		return weather;
	}
	
	public int selectionSize() {
		return selected.size();
	}
	
	public Thing getSelection(int i) {
		return selected.get(i);
	}
	
	public ThingVAO getVAO(ThingType type) {
		return things.getVAO(type);
//		return vaos[type.ordinal];
	}
	
	void generate(double radius) {
		Main.world.generator.borders(Main.world.avatar.pos.x - radius, Main.world.avatar.pos.x + radius);
	}
	
	public boolean update2(double delta) {


		//Delete dead things
		for(Thing t : deletionRequested){
			t.disconnectFrom(t.link);
			t.remove();
		}
		deletionRequested.clear();
		
		//generate terrain
		generate(Settings.GENERATION_RADIUS);

		//update window position
		landscape.moveToColumn((int)Math.floor(Main.world.avatar.pos.x/Column.COLUMN_WIDTH));
		things.moveToColumn((int)Math.floor(Main.world.avatar.pos.x/Column.COLUMN_WIDTH));

		//update all things
		for(int type = 0; type < ThingType.types.length; type++)
		for(Column c = Main.world.window.landscape.getEnd(Dir.l); c != Main.world.window.landscape.getEnd(Dir.r).next(Dir.r); c = c.next(Dir.r))
		for(Thing t = c.things[type]; t != null; t = t.next) {
			t.update(delta);
			if(t.oldLink != t.link){
				thingsChange.add(t);
			}
		}
		thingsChange.forEach((t)-> t.applyLink());
		thingsChange.clear();
		
		world.forEachQuest((aq) -> aq.update(delta));
		return true;
	}
	
	public boolean update(final double delta){
//		delta *= Settings.timeScale;
		update2(delta);
		
		effects.addAll(toAdd);
		toAdd.clear();
		effects.removeAll(toRemove);
		toRemove.clear();

		for(int i = 0; i < effects.size(); i++){
			effects.get(i).update(delta);
			if(!effects.get(i).living()){
				effects.remove(i);
				i--;
			} else if(effects.get(i) instanceof WorldEffect){
				((WorldEffect)effects.get(i)).checkInside(landscape);
			}
		}
		
		scaleX = zoom/Main.HALFSIZE.w;
		scaleY = zoom/Main.HALFSIZE.h;
		offsetX = (float)-Main.world.avatar.pos.x;
		offsetY = (float)-Main.world.avatar.pos.y;
		ParticleEmitter.offset.set(offsetX, offsetY);
		ParticleEffect.wind.set((Listener.getMousePos(Main.WINDOW).x - Main.HALFSIZE.w)*60f/Main.HALFSIZE.w, 0);
		return false;
	}
	
	public void draw(){

		landscape.renderBackground();
		
		landscapeBuffer.bind();
		
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			landscape.renderLandscape();
		
		Framebuffer.bindNone();

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.4f);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		Render.drawSingleQuad(completeWindow, Color.WHITE, landscapeBuffer.getTex(), 0, 0, scaleX, scaleY, true, 0);
		
		things.renderThings();
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		landscape.renderWater();

		//Outlines of living things
		things.renderOutlines();
		
//		//auras
////		renderAuras();

		//draw the darkness which is crouching out of the earth
		if(Settings.DARKNESS){
			landscape.renderDarkness();
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
		world.forEachQuest((aq) -> aq.render());

	}
	
	public interface Decider { public boolean decide(ThingType type);}
	
	Consumer<Thing> boundingBoxRenderer = (t) -> {
//		Render.bounds(t.ani.tex.pixelCoords, t.pos.x, t.pos.y + t.yOffset);TODO
//		t.pos.drawPoint();
	};
	public void renderBoundingBoxes(){
		forEachThing(boundingBoxRenderer);
	}

	public Thing[] livingsAt(Vec loc){
		thingsAt.clear();
		for(int type = 0; type < ThingType.types.length; type++)
		for(Column c = Main.world.window.landscape.getEnd(Dir.l); c != Main.world.window.landscape.getEnd(Dir.r).next(Dir.r); c = c.next(Dir.r))
		for(Thing t = c.things[type]; t != null; t = t.next){
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
		for(Column c = Main.world.window.landscape.getEnd(Dir.l); c != Main.world.window.landscape.getEnd(Dir.r).next(Dir.r); c = c.next(Dir.r))
		for(Thing t = c.things[type]; t != null; t = t.next){
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
		for(Column c = Main.world.window.landscape.getEnd(Dir.l); c != Main.world.window.landscape.getEnd(Dir.r).next(Dir.r); c = c.next(Dir.r))
		for(Thing t = c.things[type]; t != null; t = t.next) {
			if(!t.equals(Main.world.avatar) && loc.containedBy(t.box.pos.x + t.pos.x, t.box.pos.y + t.pos.y + t.yOffset, t.box.size.x, t.box.size.y)){
				objectsAt.add(t);
			}
		}
		objectsAt.sort((t1, t2) -> t1.z > t2.z ? 1 : t1.z < t2.z ?  -1 : 0);
		return objectsAt.toArray(new Thing[objectsAt.size()]);
	}
	
	public List<Effect> getEffects(){
		return effects;
	}
	
	public void addEffect(Effect effect){
		toAdd.add(effect);
	}
	
	public void removeEffect(Effect effect) {
		toRemove.add(effect);
	}
	
	public void forEach(ThingType type, Consumer<Thing> cons){


		for(Column c = Main.world.window.landscape.getEnd(Dir.l); c != Main.world.window.landscape.getEnd(Dir.r).next(Dir.r); c = c.next(Dir.r))
			for(Thing thing = c.things[type.ordinal]; thing != null; thing = thing.next){
				cons.accept(thing);
			}
	}
	
	public void forEachThing(Consumer<Thing> cons){

		for(int type = 0; type < ThingType.types.length; type++)
		for(Column c = Main.world.window.landscape.getEnd(Dir.l); c != Main.world.window.landscape.getEnd(Dir.r).next(Dir.r); c = c.next(Dir.r))
		for(Thing thing = c.things[type]; thing != null; thing = thing.next){
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
