package effects.particles;

import static org.lwjgl.glfw.GLFW.*;

import java.util.*;

import core.*;
import util.math.Vec;

public class ParticleTest implements Listener, Renderer, Updater{
	static Core core;
	public static void main(String[] args){
		
//		Core core = new Core("Particle Tester", new Vec(1500, 1000));
		core = new Core("res/Snail.png");
		core.init(new Window("Particle Tester", false), clearColor);
		ParticleTest test = new ParticleTest();
		
		Updater.updaters.add(test);
		Listener.listeners.add(test);
		Renderer.renderers.add(test);
		
		core.coreLoop();
	}
	
	public List<ParticleEffect> effects = new ArrayList<>();
	
	public ParticleTest(){
//		RainEffect rain = new RainEffect(new Vec(1000, 500), 50, 100);
//		Fog fog = new Fog(0, 0, 300, 2, 100);
//		effects.add(rain);
//		effects.add(fog);
//		effects.add(new Snow(new Vec(-core.SIZE_HALF.w, core.SIZE_HALF.h+20), core.SIZE.w, 1));
		effects.add(new BasicMagicEffect(100) {
			public void update(double delta) {
				setPos(Listener.getMousePos(core.getWindow().getHandle()).copy().shift(-core.SIZE_HALF.w, -core.SIZE_HALF.h));
				super.update(delta);
			}
		});
	}
	
	public boolean update(double delta) {
		float d = (float) delta;
		ParticleEffect.wind.set((Listener.getMousePos(core.getWindow().getHandle()).x - core.SIZE_HALF.w)*60f/core.SIZE_HALF.w, 0);
		effects.forEach((e) -> e.update(d));
		return false;
	}

	public void draw() {
		effects.forEach((e) -> e.render(1f/core.SIZE_HALF.w, 1f/core.SIZE_HALF.h));
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		switch(button){
		case 0:
			effects.add(new DeathDust(mousePos.minus(core.SIZE_HALF.w, core.SIZE_HALF.h)));
			break;
		case 1:
			effects.add(new Hearts(mousePos.minus(core.SIZE_HALF.w, core.SIZE_HALF.h)));
			break;
		case 2:
			effects.add(new BloodSplash(mousePos.minus(core.SIZE_HALF.w, core.SIZE_HALF.h)));
			break;
		}
		return false;
	}

	public boolean keyPressed(int key) {
		Vec mousePos = Listener.getMousePos(core.getWindow().getHandle()).minus(core.SIZE_HALF.w, core.SIZE_HALF.h);
		switch(key){
		case GLFW_KEY_1:
			effects.add(new DeathDust(mousePos));
			break;
		case GLFW_KEY_2:
			effects.add(new Hearts(mousePos));
			break;
		case GLFW_KEY_3:
			effects.add(new BloodSplash(mousePos));
			break;
		case GLFW_KEY_4:
			effects.add(new BerryEat(mousePos));
			break;
		case GLFW_KEY_5:
			effects.add(new RainbowSpit(mousePos, 1));
			break;
		case GLFW_KEY_6:
			effects.add(new FireEffect(mousePos));
			break;
		case GLFW_KEY_7:
			effects.add(new RainEffect(mousePos, 100, 20));
			break;
		case GLFW_KEY_8:
			effects.add(new ChristmasBalls(mousePos));
			break;
		case GLFW_KEY_9:
			effects.add(new Snow(mousePos, 2000, 1));
			break;
		case GLFW_KEY_0:
			effects.add(new BasicMagicDissapperance(mousePos));
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
	
}
