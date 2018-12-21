package menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;

import main.Main;
import render.Texture;
import util.Color;
import util.math.Vec;

public class TextInput extends TextField {
	
	public Color color1, selectedColor;
	public boolean selected;

	public TextInput(Main game, String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2,
			int y2, Color background, Color selected, Texture backgroundTex) {
		this(game, text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, background, selected, backgroundTex, true);
	}

		public TextInput(Main game, String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2,
				int y2, Color background, Color selected, Texture backgroundTex, boolean center) {
		super(game, text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, background, backgroundTex, center);
		this.color1 = background;
		this.selectedColor = selected;
	}
	
	public void render(){
		if(selected)
			color = selectedColor;
		else
			color = color1;

		super.render();
//		Render.quad(x1, y1, x2, y2);
//		
//		float xText = ((x1 + x2)/2)- (font.getWidth(text)/3);
//		float yText = ((y1+y2)/2) - (font.getHeight()/2);
//		fontColor.bind();
//		font.drawString(xText, yText, text, 1, 1);
//		Color.WHITE.bind();
		
	}
	
	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			if(!selected) text = "";
			selected = !selected;
			return true;
		}
		return false;
	}
	
	public boolean keyReleased(int key) {
		return selected;
	}
	
	public boolean keyPressed(int key){
		if(selected){
			if(key == GLFW_KEY_BACKSPACE && text.length() > 0){
				text = text.substring(0, text.length()-1);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean charTyped(char ch){
		if(selected){
			if(ch != 0){
				text += ch;
			}
			return true;
		}
		return false;
	}
}
