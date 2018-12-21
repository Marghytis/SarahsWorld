package menu;

import java.awt.Font;

import main.Main;
import render.Texture;
import util.*;

public class TextField extends Element {

	public static TrueTypeFont font = new TrueTypeFont(new Font("Times New Roman", 0, 40), true);
	public static Color fontColor = new Color(0.9f, 0.8f, 0.1f);
	
	public String text;
	public int xCenter, yCenter;
	public boolean center;
	
	public TextField(Main game, String text, double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color background, Texture backgroundTex, boolean center){
		super(game, relX1, relY1, relX2, relY2, x1, y1, x2, y2, background, backgroundTex);
		this.text = text;
		this.center = center;
		xCenter = (this.x1 + this.x2)/2;
		yCenter = (this.y1 + this.y2)/2;
	}
	
	public void render() {
		super.render();
		if(text != null && visible()){
			float xText = center? xCenter : this.x1+20;
			float yText = yCenter - (font.getHeight("I")/2);
			font.drawString(xText - Main.HALFSIZE.w, yText - Main.HALFSIZE.h, text, fontColor, 1, 1, 1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h, center ? TrueTypeFont.ALIGN_CENTER : TrueTypeFont.ALIGN_LEFT);
		}
	}

}