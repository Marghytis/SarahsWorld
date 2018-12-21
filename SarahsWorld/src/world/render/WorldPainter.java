package world.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import core.Renderer;
import core.Updater;
import effects.Effect;
import effects.particles.ParticleEffect;
import effects.particles.ParticleEmitter;
import item.Nametag;
import main.Main;
import main.Res;
import menu.Settings;
import render.Animator;
import render.Framebuffer;
import render.Render;
import render.VAO;
import things.Thing;
import util.Color;
import util.math.Vec;
import world.World;
import world.data.WorldData;
import world.window.BackgroundWindow;
import world.window.TerrainWindow;
import world.window.ThingWindow;

public class WorldPainter implements Updater, Renderer{
	
	private WorldData world;
	
	//tracking
	private EffectManager effects;
	private List<Thing> selected = new ArrayList<>();

	//rendering
	private VAO completeWindow;
	private Framebuffer landscapeBuffer;
	private TerrainWindow terrain;
	private BackgroundWindow background;
	private ThingWindow things;
	
	//others
	Animator death = new Animator(Res.death, () -> {}, false);
	
	public WorldPainter(WorldData l, ThingWindow things, TerrainWindow landscape, BackgroundWindow background) {
		this.world = l;
		World.world.window = this;
		this.things = things;
		this.terrain = landscape;
		this.background = background;

		landscapeBuffer = new Framebuffer("Landscape", Main.SIZE.w, Main.SIZE.h);
		this.completeWindow = Render.quadInScreen(-Main.HALFSIZE.w, Main.HALFSIZE.h, Main.HALFSIZE.w, -Main.HALFSIZE.h);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClearStencil(0);
		
		effects = new EffectManager();
		addEffect(new Nametag());
		l.getWeather().addEffects();
	}
	
	public Vec toWorldPos(Vec windowPos) {
		windowPos.shift(-Main.HALFSIZE.w, -Main.HALFSIZE.h);
		windowPos.scale(1/Settings.getDouble("ZOOM"));
		windowPos.shift(-Render.offsetX, -Render.offsetY);
		return windowPos;
	}
	
	public void select(Thing t) {

		selected.add(t);
		t.selected = true;
		t.switchedSelected = true;
	}
	
	public void deselect(Thing t) {
		
		selected.remove(t);
		t.selected = false;
		t.switchedSelected = true;
	}
	
	public int selectionSize() {
		return selected.size();
	}
	
	public Thing getSelection(int i) {
		return selected.get(i);
	}
	
	public boolean update(final double delta){
//		delta *= Settings.timeScale;
		
		effects.update(delta);
		
		if(world.isGameOver()) {
			death.update(delta);
		}
		return false;
	}
	
	Vec lastAvatarPos = new Vec(), avatarPos = new Vec(), offset = new Vec();
	public void updateTransform(double interpolationShift) {

		Render.scaleX = (float)(Settings.getDouble("ZOOM")/Main.HALFSIZE.w);
		Render.scaleY = (float)(Settings.getDouble("ZOOM")/Main.HALFSIZE.h);
		
		lastAvatarPos.set(Main.world.engine.lastAvatarPosition).set(Main.world.avatar.pos);
		avatarPos.set(Main.world.avatar.pos);
		
		offset.set(avatarPos).shift(avatarPos.shift(lastAvatarPos, -1), interpolationShift);
		
		Render.offsetX = (float)-offset.x;
		Render.offsetY = (float)-offset.y;
	}
	
	public void draw(double interpolationShift){
		
//		if(Core.updatedToLong) {
//			for(int i = 0; i < Main.world.engine.timeIndex; i++) {
//				System.out.println(Main.world.engine.lastTimes[0][i] + "  " + Main.world.engine.lastTimes[1][i] + "  " + Main.world.engine.lastTimes[2][i] + "  " + Main.world.engine.lastTimes[3][i]);
//			}
//		}
//		Main.world.engine.timeIndex = 0;
		
		updateTransform(interpolationShift);

		background.renderBackground();
		
		landscapeBuffer.bind();
		
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			terrain.renderLandscape();
		
		Framebuffer.bindNone();

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.4f);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		Render.drawSingleQuad(completeWindow, Color.WHITE, landscapeBuffer.getTex(), 0, 0, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h, true, 0);
		
		things.renderThings();
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		terrain.renderWater();

		//Outlines of living things
		things.renderOutlines();
		
		//auras
//		renderAuras();

		//draw the darkness which is crouching out of the earth
		if(Settings.getBoolean("DARKNESS")){
			background.renderDarkness();
		}
//
		//draw bounding boxes of all things and their anchor points
		if(Settings.getBoolean("SHOW_BOUNDING_BOX")){
			things.renderBoundingBoxes();
		}
		
		//effects
		ParticleEmitter.offset.set(Render.offsetX, Render.offsetY);
		ParticleEffect.wind.set((Main.input.getMousePos(Main.WINDOW).x - Main.HALFSIZE.w)*60f/Main.HALFSIZE.w, 0);
		effects.forEach(Effect::render);

		//quests
		world.forEachQuest((aq) -> aq.render());
		
		if(world.isGameOver()) {

			Render.drawSingleQuad(completeWindow, Color.BLACK, null, 0, 0, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h, false, 0);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			death.bindTex();
			death.quad.render(new Vec(), Render.scaleX);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
		}
	}

	public void forEachEffect(Consumer<Effect> cons) {
		effects.forEach(cons);
	}
	
	public void addEffect(Effect effect){
		effects.addEffect(effect);
	}
	
	public void removeEffect(Effect effect) {
		effects.removeEffect(effect);
	}
	
	public static String getDayTime() {
		return "evening";
	}

	public String debugName() {
		return "World Window";
	}

}
