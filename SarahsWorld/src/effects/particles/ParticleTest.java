package effects.particles;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import util.math.Vec;
import core.Core;
import core.Listener;
import core.Renderer;
import core.Updater;

public class ParticleTest implements Listener, Renderer, Updater{

	public static void main(String[] args){
		
		Core core = new Core("Particle Tester", new Vec(1500, 1000));
		ParticleTest test = new ParticleTest();
		
		Updater.updaters.add(test);
		Listener.listeners.add(test);
		Renderer.renderers.add(test);
		
		core.coreLoop();
	}
	
	public List<ParticleEffect> effects = new ArrayList<>();
	
	public ParticleTest(){
//		RainEffect rain = new RainEffect(new Vec(1000, 500), 50, 100);
//		effects.add(rain);
	}

	public boolean update(double delta) {
		float d = (float) delta;
		effects.forEach((e) -> e.update(d));
		return false;
	}

	public void draw() {
		effects.forEach((e) -> e.render());
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		switch(button){
		case 0:
			effects.add(new DeathDust(mousePos));
			break;
		case 1:
			effects.add(new Hearts(mousePos));
			break;
		case 2:
			effects.add(new BloodSplash(mousePos));
			break;
		}
		return false;
	}

	public boolean keyPressed(int key) {
		Vec mousePos = Listener.getMousePos();
		switch(key){
		case Keyboard.KEY_1:
			effects.add(new DeathDust(mousePos));
			break;
		case Keyboard.KEY_2:
			effects.add(new Hearts(mousePos));
			break;
		case Keyboard.KEY_3:
			effects.add(new BloodSplash(mousePos));
			break;
		case Keyboard.KEY_4:
			effects.add(new BerryEat(mousePos));
			break;
		case Keyboard.KEY_5:
			effects.add(new RainbowSpit(mousePos, null));
			break;
		case Keyboard.KEY_6:
			break;
		case Keyboard.KEY_7:
			break;
		case Keyboard.KEY_8:
			break;
		case Keyboard.KEY_9:
			break;
		case Keyboard.KEY_0:
			break;
		case Keyboard.KEY_NUMPAD0:
			effects.add(new FireEffect(mousePos));
			break;
		case Keyboard.KEY_NUMPAD1:
			effects.add(new RainEffect(mousePos, 100, 20));
			break;
		case Keyboard.KEY_NUMPAD2:
			break;
		case Keyboard.KEY_NUMPAD3:
			break;
		case Keyboard.KEY_NUMPAD4:
			break;
		case Keyboard.KEY_NUMPAD5:
			break;
		case Keyboard.KEY_NUMPAD6:
			break;
		case Keyboard.KEY_NUMPAD7:
			break;
		case Keyboard.KEY_NUMPAD8:
			break;
		case Keyboard.KEY_NUMPAD9:
			break;
		}
		return false;
	}

	@Override
	public boolean keyReleased(int key) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
