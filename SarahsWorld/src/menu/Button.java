package menu;

import static org.lwjgl.glfw.GLFW.*;

import core.Listener;
import main.*;
import render.TexAtlas;
import render.Texture;
import util.Color;
import util.math.Vec;

public abstract class Button extends TextField {
	
	public static TexAtlas button = Res.button;

	public Color c1, c2, c3;
	public Texture t1, t2, t3;
	boolean threeStates = true;
	
	public Button(String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color1, Color color2, Color color3, Texture tex1, Texture tex2, Texture tex3) {
		super(text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, color1, tex1, true);
		this.c1 = color1;
		this.c2 = color2;
		this.c3 = color3;
		this.t1 = tex1;
		this.t2 = tex2;
		this.t3 = tex3;
	}
	
	public Button(String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color1, Color color2, Texture tex1, Texture tex2) {
		this(text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, color1, color2, null, tex1, tex2, null);
		threeStates = false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			released(button);
			return true;
		}
		return false;
	}
	
	public abstract void released(int button);

	public void render() {
		if(contains(Listener.getMousePos(Main.WINDOW))){
			if(threeStates && glfwGetMouseButton(Main.WINDOW, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS){
				color = c3;
				tex = t3;
			} else {
				color = c2;
				tex = t2;
			}
		} else {
			color = c1;
			tex = t1;
		}
		super.render();
	}

}
