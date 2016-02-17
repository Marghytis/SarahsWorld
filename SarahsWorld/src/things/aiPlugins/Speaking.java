package things.aiPlugins;

import main.Main;
import menu.Dialog;
import menu.Menu.Menus;

import org.lwjgl.opengl.GL11;

import quest.ActiveQuest;
import quest.Strings;
import render.TexAtlas;
import render.TexFile;
import render.Texture;
import things.AiPlugin;
import things.Thing;
import util.Anim;
import util.Anim.AnimPart;
import util.Anim.Func;
import util.Anim.Value;
import util.Color;
import util.math.UsefulF;
import util.math.Vec;
import world.World;
import core.Window;
import effects.Effect;

public class Speaking extends AiPlugin {
	
	public void setup(Thing t){
		t.tb = new ThoughtBubble(t);
	}
	
	public void say(Thing t, boolean thoughtBubble, ActiveQuest quest, String what, String[] answers){
		t.quest = quest;
		t.currentSpeech = what;
		t.answers = answers;
		
		if(!thoughtBubble && t.pos.minus(Main.world.avatar.pos).lengthSquare() < 90000){
			t.speaking = true;
			String[] realAnswers = new String[answers.length];
			for(int i = 0; i < answers.length; i++){
				realAnswers[i] = Strings.get(answers[i], World.rand);
			}
			((Dialog)Menus.DIALOG.elements[0]).setup(quest, t, Strings.get(what, World.rand), realAnswers);
			Menus.DIALOG.ani = ((Dialog)Menus.DIALOG.elements[0]).ani;
			Main.menu.setMenu(Menus.DIALOG);
		}
	}

	public void update(Thing t, double delta) {
		if(t.pos.minus(Main.world.avatar.pos).lengthSquare() < 90000 && !t.speaking){// && "".equals(t.currentSpeech)
			if(Main.menu.open == Menus.DIALOG && ((Dialog)Menus.DIALOG.elements[0]).other == t){
				Main.menu.setMenu(Menus.DIALOG);
			} else {
				t.tb.popUp();
			}
		} else if(t.pos.minus(Main.world.avatar.pos).lengthSquare() > 90000){
			if(t.tb.living) t.tb.goAway();
			if(Main.menu.open == Menus.DIALOG && ((Dialog)Menus.DIALOG.elements[0]).other == t){
				Main.menu.setMenu(Menus.EMPTY);
				t.speaking = false;
			}
		}
	}

	public void remove(Thing t){
		if(t.tb.living){
			t.tb.goAway();
		}
		if(Main.menu.open == Menus.DIALOG && ((Dialog)Menus.DIALOG.elements[0]).other == t){
			Main.menu.setMenu(Menus.EMPTY);
			t.speaking = false;
		}
	}
	public static Texture bubble1 = new Texture("res/particles/Bubble.png", 0, 0);
	public static TexAtlas bubble2 = new TexAtlas("res/particles/ThoughtBubble.png", 1, 3, -0.5, -0.5);
	public static Texture[] texs = {bubble2.tex(0, 0), bubble2.tex(0, 1), bubble2.tex(0, 2)};
	public static double animationTime = 1.8;
	public static double[] positions = {0.1, 0.01, 0.25, 0.0625, 0.45, 0.2025, 0.65, 0.44225};
	public class ThoughtBubble implements Effect {
		public int tex;
		public Thing speaker;
		public Anim ani;
		public Vec relPos, pos, vel = new Vec();
		double rWobble = 0.8;
		Value[] rs = {new Value(), new Value(), new Value(), new Value(), new Value()};
		
		public ThoughtBubble(Thing speaker){
			this.speaker = speaker;
			relPos = new Vec(50, 75);
			pos = speaker.pos.copy();
			tex = World.rand.nextInt(3);
			Func f = (t) -> {
				if(t > 0) return Math.sin(Math.PI*t)*rWobble + t;
				else return -1;
			};
			ani = new Anim(
					new AnimPart(rs[0], f, 0.2, 1.0),
					new AnimPart(rs[1], f, 0.2, 1.0),
					new AnimPart(rs[2], f, 0.2, 1.0),
					new AnimPart(rs[3], f, 0.2, 1.0),
					new AnimPart(rs[4], f, 0.2, 1.0));
			ani.time = -1;
		}

		public void update(double delta) {
			if(ani.time < 0){
				living = false;
			} else {
				ani.update(delta);
				living = true;
				
				Vec shift = speaker.pos.minus(pos);
				double speakerMovement = shift.lengthSquare();
				if(speakerMovement > 0.1){
					vel.shift(shift.setLength(speakerMovement*delta/10));
					vel.scale(0.9);
					pos.shift(vel);
				}
			}
		}

		public void popUp() {
			ani.dir = true;
			if(!living){
				living = true;
				ani.time = 0;
				Main.world.window.toAdd.add(this);
			}
		}
		
		public void goAway() {
			ani.dir = false;
		}

		public void render() {
			Vec shift = pos.copy().shift(relPos).minus(speaker.pos);
			bubble1.file.bind();
			Color.WHITE.bind();
			GL11.glBegin(GL11.GL_QUADS);
			for(int i = 0, s = 1; i < 8; i += 2, s += 3){
				double r = rs[i/2].v*s;
				if(r >= 0){
					double x = speaker.pos.x + shift.x*positions[i];
					double y = speaker.pos.y + shift.y*positions[i+1] + 60;
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(x - r, y - r);
					GL11.glTexCoord2d(1, 1);
					GL11.glVertex2d(x + r, y - r);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(x + r, y + r);
					GL11.glTexCoord2d(0, 0);
					GL11.glVertex2d(x - r, y + r);
				}
			}
			GL11.glEnd();
			double bR = rs[4].v*(pressed ? 0.16 : 0.2);
			if(bR >= 0){
				bubbleR = bR;
				bubble2.file.bind();
				texs[tex].resetMod();
				texs[tex].scale(bubbleR, bubbleR);
				texs[tex].translate(speaker.pos.x + shift.x, speaker.pos.y + shift.y+60);
				texs[tex].drawMod(false);
			}
			TexFile.bindNone();
		}
		double bubbleR;
		
		boolean living = false;
		public boolean living() {
			return living;
		}

		public boolean released(int button, Vec mousePos, Vec pathSincePress) {
			if(pressed && contains(mousePos)){
				pressed = false;
				goAway();
				speaker.speaking = true;
				String[] realAnswers = new String[speaker.answers.length];
				for(int i = 0; i < speaker.answers.length; i++){
					realAnswers[i] = Strings.get(speaker.answers[i], World.rand);
				}
				((Dialog)Menus.DIALOG.elements[0]).setup(speaker.quest, speaker, Strings.get(speaker.currentSpeech, World.rand), realAnswers);
				Menus.DIALOG.ani = ((Dialog)Menus.DIALOG.elements[0]).ani;
				Main.menu.setMenu(Menus.DIALOG);
				return true;
			}
			return false;
		}
		
		boolean pressed;
		
		public boolean contains(Vec mousePos){
			Vec shift = pos.copy().shift(relPos).minus(speaker.pos);
			return UsefulF.contains(
					mousePos.x + Main.world.avatar.pos.x - Window.WIDTH_HALF, mousePos.y + Main.world.avatar.pos.y - Window.HEIGHT_HALF,
					bubble2.pixelCoords[0]*bubbleR + speaker.pos.x + shift.x,
					bubble2.pixelCoords[1]*bubbleR + speaker.pos.y + shift.y+60,
					bubble2.pixelCoords[2]*bubbleR + speaker.pos.x + shift.x,
					bubble2.pixelCoords[3]*bubbleR + speaker.pos.y + shift.y+60);
		}

		public boolean pressed(int button, Vec mousePos) {
			if(contains(mousePos)){
				pressed = true;
				return true;
			}
			return false;
		}

		public boolean keyPressed(int key) {
			return false;
		}

		public boolean keyReleased(int key) {
			return false;
		}
		
	}
	
//	public static class SpeechBubble implements Effect {
//		public static TexFile speechBubble = new TexFile("res/menu/SpeechBubble.png", -0.5, -0.5);
//		public static TexFile connector = new TexFile("res/menu/SpeechBubble2.png", -0.5, -0.5);
//		
//		public Thing speaker;
//		public boolean living;
//		
//		public SpeechBubble(Thing speaker){
//			this.speaker = speaker;
//		}
//		
//		public void goAway() {
//			living = false;
//			speaker.speak.speaking = false;
//		}
//
//		public void popUp(){
//			living = true;
//			Main.world.window.toAdd.add(this);
//		}
//
//		public boolean pressed(int button, Vec mousePos) {
//			return false;
//		}
//
//		public boolean released(int button, Vec mousePos, Vec pathSincePress) {
//			return false;
//		}
//
//		public boolean keyPressed(int key) {
//			return false;
//		}
//
//		public void update(double delta) {
//			
//		}
//
//		public void render() {
//			GL11.glPushMatrix();
//			GL11.glLoadIdentity();
//			Color.WHITE.bind();
//			speechBubble1.bind();
//			Rect bubble = speechBubble1.pixelBox.copy().scaleSizeKeepCenter(1, 0.65).shift(Window.WIDTH_HALF, Window.HEIGHT*0.85);
//			speechBubble1.fill(bubble, 0);
//			speechBubble2.bind();
//			GL11.glBegin(GL11.GL_QUAD_STRIP);
//			double speakerY = speaker.pos.y - Main.world.avatar.pos.y + Window.HEIGHT_HALF + 60,
//					speakerX = speaker.pos.x - Main.world.avatar.pos.x;
//			double yTotal = bubble.pos.y - speakerY;
//			for(double y = speakerY; y <= bubble.pos.y + 9; y += 5){
//				double x = Math.sqrt((y - speakerY)/yTotal)*speakerX;
//				GL11.glTexCoord2d(0, 1 - ((y - speakerY)/yTotal));
//				GL11.glVertex2d(Window.WIDTH_HALF - x + speakerX - (speechBubble2.size.x/2), y);
//				GL11.glTexCoord2d(1, 1 - ((y - speakerY)/yTotal));
//				GL11.glVertex2d(Window.WIDTH_HALF - x + speakerX + (speechBubble2.size.x/2), y);
//			}
//			GL11.glEnd();
//			TexFile.bindNone();
//			GL11.glPopMatrix();
//		}
//
//		public boolean living() {
//			return living;
//		}
//		
//	}
//	public static class Connection implements Effect {
//
//		public static double duration = 1, height = 200;
//		
//		public Vec pos1, pos2, conn = new Vec(), middle = new Vec();
//		public Color color1, color2;
//		public double time = 0;
//		public int state = 0;
//		public Function f;
//		public double dist;
//		public boolean broken;
//		
//		public Connection(Vec pos1, Vec pos2, Color color1, Color color2){
//			this.pos1 = pos1;
//			this.pos2 = pos2;
//			this.color1 = color1;
//			this.color2 = color2;
//		}
//		
//		public void setFuncs(){
//			if(pos2.x < pos1.x){
//				Vec tmp1 = pos2;
//				pos2 = pos1;
//				pos1 = tmp1;
//				Color tmp2 = color2;
//				color2 = color1;
//				color1 = tmp2;
//			}
//			conn.set(pos2.x - pos1.x, pos2.y - pos1.y);
//			dist = conn.length();
//			middle.set(0.5*conn.x + pos1.x, 0.5*conn.y + pos1.y);
//			f = Function.quartic(dist, height);
//		}
//
//		public void update(double delta) {
//			setFuncs();
//			switch(state){
//			case 0:
//				time += delta;
//				if(time >= Connection.duration)
//					state = 1;
//				break;
//			}
//		}
//
//		public void render() {
//			GL11.glPushMatrix();
//			GL11.glTranslated(middle.x, middle.y+50, 0);
//			GL11.glRotated((conn.angle()/(2*Math.PI))*360, 0, 0, 1);
//			
//			setFuncs();
//			
//			color1.bind();
//			
//			double step = (dist)/(int)(dist/1);
//			
//			GL11.glBegin(GL11.GL_LINE_STRIP);
//				for(double x = -dist/2; x <= 0; x += step){
//					double y = f.f(x);
//					if((y/height) <= (time/Connection.duration)){
//						double col2 = x/dist+0.5, col1 = 1 - col2;
//						GL11.glColor3d((col1 * color1.r) + (col2 * color2.r), (col1 * color1.g) + (col2 * color2.g), (col1 * color1.b) + (col2 * color2.b));
//						GL11.glVertex2d(x, y);
//					}
//				}
//			GL11.glEnd();
//			GL11.glBegin(GL11.GL_LINE_STRIP);
//				for(double x = dist/2; x >= 0; x -= step){
//					double y = f.f(x);
//					if((y/height) <= (time/Connection.duration)){
//						double col2 = x/dist+0.5, col1 = 1 - col2;
//						GL11.glColor3d((col1 * color1.r) + (col2 * color2.r), (col1 * color1.g) + (col2 * color2.g), (col1 * color1.b) + (col2 * color2.b));
//						GL11.glVertex2d(x, y);
//					}
//				}
//			GL11.glEnd();
//			switch(state){
//			case 0:
//				Res.light1.bind();
//				color1.bind();
//				double leftX = (dist/2)*(time/Connection.duration), leftY = f.f(leftX - 35);
//				GL11.glBegin(GL11.GL_QUADS);
//					GL11.glTexCoord2d(0, 1);
//					GL11.glVertex2d(leftX-30, leftY-30);
//					GL11.glTexCoord2d(1, 1);
//					GL11.glVertex2d(leftX+30, leftY-30);
//					GL11.glTexCoord2d(1, 0);
//					GL11.glVertex2d(leftX+30, leftY+30);
//					GL11.glTexCoord2d(0, 0);
//					GL11.glVertex2d(leftX-30, leftY+30);
//				GL11.glEnd();
//				color2.bind();
//				double rightX = dist - ((dist/2)*(time/Connection.duration)), rightY = f.f(rightX - dist/2 - 35) + 200;
//				GL11.glBegin(GL11.GL_QUADS);
//				GL11.glTexCoord2d(0, 1);
//				GL11.glVertex2d(rightX-30, rightY-30);
//				GL11.glTexCoord2d(1, 1);
//				GL11.glVertex2d(rightX+30, rightY-30);
//				GL11.glTexCoord2d(1, 0);
//				GL11.glVertex2d(rightX+30, rightY+30);
//				GL11.glTexCoord2d(0, 0);
//				GL11.glVertex2d(rightX-30, rightY+30);
//				GL11.glEnd();
//				TexFile.bindNone();
//				break;
//			}
//			GL11.glPopMatrix();
//		}
//
//		public boolean living() {
//			return !broken;
//		}
//	}
}
