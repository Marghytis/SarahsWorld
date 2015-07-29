package menu;

import main.Main;
import main.Res;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import quest.ActiveQuest;
import render.TexFile;
import render.Texture;
import util.Color;
import util.Render;
import util.math.UsefulF;
import world.things.Thing;
import core.Window;

public class Dialog extends Element {
	public static TexFile tex = new TexFile("res/menu/Bar.png", 1, 3, 0, 0);
	public static Texture upTex = tex.tex(0, 0), downTex = tex.tex(0, 1), tex1 = tex.tex(0, 2);
	public static int fontHeightHalf = Res.menuFont.getHeight()/2;

	public String text1;
	public String[] answers;
	public double width1, y1;
	public double[] widths, ys;
	public ActiveQuest quest;
	public Thing other;
	
	public Dialog() {
		super(0, 0, 1, 1, 0, 0, 0, 0, null, null);
	}
	
	public void setup(ActiveQuest quest, Thing other, String text1, String[] answers){
		this.quest = quest;
		this.other = other;
		this.text1 = text1;
		this.answers = answers;
		this.widths = new double[answers.length];
		this.ys = new double[answers.length];
		for(int i = 0, y = Window.HEIGHT_HALF - (tex.pixelBox.size.yInt() + 30); i < answers.length; i++, y -= tex.pixelBox.size.yInt() + 30){
			widths[i] = (Res.menuFont.getWidth(answers[i])/1.8) + 100;
			ys[i] = y;
		}
		width1 = (Res.menuFont.getWidth(text1)/1.8) + 100;
		y1 = Window.HEIGHT*0.8;
	}
	
	public void render(){
		boolean side = other.pos.p.x > Main.world.avatar.pos.p.x;
		
		tex.bind();
		Render.quad(getX1(!side, width1), y1, getX2(!side, width1), y1 + tex.pixelBox.size.y, tex1);
		for(int i = 0; i < answers.length; i++){
			if(UsefulF.contains(Mouse.getX(), Mouse.getY(), 0, ys[i], widths[i], ys[i] + tex.pixelBox.size.y)){
				Render.quad(getX1(side, widths[i]), ys[i], getX2(side, widths[i]), ys[i] + tex.pixelBox.size.y, downTex);
			} else {
				Render.quad(getX1(side, widths[i]), ys[i], getX2(side, widths[i]), ys[i] + tex.pixelBox.size.y, upTex);
			}
		}
		TexFile.bindNone();
		Color.WHITE.bind();
		GL11.glLoadIdentity();
		Res.menuFont.drawString(getXS(!side, width1), (float)(y1 + (tex.pixelBox.size.y/2) - fontHeightHalf), text1, 1, 1);
		for(int i = 0; i < answers.length; i++){
			Res.menuFont.drawString(getXS(side, widths[i]), (float)(ys[i] + (tex.pixelBox.size.y/2) - fontHeightHalf), answers[i], 1, 1);
		}
	}
	
	public double getX1(boolean side, double width){
		if(side){
			return -tex.pixelBox.size.x + width;
		} else {
			return Window.WIDTH - width;
		}
	}
	
	public double getX2(boolean side, double width){
		if(side){
			return width;
		} else {
			return Window.WIDTH - width + tex.pixelBox.size.x;
		}
	}
	
	public float getXS(boolean side, double width){
		if(side){
			return 70;
		} else {
			return (float)(Window.WIDTH - width + 30);
		}
	}
}
