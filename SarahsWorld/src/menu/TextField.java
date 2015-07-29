package menu;

import java.awt.Font;

import render.Texture;
import util.Color;
import util.TrueTypeFont;

public class TextField extends Element {

	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 47), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.1f);
	
	public String text;
	
	public TextField(String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color background, Texture backgroundTex){
		super(relX1, relY1, relX2, relY2, x1, y1, x2, y2, background, backgroundTex);
		this.text = text;
	}
	
	public void render() {
		super.render();
		float xText = ((x1 + x2)/2)- (font.getWidth(text)/3);
		float yText = ((y1+y2)/2) - (font.getHeight()/2);
		fontColor.bind();
		font.drawString(xText, yText, text, 1, 1);
		Color.WHITE.bind();
	}

}