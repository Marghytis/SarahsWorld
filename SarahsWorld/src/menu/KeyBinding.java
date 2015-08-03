package menu;

import menu.Settings.Key;

import org.lwjgl.input.Keyboard;

import render.Texture;
import util.Color;
import util.math.Vec;

public class KeyBinding extends FlexibleTextField {

	Key key;
	boolean selected;
	
	public KeyBinding(Key key, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color, Texture tex) {
		super(() -> Keyboard.getKeyName(key.key), relX1, relY1, relX2, relY2, x1, y1, x2, y2, color, tex);
		this.key = key;
	}
	
	public void update(double delta){
		if(selected){
			text = "";
		} else {
			super.update(delta);
		}
	}
	
	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			System.out.println("selected: " + key);
			selected = true;
		} else {
			selected = false;
		}
		return false;
	}
	
	public boolean keyPressed(int key){
		if(selected){
			System.out.println("Changed " + this.key + " to: " + key);
			this.key.key = key;
			selected = false;
			return true;
		}
		return false;
	}

}
