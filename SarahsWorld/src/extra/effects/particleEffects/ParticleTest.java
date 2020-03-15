package extra.effects.particleEffects;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;

import basis.effects.Effect;
import basis.effects.particleEffects.ParticleEffect;
import core.Core;
import core.Game;
import core.Listener;
import core.Renderer;
import core.Speaker;
import core.Updater;
import core.Window;
import extra.Main;
import extra.Res;
import input.PollData;
import menu.Settings;
import render.Render;
import util.math.Vec;
import world.render.EffectManager;

public class ParticleTest extends Core implements Game, Updater, Renderer, Listener {
	
	static Window staticWindow; 
	
	public static class ParticleTestMain extends Main {
		public static void main(String[] args){
			Window.showSplashScreen("res/Snail.png");

			createHardwareBindings("Particle Test");
	
			ParticleTest test = new ParticleTest(window, speaker, 60);
			
			window.init();
			speaker.init();
			
			test.start(true);
//			test.initGame();
			
	//		Core core = new Core("Particle Tester", new Vec(1500, 1000));
	//		core = new Core("res/Snail.png");
	//		core.init(new Window("Particle Tester", false), clearColor);
			
	//		Updater.updaters.addCoins(test);
	//		Listener.listeners.addCoins(test);
	//		Renderer.renderers.addCoins(test);
	//		
	//		core.coreLoop();
		}
	}
	
	private PollData input;
	
	private EffectManager effects;
	
	@Override
	public void initGame() {
		Res.init();
		input = getWindow().getPollData();
		
		setUpdaters(this);
		getWindow().setRenderers(this);
		getWindow().setListeners(this);
		

		//effects.addEffect(new Meteor(new Vec(1, 0), 400));
		effects.addEffect(new Snow(new Vec(-SIZE_HALF.w, SIZE_HALF.h), 2000, 10));
	}
	
	public void requestTermination() {
		getWindow().requestClose();
	}
	
//	public List<ParticleEffect> effects = new ArrayList<>();
	
	public ParticleTest(Window window, Speaker speaker, int fps) {
		super(window, speaker, fps);
//		RainEffect rain = new RainEffect(new Vec(1000, 500), 50, 100);
//		Fog fog = new Fog(0, 0, 300, 2, 100);
//		effects.add(rain);
//		effects.add(fog);
//		effects.add(new Snow(new Vec(-core.SIZE_HALF.w, core.SIZE_HALF.h+20), core.SIZE.w, 1));
		effects = new EffectManager();
//		effects.add(new BasicMagicEffect(100) {
//			public void update(double delta) {
//				setPos(Listener.getMousePos(core.getWindow().getHandle()).copy().shift(-core.SIZE_HALF.w, -core.SIZE_HALF.h));
//				super.update(delta);
//			}
//		});
	}
	
	public boolean update(double delta) {
		ParticleEffect.wind.set((input.getMousePos().x - SIZE_HALF.w)*60f/SIZE_HALF.w, 0);
		effects.update(delta);
		return false;
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		switch(button){
		case 0:
			effects.addEffect(new DeathDust(mousePos.minus(SIZE_HALF.w, SIZE_HALF.h)));
			break;
		case 1:
			effects.addEffect(new Hearts(mousePos.minus(SIZE_HALF.w, SIZE_HALF.h)));
			break;
		case 2:
			effects.addEffect(new BloodSplash(mousePos.minus(SIZE_HALF.w, SIZE_HALF.h)));
			break;
		}
		return false;
	}

	public boolean keyPressed(int key) {
		
		Vec mousePos = input.getMousePos().minus(SIZE_HALF.w, SIZE_HALF.h);
		switch(key){
		case GLFW_KEY_1:
			effects.addEffect(new DeathDust(mousePos));
			break;
		case GLFW_KEY_2:
			effects.addEffect(new Hearts(mousePos));
			break;
		case GLFW_KEY_3:
			effects.addEffect(new BloodSplash(mousePos));
			break;
		case GLFW_KEY_4:
			effects.addEffect(new BerryEat(mousePos));
			break;
		case GLFW_KEY_5:
			effects.addEffect(new RainbowSpit(mousePos, 1));
			break;
		case GLFW_KEY_6:
			effects.addEffect(new FireEffect(mousePos));
			break;
		case GLFW_KEY_7:
			effects.addEffect(new RainEffect(mousePos, 100, 20));
			break;
		case GLFW_KEY_8:
			effects.addEffect(new ChristmasBalls(mousePos));
			break;
		case GLFW_KEY_9:
			effects.addEffect(new Snow(mousePos, 2000, 1));
			break;
		case GLFW_KEY_0:
			effects.addEffect(new BasicMagicDissapperance(mousePos));
			break;
		}
		return false;
	}

	@Override
	public boolean keyReleased(int key) {
		return false;
	}

	public String debugName() {
		return "Particle Test";
	}

	@Override
	public boolean charTyped(char ch) {
		return false;
	}
	
	public void updateTransform() {

		Render.scaleX = (float)(Settings.getDouble("ZOOM")/SIZE_HALF.w);
		Render.scaleY = (float)(Settings.getDouble("ZOOM")/SIZE_HALF.h);
		
		
		
		Render.offsetX = (float)0;
		Render.offsetY = (float)0;
	}

	@Override
	public void draw(double beta) {
		updateTransform();
		effects.forEach(Effect::render);
	}
	
}
