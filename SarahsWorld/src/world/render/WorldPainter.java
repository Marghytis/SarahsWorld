package world.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import basis.effects.Effect;
import basis.effects.particleEffects.ParticleEffect;
import basis.effects.particleEffects.ParticleEmitter;
import core.Renderer;
import core.Updater;
import extra.Main;
import extra.Res;
import extra.effects.specialEffects.Nametag;
import extra.things.Thing;
import menu.Settings;
import render.Animator;
import render.Framebuffer;
import render.Render;
import render.VAO;
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

		landscapeBuffer = new Framebuffer("Landscape", Main.game().SIZE.w, Main.game().SIZE.h);
		this.completeWindow = Render.quadInScreen(-Main.game().SIZE_HALF.w, Main.game().SIZE_HALF.h, Main.game().SIZE_HALF.w, -Main.game().SIZE_HALF.h);
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClearStencil(0);
		
		effects = new EffectManager();
		addEffect(new Nametag());
		l.getWeather().addEffects();
	}
	
	public Vec toWorldPos(Vec windowPos) {
		windowPos.shift(-Main.game().SIZE_HALF.w, -Main.game().SIZE_HALF.h);
		windowPos.scale(1/Settings.getDouble("ZOOM"));
		windowPos.shift(-Render.offsetX, -Render.offsetY);
		return windowPos;
	}
	
	public void select(Thing t) {

		selected.add(t);
		t.aniPlug.setSelected( true);
		t.aniPlug.setSwitchedSelected( true);
	}
	
	public void deselect(Thing t) {
		
		selected.remove(t);
		t.aniPlug.setSelected( false);
		t.aniPlug.setSwitchedSelected( true);
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

		Render.scaleX = (float)(Settings.getDouble("ZOOM")/Main.game().SIZE_HALF.w);
		Render.scaleY = (float)(Settings.getDouble("ZOOM")/Main.game().SIZE_HALF.h);
		
		lastAvatarPos.set(Main.game().world.engine.lastAvatarPosition).set(Main.game().world.avatar.pos);
		avatarPos.set(Main.game().world.avatar.pos);
		
		offset.set(avatarPos).shift(avatarPos.shift(lastAvatarPos, -1), interpolationShift);
		
		Render.offsetX = (float)-offset.x;
		Render.offsetY = (float)-offset.y;
	}
	
	public void draw(double interpolationShift){
		
//		if(Core.updatedToLong) {
//			for(int i = 0; i < Main.game().world.engine.timeIndex; i++) {
//				System.out.println(Main.game().world.engine.lastTimes[0][i] + "  " + Main.game().world.engine.lastTimes[1][i] + "  " + Main.game().world.engine.lastTimes[2][i] + "  " + Main.game().world.engine.lastTimes[3][i]);
//			}
//		}
//		Main.game().world.engine.timeIndex = 0;
		
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
		Render.drawSingleQuad(completeWindow, Color.WHITE, landscapeBuffer.getTex(), 0, 0, 1f/Main.game().SIZE_HALF.w, 1f/Main.game().SIZE_HALF.h, true, 0);
		
		if(Settings.getBoolean("RENDER_THINGS"))
			things.renderThings();

		GL11.glAlphaFunc(GL11.GL_ALWAYS, 1.0f);
//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		terrain.renderWater();

		//Outlines of living things
		if(Settings.getBoolean("RENDER_THINGS"))
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
		ParticleEffect.wind.set((Main.game().input2.getMousePos().x - Main.game().SIZE_HALF.w)*60f/Main.game().SIZE_HALF.w, 0);
		effects.forEach(Effect::render);

		//quests
		world.forEachQuest((aq) -> aq.render());
		
		if(world.isGameOver()) {

			Render.drawSingleQuad(completeWindow, Color.BLACK, null, 0, 0, 1f/Main.game().SIZE_HALF.w, 1f/Main.game().SIZE_HALF.h, false, 0);
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
