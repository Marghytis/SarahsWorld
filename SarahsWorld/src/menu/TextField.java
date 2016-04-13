package menu;

import java.awt.Font;

import core.Window;
import render.Texture;
import util.Color;
import util.TrueTypeFont;

public class TextField extends Element {

	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 40), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.1f);
	
	public String text;
	public int xCenter, yCenter;
	public boolean center;
	
	public TextField(String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color background, Texture backgroundTex, boolean center){
		super(relX1, relY1, relX2, relY2, x1, y1, x2, y2, background, backgroundTex);
		this.text = text;
		this.center = center;
		xCenter = (this.x1 + this.x2)/2;
		yCenter = (this.y1 + this.y2)/2;
	}
	
	public void render() {
		super.render();
		float xText = center? xCenter- (font.getWidth(text)/3) : this.x1+20;
		float yText = yCenter + (font.getHeight(text)/2);
		font.drawString(xText - Window.WIDTH_HALF, yText - Window.HEIGHT_HALF, text, fontColor, 1, 1);
	}

}