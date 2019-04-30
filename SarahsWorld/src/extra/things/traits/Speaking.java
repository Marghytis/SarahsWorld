package extra.things.traits;

import basis.entities.Trait;
import basis.entities.Entity;
import effects.Effect;
import extra.things.Thing;
import extra.things.ThingAttribute;
import main.Main;
import main.Res;
import menu.Dialog;
import menu.MenuManager.MenuType;
import quest.ActiveQuest;
import quest.Strings;
import render.Render;
import render.TexAtlas;
import render.Texture;
import render.VAO;
import util.Anim;
import util.Anim.AnimPart;
import util.Anim.Func;
import util.Anim.Value;
import util.Color;
import util.math.UsefulF;
import util.math.Vec;
import world.World;

public class Speaking extends Trait {
	
	@Override
	public SpeakingPlugin createAttribute(Entity thing) {
		return new SpeakingPlugin(thing);
	}
	
	public class SpeakingPlugin extends ThingAttribute {
		
		private ThoughtBubble tb;
		private boolean speaking;
		private String currentSpeech;
		private String[] answers;
		private ActiveQuest quest;

		public SpeakingPlugin(Entity thing) {
			super(thing);
			this.tb = new ThoughtBubble(this.thing);
		}

		public boolean speaking() {
			return speaking;
		}
		
		@Override
		public void update(double delta) {
			if(currentSpeech != null){
				if(thing.pos.minus(Main.world.avatar.pos).lengthSquare() < 90000 && !speaking){// && "".equals(t.currentSpeech)
					if(Main.menu.openActive.type == MenuType.DIALOG && ((Dialog)Main.menu.active(MenuType.DIALOG).getElement(0)).other == thing){
						Main.menu.setMenu(MenuType.DIALOG);
					} else {
						tb.popUp();
					}
				} else if(thing.pos.minus(Main.world.avatar.pos).lengthSquare() > 90000){
					if(tb.living) tb.goAway();
					if(Main.menu.openActive.type == MenuType.DIALOG && ((Dialog)Main.menu.active(MenuType.DIALOG).getElement(0)).other == thing){
						Main.menu.setMenu(MenuType.EMPTY);
						speaking = false;
					}
				}
			}
		}

		public void remove(){
			if(tb.living){
				tb.goAway();
			}
			if(Main.menu.openActive.type == MenuType.DIALOG && ((Dialog)Main.menu.active(MenuType.DIALOG).getElement(0)).other == thing){
				Main.menu.setMenu(MenuType.EMPTY);
				speaking = false;
			}
		}
		
		public void say(String what) {
			currentSpeech = what;
		}
		
		public void say(boolean thoughtBubble, ActiveQuest quest, String what, String[] answers){
			this.quest = quest;
			currentSpeech = what;
			this.answers = answers;

			if(!thoughtBubble && thing.pos.minus(Main.world.avatar.pos).lengthSquare() < 90000){
				speaking = true;
				String[] realAnswers = new String[answers.length];
				for(int i = 0; i < answers.length; i++){
					realAnswers[i] = Strings.get(answers[i], World.rand);
				}
				((Dialog)Main.menu.active(MenuType.DIALOG).getElement(0)).setup(quest, thing, Strings.get(what, World.rand), realAnswers);
				Main.menu.setMenu(MenuType.DIALOG);
			}
		}
	}

	public static Texture bubble1 = Res.getTex("speechBubble");
	public static TexAtlas bubble2 = Res.getAtlas("thoughtBubble");
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
		VAO quad = Render.quadInScreen(-1, -1, 1, 1);
		
		boolean pressed;
		double bubbleR;
		boolean living = false;
		
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
				Main.world.window.addEffect(this);
			}
		}
		
		public void goAway() {
			ani.dir = false;
		}

		public void render(){
			render(1f/Main.HALFSIZE.w, 1f/Main.HALFSIZE.h);
		}
		public void render(float scaleX, float scaleY) {
			Vec shift = pos.copy().shift(relPos).minus(speaker.pos);
			for(int i = 0, s = 1; i < 8; i += 2, s += 3){
				double r = rs[i/2].v*s;
				if(r >= 0){
					double x = speaker.pos.x + shift.x*positions[i] - Main.world.avatar.pos.x;
					double y = speaker.pos.y + shift.y*positions[i+1] + 60 - Main.world.avatar.pos.y;
					Render.drawSingleQuad(quad, Color.WHITE, bubble1, x, y, Render.scaleX, Render.scaleY, true, 0, r);
				}
			}
//			GL11.glEnd();
			double bR = rs[4].v*(pressed ? 0.16 : 0.2);
			bubbleR = bR;
			if(bR >= 0){
				Render.drawSingleQuad(quad, Color.WHITE, texs[tex], speaker.pos.x + shift.x - Main.world.avatar.pos.x, speaker.pos.y + shift.y+60 - Main.world.avatar.pos.y, Render.scaleX, Render.scaleY, true, 0, bR*texs[tex].w/2);
			}
		}
		public boolean living() {
			return living;
		}

		public boolean released(int button, Vec mousePos, Vec pathSincePress) {
			if(pressed && contains(mousePos)){
				pressed = false;
				goAway();
				speaker.speakPlug.speaking = true;
				String[] realAnswers = new String[speaker.speakPlug.answers.length];
				for(int i = 0; i < speaker.speakPlug.answers.length; i++){
					realAnswers[i] = Strings.get(speaker.speakPlug.answers[i], World.rand);
				}
				((Dialog)Main.menu.active(MenuType.DIALOG).getElement(0)).setup(speaker.speakPlug.quest, speaker, Strings.get(speaker.speakPlug.currentSpeech, World.rand), realAnswers);
				Main.menu.setMenu(MenuType.DIALOG);
				return true;
			}
			return false;
		}
		
		public boolean contains(Vec mousePos){
			Vec shift = pos.copy().shift(relPos).minus(speaker.pos);
			return UsefulF.contains(
					mousePos.x + Main.world.avatar.pos.x - Main.HALFSIZE.w, mousePos.y + Main.world.avatar.pos.y - Main.HALFSIZE.h,
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

		@Override
		public boolean charTyped(char ch) {
			return false;
		}
		
	}
}
