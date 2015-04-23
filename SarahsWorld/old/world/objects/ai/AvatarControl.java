package world.objects.ai;

import java.io.IOException;

import main.Main;
import main.S;
import menu.Menu;

import org.lwjgl.input.Keyboard;

import util.math.Vec;
import world.objects.Thing;
import core.Listener;
import core.Window;

public  class AvatarControl extends AiPlugin implements Listener{

	public static final float aWalking = 1000;
	public static final float aSprint = 2500;
	public static final float aDebug = 10000;
	
	public AvatarControl(Thing t){
		super(t);
	}
	
	private double walkingDir;
	private boolean lastChangeKey = true;
	
	public boolean action(double delta) {
		if(t.ground.g){
			float a = aWalking;
			if(lastChangeKey){
				walkingDir = 0;
			}			
			if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				if(walkingDir < 2) walkingDir++;
				lastChangeKey = true;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				if(walkingDir > -2) walkingDir--;
				lastChangeKey = true;
			}
			if(walkingDir != 0){
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
					walkingDir *= 2.5;
					if(Keyboard.isKeyDown(Keyboard.KEY_TAB)){
						walkingDir *= 4;
					}
				}
			}
			t.ground.sprint = walkingDir > 1 || walkingDir < -1;
			t.ground.setAni(walkingDir*a);
			t.ground.acc += walkingDir*a;
		}
		return false;
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	private static final int touchSensitivityX = 90, touchSensitivityY = 60;
	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		Vec worldPos = mousePos.minus(new Vec(Window.WIDTH/2, Window.HEIGHT/2)).shift(t.pos.p);
		Thing[] livingsClickedOn = Main.world.window.livingsAt(worldPos);
		
		if(pathSincePress.y > pathSincePress.x && pathSincePress.y > -pathSincePress.x && pathSincePress.y > touchSensitivityY && livingsClickedOn.length == 0){
			t.ground.jump();
		} else {
			if(pathSincePress.x > touchSensitivityX){
				if(walkingDir < 2) walkingDir++;
				lastChangeKey = false;
			} else if(pathSincePress.x < -touchSensitivityX){
				if(walkingDir > -2) walkingDir--;
				lastChangeKey = false;
			}
		}
		
		switch(button){
		case 0://ATTACK
			if(livingsClickedOn.length > 0) t.attack.attack(t.inv.getSelectedItem(), livingsClickedOn);
			break;
		case 1://USE
			t.inv.useSelectedItem(worldPos, livingsClickedOn);
			break;
		}
//		int hitThingLoc = -3;
//		Thing[] hitThing = new Thing[1];
//		for(List<Thing> list : Main.world.objects) for(Thing t : list){
//			if(t.ani.behind > hitThingLoc && t.life != null && !t.equals(this) && worldPos.containedBy(t.ani.box.pos.x + t.pos.p.x, t.ani.box.pos.y + t.pos.p.y, t.ani.box.size.x, t.ani.box.size.y)){
//				hitThing[0] = t;
//				hitThingLoc = t.ani.behind;
//			}
//		}
//		if(hitThing[0] != null){
//			switch(button){
//			case 0: sarah.attack.attack(hitThing[0]); break;
//			case 1: 
//				switch(hitThing[0].type){
//				case COW : sarah.riding.mount(hitThing[0]); break;
//				default: break;
//				}
//			}
//			
//		}
		return false;
	}

	public boolean keyPressed(int key) {
		
		switch(key){
		case Keyboard.KEY_SPACE: if(t.ground.g) t.ground.jump(); break;
		case Keyboard.KEY_E: if(t.riding.isRiding) t.riding.dismount(); break;
		case Keyboard.KEY_S: if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
			try { S.saveWorld(Main.world); }
			catch (IOException e) {e.printStackTrace();}
			break;
		case Keyboard.KEY_Q:
			if(Main.menu.open != Menu.INVENTORY){
				Main.menu.open = Menu.INVENTORY;
			} else {
				Main.menu.open = Menu.EMPTY;
			}
			break;
		case Keyboard.KEY_ESCAPE:
			Main.menu.open = Main.menu.open != Menu.MAIN ? Menu.MAIN : Menu.EMPTY;
			break;
		case Keyboard.KEY_F1:
			Main.menu.open = Main.menu.open != Menu.DEBUG ? Menu.DEBUG : Menu.EMPTY;
			break;
		}
		return false;
	}

	public String save() {return "";}
	public void load(String data){}

}