package menu.elements;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import extra.Res;
import extra.SarahsWorld;
import render.TexAtlas;
import render.Texture;
import util.Color;
import util.math.Vec;

public abstract class Button extends TextField {
	
	public static TexAtlas button = Res.getAtlas("button");

	public Color[] colors;
	public Texture[] textures;
	int nStates, visualState;

	public Button(SarahsWorld game, String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color1, Color color2, Color color3, Texture tex1, Texture tex2, Texture tex3) {
		super(game, text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, color1, tex1, true);
		this.colors = new Color[] {color1,color2,color3};
		this.textures = new Texture[] {tex1,tex2,tex3};
		nStates = 3;
	}

	public Button(SarahsWorld game, String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color1, Color color2, Texture tex1, Texture tex2) {
		super(game, text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, color1, tex1, true);
		this.colors = new Color[] {color1,color2};
		this.textures = new Texture[] {tex1,tex2};
		nStates = 2;
	}
	
	public Button(SarahsWorld game, String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Object... colsAndTexs) {
		super(game, text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, (Color)colsAndTexs[0], (Texture)colsAndTexs[1], true);
		nStates = colsAndTexs.length/2;
		this.colors = new Color[nStates];
		this.textures = new Texture[nStates];
		for(int i = 0; i < nStates; i++) {
			colors[i] = (Color)colsAndTexs[2*i];
			textures[i] = (Texture)colsAndTexs[2*i+1];
		}
	}
	
	@Override
	public boolean pressed(int button, Vec mousePos){
		if(contains(mousePos)){
			pressed(button);
			return true;
		}
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			released(button);
			return true;
		}
		return false;
	}

	public void pressed(int button) {}
	public abstract void released(int button);

	public void determineState() {
		if(contains(game.getInputData().getMousePos())){
			if(nStates >= 3 && game.getInputData().isMousePressed(GLFW_MOUSE_BUTTON_1)){
				visualState = 2;
			} else {
				visualState = 1;
			}
		} else {
			visualState = 0;
		}
	}
	
	public void render() {
		determineState();
		color = colors[visualState];
		tex = textures[visualState];
		super.render();
	}
}
