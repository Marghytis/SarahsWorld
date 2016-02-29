package menu;

import org.lwjgl.input.Keyboard;

import render.TexFile;
import render.Texture;
import util.Color;
import util.Render;
import util.math.Vec;

public class TextInput extends TextField {
	
	public Color selectedColor;
	public boolean selected;

	public TextInput(String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2,
			int y2, Color background, Color selected, Texture backgroundTex) {
		super(text, relX1, relY1, relX2, relY2, x1, y1, x2, y2, background, backgroundTex);
		this.selectedColor = selected;
	}
	
	public void render(){
		if(selected)
			selectedColor.bind();
		else
			color.bind();

		TexFile.bindNone();
		Render.quad(x1, y1, x2, y2);
		
		float xText = ((x1 + x2)/2)- (font.getWidth(text)/3);
		float yText = ((y1+y2)/2) - (font.getHeight()/2);
		fontColor.bind();
		font.drawString(xText, yText, text, 1, 1);
		Color.WHITE.bind();
		
	}
	
	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		if(contains(mousePos)){
			if(!selected) text = "";
			selected = !selected;
			return true;
		}
		return false;
	}
	
	public boolean keyPressed(int key){
		if(selected){
			if(key == Keyboard.KEY_BACK && text.length() > 0){
				text = text.substring(0, text.length()-1);
			} else {
				char ch = Keyboard.getEventCharacter();
				if(ch != 0){
					text += ch;
				}
			}
			return true;
		} else return false;
	}

}
