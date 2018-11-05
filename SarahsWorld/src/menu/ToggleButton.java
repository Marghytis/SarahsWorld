package menu;

import core.Listener;
import main.Main;
import render.Texture;
import util.Color;

public abstract class ToggleButton extends Button {
	
	static int NORMAL = 0, RELEASED = 1, HOVER = 2, PUSHED = 3;
	boolean hooked = false;
	boolean value = false;
	
	String normalText;
	String activeText;

	public ToggleButton(String text1, String text2, double relX1, double relY1, double relX2, double relY2, int x1,
			int y1, int x2, int y2, Color color1, Color color2, Color color3, Color color4, Texture tex1, Texture tex2,
			Texture tex3, Texture tex4) {
		super(text1, relX1, relY1, relX2, relY2, x1, y1, x2, y2, color1, tex1, color2, tex2, color3, tex3, color4, tex4);
		this.normalText = text1;
		this.activeText = text2;
	}
	
	public void pressed(int button) {
		visualState = PUSHED;
		hooked = !hooked;
		if(hooked) {
			toggle(true);
		}
	}

	public void released(int button) {
		visualState = HOVER;
		if(!hooked) {
			toggle(false);
		}
	}
	private void toggle(boolean boo) {
		value = boo;
		toggled(boo);
	}
	
	public abstract boolean getRealValue();
	public abstract void toggled(boolean on);
	
	public void determineState() {
		if(getRealValue() != value) {
			determineStateClassically();//to detect hover and leave events
			if(value == false) {
				visualState = PUSHED;
				hooked = true;
			} else {
				visualState = PUSHED;
				hooked = false;
			}
			value = !value;
		}
		determineStateClassically();
	}
	
	public void determineStateClassically() {
		if(		 (visualState == NORMAL || visualState == RELEASED) && contains(Listener.getMousePos(Main.WINDOW))){
			visualState = HOVER;
		} else if((visualState == HOVER || visualState == PUSHED) && !contains(Listener.getMousePos(Main.WINDOW))){
			if(hooked) {
				visualState = RELEASED;
			} else {
				visualState = NORMAL;
				toggle(false);
			}
		}
		
		if(value) {
			text = activeText;
		} else {
			text = normalText;
		}
	}

}
