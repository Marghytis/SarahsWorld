package menu;

import main.Main;
import main.Res;
import menu.Element;

import org.lwjgl.opengl.GL11;

import quest.ActiveQuest;
import render.TexFile;
import render.Texture;
import util.Color;
import util.math.Rect;
import util.math.Vec;
import world.things.Thing;
import core.Listener;
import core.Window;

public class Answers extends Element {
	
	public static int fontHeightHalf = Res.menuFont.getHeight()/2;
	
	Texture upTex;
	Texture downTex;
	public String[] texts;
	public String[] onReleases;
	public Rect[] boxes;
	public double[] widths;
	Thing other;
	ActiveQuest quest;

	public Answers(Texture upTex, Texture downTex) {
		super(upTex, upTex.file.pixelBox, new Vec());
		this.upTex = upTex;
		this.downTex = downTex;
	}
	
	@SuppressWarnings("deprecation")
	public void draw(){
		if(texts != null) for(int i = 0; i < texts.length; i++){
			if(boxes[i].contains(Listener.getMousePos())){
				downTex.fill(boxes[i]);
			} else {
				upTex.fill(boxes[i]);
			}
			TexFile.bindNone();
			Color.WHITE.bind();
			GL11.glLoadIdentity();
			Res.menuFont.drawString(10, (float)(boxes[i].pos.y + (boxes[i].size.y/2) - fontHeightHalf), texts[i], 1, 1);
		}
	}
	
	public void setButtons(ActiveQuest aq, String[] texts, String[] onRelease, Thing other){
		this.other = other;
		this.texts = texts;
		this.onReleases = onRelease;
		this.quest = aq;
		this.widths = new double[texts.length];
		this.boxes = new Rect[texts.length];
		if(texts != null) for(int i = 0, y = Window.HEIGHT_HALF + (tex.file.pixelBox.size.yInt() + 30)*(texts.length/2); i < texts.length; i++, y -= tex.file.pixelBox.size.yInt() + 30){
			widths[i] = (Res.menuFont.getWidth(texts[i])/1.8) + 40 - (tex.file.pixelBox.size.x/2);
			boxes[i] = tex.file.pixelBox.copy().shift(widths[i], y);
		}
	}
	
	public void onRelease(Vec mousePos){
		if(texts != null) for(int i = 0; i < texts.length; i++){
			if(boxes[i].contains(mousePos)){
				Main.menu.setLast();
				quest.lastAnswer = onReleases[i];
				break;
			}
		}
	}
}
