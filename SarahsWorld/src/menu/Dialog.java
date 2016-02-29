package menu;

import java.awt.Font;

import main.Main;
import main.Res;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import quest.ActiveQuest;
import render.TexAtlas;
import render.TexFile;
import render.Texture;
import things.Thing;
import util.Anim;
import util.Anim.AnimPart;
import util.Anim.Func;
import util.Anim.Value;
import util.Color;
import util.TrueTypeFont;
import util.TrueTypeFont2;
import util.math.Graph;
import util.math.UsefulF;
import util.math.Vec;
import core.Window;

public class Dialog extends Element {
	public static Texture answersTex = new Texture("res/menu/Answers.png", -0.5, -0.5);
	public static TexAtlas tex = new TexAtlas("res/menu/Bar.png", 1, 3, 0, 0);
	public static Texture upTex = tex.tex(0, 0), downTex = tex.tex(0, 1), tex1 = tex.tex(0, 2);
	public static TexAtlas tex2 = new TexAtlas("res/menu/Bar2.png", 3, 3, 0, 0);
	public static Texture upTex2_1 = tex2.tex(0, 0), upTex2_2 = tex2.tex(1, 0),upTex2_3 = tex2.tex(2, 0), downTex2_1 = tex2.tex(0, 1), downTex2_2 = tex2.tex(1, 1), downTex2_3 = tex2.tex(2, 1), tex12_1 = tex2.tex(2, 2), tex12_2 = tex2.tex(1, 2), tex12_3 = tex2.tex(0, 2);
	public static Texture connection = new Texture("res/menu/Connector.png", 0, 0);
	public static TrueTypeFont fontOld = Res.menuFont;
	public static TrueTypeFont2 font = new TrueTypeFont2(new Font("Times New Roman", 0, 40), true);
	public static int fontHeightHalf = Res.menuFont.getHeight()/2;
	public static double rDefault = 150, animationTime = 2;

	public ActiveQuest quest;
	public Thing other;
	public String[] text1;
	public String[] answers;
	
	public double width1[]; public Vec relPos, pos, vel = new Vec();
	public double[] widthsH, ys, lineHeight;
	public double rWobble = 0.8, bubbleHeight, bubbleWidth, line;
	public Anim ani;
	public Value drawAngle, bubbleHeightR, answersWidth;
	public Value[] answerWidths;
	
	public Dialog() {
		super(0, 0, 1, 1, 0, 0, 0, 0, null, null);
	}
	
	public void setup(ActiveQuest quest, Thing other, String text1, String[] answers){
		this.quest = quest;
		this.other = other;
		this.text1 = text1.split("\\|");
		this.answers = answers;
		this.widthsH = new double[answers.length];
		this.ys = new double[answers.length];
		int height = font.getHeight(), d = (answersTex.h - (answers.length*height))/(answers.length+1);
		line = height + 20;
		for(int i = 0, y = (Window.HEIGHT_HALF/2) + (answersTex.h/2) - (height + d); i < answers.length; i++, y -= height + d){
			widthsH[i] = ((font.getWidth(answers[i])) + 100)/2;
			ys[i] = y;
		}
		width1 = new double[this.text1.length];
		bubbleHeight = 40;
		for(int i = 0; i < this.text1.length; i++){
			width1[i] = (font.getWidth(this.text1[i]));
			if(width1[i] > bubbleWidth) bubbleWidth = width1[i];
			bubbleHeight += line;
		}
		bubbleWidth += 40;
		relPos = new Vec(rDefault, rDefault);
		pos = other.pos.copy();
		
		//Animation
		drawAngle = new Value();
		bubbleHeightR = new Value();
		answersWidth = new Value();
		answerWidths = new Value[answers.length];
		for(int i = 0; i < answers.length; i++) answerWidths[i] = new Value();
		
		Func f = (t) -> {
			if(t > 0) return Math.sin(Math.PI*t)*rWobble + t;
			else return -1;
		};
		AnimPart[] parts = new AnimPart[answers.length + 3];
		parts[0] = new AnimPart(drawAngle, (t) -> Math.min(1 - t, 1), 0, 0.5);
		parts[1] = new AnimPart(bubbleHeightR, f, 0.5, 0.5);
		parts[2] = new AnimPart(answersWidth, f, 0.5, 0.5);
		for(int i = 0; i < answers.length; i++){
			parts[i+3] = new AnimPart(answerWidths[i], (t) -> Math.min(t, 1), 0.2, 1.0);
		}
		
		ani = new Anim(parts);
	}
	
	public void update(double delta){
		
		Vec shift = other.pos.minus(pos);
		double speakerMovement = shift.lengthSquare();
		if(speakerMovement > 0.1){
			vel.shift(shift.setLength(speakerMovement*delta/10));
			vel.scale(0.9);
			pos.shift(vel);
		}
	}
	
	Vec graph = new Vec();
	Color textColor1 = new Color(0.9f, 0.9f, 0.9f), textColor2 = new Color(0.5f, 0.5f, 0.5f);
	Texture corner = new Texture("res/menu/SpeechBubbleCorner.png", 0, 0);
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslated(Window.WIDTH_HALF - Main.world.avatar.pos.x, Window.HEIGHT_HALF - Main.world.avatar.pos.y, 0);
		
		//Speech bubble
		Vec shift = pos.copy().shift(relPos).minus(other.pos);
		if(bubbleHeightR.v > 0){
			corner.file.bind();
			double x = other.pos.x + shift.x*1;
			double y = other.pos.y + shift.y*1 + 60;
			render(x - (bubbleWidth/2), y, x + (bubbleWidth/2), y + bubbleHeightR.v*bubbleHeight, corner);
		}
		if(bubbleHeightR.v > 0.9){
			TexFile.bindNone();
			double y = bubbleHeight/2 + (text1.length/2*line);
			for(int i = 0; i < text1.length; i++){
				font.drawString((int)(other.pos.x + shift.x - (width1[i]/2)), (float)(other.pos.y + shift.y + y - fontHeightHalf + 60), text1[i], Color.WHITE, 1);
				y -= line;
			}
		}
		
		//Connection to the speech bubble
		connection.file.bind();
		
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		double rT = connection.w/2, maxAngle = -Math.PI*0.5, startAngle = maxAngle*drawAngle.v;
		for(double angle = startAngle, texY = drawAngle.v; angle >= maxAngle; angle -= Math.PI/100, texY += 0.02){
			Graph.unitCircle2.xy(graph, angle);
			double xM = other.pos.x + shift.x*graph.x;
			double yM = other.pos.y + shift.y*graph.y + 60;
			double dx = rT*Math.cos(angle);
			double dy = rT*Math.sin(angle);

			GL11.glTexCoord2d(0, texY);
			GL11.glVertex2d(xM - dx, yM - dy);
			GL11.glTexCoord2d(1, texY);
			GL11.glVertex2d(xM + dx, yM + dy);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		//possible answers
		
		if(answers.length == 0) return;
		
		if(answersWidth.v > 0){
			answersTex.file.bind();
			answersTex.fill(
					(answersTex.pixelCoords[0]*answersWidth.v) + Window.WIDTH_HALF,
					(answersTex.pixelCoords[1]*answersWidth.v) + Window.HEIGHT_HALF/2,
					(answersTex.pixelCoords[2]*answersWidth.v) + Window.WIDTH_HALF,
					(answersTex.pixelCoords[3]*answersWidth.v) + Window.HEIGHT_HALF/2, false);
		}

		TexFile.bindNone();

		for(int i = 0; i < answers.length; i++){
			if(answerWidths[i].v > 0){
				if(UsefulF.contains(Mouse.getX(), Mouse.getY(), Window.WIDTH_HALF + answersTex.pixelCoords[0], ys[i], Window.WIDTH_HALF + answersTex.pixelCoords[2], ys[i] + Res.menuFont.getHeight())){
					Color.WHITE.bind(0.5f);
					Res.light2.file.bind();
					Res.light.fill(Window.WIDTH_HALF - widthsH[i], ys[i]-10, Window.WIDTH_HALF + widthsH[i], ys[i] + Res.menuFont.getHeight() + 10, false);
					TexFile.bindNone();
				}
				String string = answers[i].substring(0, (int)(answers[i].length()*answerWidths[i].v));
				font.drawString((int)(Window.WIDTH_HALF - widthsH[i]+50), (float)ys[i], string, Color.WHITE, 1);
			}
		}
		
//		tex2.bind();
//		
//		for(int i = 0; i < answers.length; i++){
//			double wh = widthsH[i]*boxWidths[i].v;
//			if(wh > 0){
//				if(UsefulF.contains(Mouse.getX(), Mouse.getY(), Window.WIDTH_HALF - widthsH[i], ys[i], Window.WIDTH_HALF + widthsH[i], ys[i] + tex.pixelBox.size.y)){
//					render(Window.WIDTH_HALF - wh, ys[i], Window.WIDTH_HALF + wh, downTex2_1, downTex2_2, downTex2_3);
//				} else {
//					render(Window.WIDTH_HALF - wh, ys[i], Window.WIDTH_HALF + wh, upTex2_1, upTex2_2, upTex2_3);
//				}
//			}
//		}
//		TexFile.bindNone();
//		Color.WHITE.bind();
//		for(int i = 0; i < answers.length; i++){
//			if(boxWidths[i].v >= 0.9){
//				Res.menuFont.drawString((int)(Window.WIDTH_HALF - widthsH[i] + 50), (float)(ys[i] + (tex.pixelBox.size.y/2) - fontHeightHalf), answers[i], 1, 1);
//			}
//		}
	}
	
	public boolean released(int button, Vec mousePos, Vec pathSincePress){
		for(int i = 0; i < answers.length; i++){
			if(UsefulF.contains(mousePos.xInt(), mousePos.yInt(), Window.WIDTH_HALF + answersTex.pixelCoords[0], ys[i], Window.WIDTH_HALF + answersTex.pixelCoords[2], ys[i] + Res.menuFont.getHeight())){
				other.currentSpeech = "";
				quest.onAnswer(i);
				Main.menu.setLast();
			}
		}
		return false;
	}
	
	public void render(double x1, double y1, double x2, double y2, Texture tex){
		GL11.glBegin(GL11.GL_QUAD_STRIP);
			GL11.glTexCoord2d(0, 1); GL11.glVertex2d(x1, y1);
			GL11.glTexCoord2d(0, 0); GL11.glVertex2d(x1, y1+tex.h);
			GL11.glTexCoord2d(0.99, 1); GL11.glVertex2d(x1+tex.w, y1);
			GL11.glTexCoord2d(0.99, 0); GL11.glVertex2d(x1+tex.w, y1+tex.h);
			GL11.glTexCoord2d(0.99, 1); GL11.glVertex2d(x2-tex.w, y1);
			GL11.glTexCoord2d(0.99, 0); GL11.glVertex2d(x2-tex.w, y1+tex.h);
			GL11.glTexCoord2d(0, 1); GL11.glVertex2d(x2, y1);
			GL11.glTexCoord2d(0, 0); GL11.glVertex2d(x2, y1+tex.h);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUAD_STRIP);
			GL11.glTexCoord2d(0, 0.01); GL11.glVertex2d(x1, y1+tex.h);
			GL11.glTexCoord2d(0, 0.01); GL11.glVertex2d(x1, y2-tex.h);
			GL11.glTexCoord2d(0.99, 0.01); GL11.glVertex2d(x1+tex.w, y1+tex.h);
			GL11.glTexCoord2d(0.99, 0.01); GL11.glVertex2d(x1+tex.w, y2-tex.h);
			GL11.glTexCoord2d(0.99, 0.01); GL11.glVertex2d(x2-tex.w, y1+tex.h);
			GL11.glTexCoord2d(0.99, 0.01); GL11.glVertex2d(x2-tex.w, y2-tex.h);
			GL11.glTexCoord2d(0, 0.01); GL11.glVertex2d(x2, y1+tex.h);
			GL11.glTexCoord2d(0, 0.01); GL11.glVertex2d(x2, y2-tex.h);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUAD_STRIP);
			GL11.glTexCoord2d(0, 0); GL11.glVertex2d(x1, y2-tex.h);
			GL11.glTexCoord2d(0, 1); GL11.glVertex2d(x1, y2);
			GL11.glTexCoord2d(0.99, 0); GL11.glVertex2d(x1+tex.w, y2-tex.h);
			GL11.glTexCoord2d(0.99, 1); GL11.glVertex2d(x1+tex.w, y2);
			GL11.glTexCoord2d(0.99, 0); GL11.glVertex2d(x2-tex.w, y2-tex.h);
			GL11.glTexCoord2d(0.99, 1); GL11.glVertex2d(x2-tex.w, y2);
			GL11.glTexCoord2d(0, 0); GL11.glVertex2d(x2, y2-tex.h);
			GL11.glTexCoord2d(0, 1); GL11.glVertex2d(x2, y2);
		GL11.glEnd();
	}
}
