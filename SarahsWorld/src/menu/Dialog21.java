package menu;

import main.Main;
import main.Res;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import quest.ActiveQuest;
import render.TexFile;
import render.Texture;
import util.Anim;
import util.Anim.AnimPart;
import util.Anim.Func;
import util.Anim.Value;
import util.Color;
import util.Render;
import util.math.Graph;
import util.math.UsefulF;
import util.math.Vec;
import world.things.Thing;
import core.Window;

public class Dialog21 extends Element {
	public static TexFile answersTex = new TexFile("res/menu/Answers.png", -0.5, -0.5);
	public static TexFile tex = new TexFile("res/menu/Bar.png", 0, 3, 0, 0);
	public static Texture upTex = tex.tex(0, 0), downTex = tex.tex(0, 1), tex1 = tex.tex(0, 2);
	public static TexFile tex2 = new TexFile("res/menu/Bar2.png", 3, 3, 0, 0);
	public static Texture upTex2_1 = tex2.tex(0, 0), upTex2_2 = tex2.tex(1, 0),upTex2_3 = tex2.tex(2, 0), downTex2_1 = tex2.tex(0, 1), downTex2_2 = tex2.tex(1, 1), downTex2_3 = tex2.tex(2, 1), tex12_1 = tex2.tex(0, 2), tex12_2 = tex2.tex(1, 2), tex12_3 = tex2.tex(2, 2);
	public static TexFile connection = new TexFile("res/menu/Connector.png");
	public static int fontHeightHalf = Res.menuFont.getHeight()/2;
	public static double rDefault = 150, animationTime = 2;

	public ActiveQuest quest;
	public Thing other;
	public String text1;
	public String[] answers;
	
	public double width1; public Vec relPos, pos, vel = new Vec();
	public double[] widthsH, ys, lineHeight;
	public double rWobble = 0.8;
	public Anim ani;
	public Value drawAngle, bubbleHeight, answersWidth;
	public Value[] answerWidths;
	
	public Dialog21() {
		super(0, 0, 1, 1, 0, 0, 0, 0, null, null);
	}
	
	public void setup(ActiveQuest quest, Thing other, String text1, String[] answers){
		this.quest = quest;
		this.other = other;
		this.text1 = text1;
		this.answers = answers;
		this.widthsH = new double[answers.length];
		this.ys = new double[answers.length];
		int height = Res.menuFont.getHeight(), d = (answersTex.pixelBox.size.yInt() - (answers.length*height))/(answers.length+1);
		for(int i = 0, y = (Window.HEIGHT_HALF/2) + (answersTex.pixelBox.size.yInt()/2) - (height + d); i < answers.length; i++, y -= height + d){
			widthsH[i] = ((Res.menuFont.getWidth(answers[i])/1.8) + 100)/2;
			ys[i] = y;
		}
		width1 = (Res.menuFont.getWidth(text1)/1.8) + 100;
		relPos = new Vec(rDefault, rDefault);
		pos = other.pos.p.copy();
		
		//Animation
		drawAngle = new Value();
		bubbleHeight = new Value();
		answersWidth = new Value();
		answerWidths = new Value[answers.length];
		for(int i = 0; i < answers.length; i++) answerWidths[i] = new Value();
		
		Func f = (t) -> {
			if(t > 0) return Math.sin(Math.PI*t)*rWobble + t;
			else return -1;
		};
		AnimPart[] parts = new AnimPart[answers.length + 3];
		parts[0] = new AnimPart(drawAngle, (t) -> Math.min(1 - t, 1), 0, 0.5);
		parts[1] = new AnimPart(bubbleHeight, f, 0.5, 0.5);
		parts[2] = new AnimPart(answersWidth, f, 0.5, 0.5);
		for(int i = 0; i < answers.length; i++){
			parts[i+3] = new AnimPart(answerWidths[i], (t) -> Math.min(t, 1), 0.2, 1.0);
		}
		
		ani = new Anim(parts);
	}
	
	public void update(double delta){
		
		Vec shift = other.pos.p.minus(pos);
		double speakerMovement = shift.lengthSquare();
		if(speakerMovement > 0.1){
			vel.shift(shift.setLength(speakerMovement*delta/10));
			vel.scale(0.9);
			pos.shift(vel);
		}
	}
	
	Vec graph = new Vec();
	Color textColor1 = new Color(0.8f, 0.8f, 0.8f), textColor2 = new Color(0.5f, 0.5f, 0.5f);
	public void render(){
		GL11.glPushMatrix();
		GL11.glTranslated(Window.WIDTH_HALF - Main.world.avatar.pos.p.x, Window.HEIGHT_HALF - Main.world.avatar.pos.p.y, 0);
		
		//Speech bubble
		Vec shift = pos.copy().shift(relPos).minus(other.pos.p);
		if(bubbleHeight.v > 0){
			tex2.bind();
			double x = other.pos.p.x + shift.x*1;
			double y = other.pos.p.y + shift.y*1 + 60;
			render(x - (width1/2), y, x + (width1/2), y + bubbleHeight.v*tex2.pixelBox.size.y, tex12_1, tex12_2, tex12_3);
		}
		if(bubbleHeight.v > 0.9){
			TexFile.bindNone();
			Color.WHITE.bind();
			Res.menuFont.drawString((int)(other.pos.p.x + shift.x - width1/2 + 50), (float)(other.pos.p.y + shift.y + (tex.pixelBox.size.y/2) - fontHeightHalf + 60), text1, 1, 1);
		}
		
		//Connection to the speech bubble
		connection.bind();
		
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		double rT = connection.pixelBox.size.x/2, maxAngle = -Math.PI*0.5, startAngle = maxAngle*drawAngle.v;
		for(double angle = startAngle, texY = drawAngle.v; angle >= maxAngle; angle -= Math.PI/100, texY += 0.02){
			Graph.unitCircle2.xy(graph, angle);
			double xM = other.pos.p.x + shift.x*graph.x;
			double yM = other.pos.p.y + shift.y*graph.y + 60;
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
			answersTex.bind();
			answersTex.fill(answersTex.pixelBox.copy().shift(Window.WIDTH_HALF, Window.HEIGHT_HALF/2).scaleSizeKeepCenter(answersWidth.v, 1), 0);
		}

		TexFile.bindNone();

		textColor1.bind();
		for(int i = 0; i < answers.length; i++){
			if(answerWidths[i].v > 0){
				if(UsefulF.contains(Mouse.getX(), Mouse.getY(), Window.WIDTH_HALF - (answersTex.pixelBox.size.x/2), ys[i], Window.WIDTH_HALF + (answersTex.pixelBox.size.x/2), ys[i] + Res.menuFont.getHeight())){
					Color.WHITE.bind(0.5f);
					Res.light2.bind();
					Render.quad(Window.WIDTH_HALF - widthsH[i], ys[i]-10, Window.WIDTH_HALF + widthsH[i], ys[i] + Res.menuFont.getHeight() + 10, Res.light.tex());
					TexFile.bindNone();
					textColor1.bind();
				}
				String string = answers[i].substring(0, (int)(answers[i].length()*answerWidths[i].v));
				Res.menuFont.drawString((int)(Window.WIDTH_HALF - widthsH[i] + 50), (float)ys[i], string, 1, 1);
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
			if(UsefulF.contains(mousePos.xInt(), mousePos.yInt(), Window.WIDTH_HALF - (answersTex.pixelBox.size.x/2), ys[i], Window.WIDTH_HALF + (answersTex.pixelBox.size.x/2), ys[i] + Res.menuFont.getHeight())){
				other.speak.currentSpeech = "";
				quest.onAnswer(i);
				Main.menu.setLast();
			}
		}
		return false;
	}
	
	public void render(double x1, double y1, double x2, Texture tex1, Texture tex2, Texture tex3){
		double w2 = tex1.file.pixelBox.size.x;
		Render.quad(x1, y1, x1 + w2, y1 + tex.pixelBox.size.y, tex1);
		Render.quad(x1 + w2, y1, x2 - w2, y1 + tex.pixelBox.size.y, tex2);
		Render.quad(x2 - w2, y1, x2, y1 + tex.pixelBox.size.y, tex3);
	}
	
	public void render(double x1, double y1, double x2, double y2, Texture tex1, Texture tex2, Texture tex3){
		double w2 = tex1.file.pixelBox.size.x;
		Render.quad(x1, y1, x1 + w2, y2, tex1);
		Render.quad(x1 + w2, y1, x2 - w2, y2, tex2);
		Render.quad(x2 - w2, y1, x2, y2, tex3);
	}
}
