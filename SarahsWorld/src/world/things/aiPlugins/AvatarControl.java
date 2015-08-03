package world.things.aiPlugins;

import main.Main;
import menu.Menu.Menus;
import menu.Settings;
import menu.Settings.Key;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import util.math.Vec;
import world.things.AiPlugin;
import world.things.Thing;
import core.Listener;
import core.Window;
import effects.Effect;

public  class AvatarControl extends AiPlugin implements Listener{

	public static final float aWalking = 1000;
	public static final float aSwimming = 500;
	
	public AvatarControl(Thing t){
		super(t);
	}
	
	private double walkingDir;
	
	public boolean action(double delta) {
		if(t.ground.g){
			float a = aWalking;
			walkingDir = 0;
			if(Keyboard.isKeyDown(Key.RIGHT.key)){
				walkingDir++;
			}
			if(Keyboard.isKeyDown(Key.LEFT.key)){
				walkingDir--;
			}
			if(walkingDir != 0){
				if(Keyboard.isKeyDown(Key.SPRINT.key)){
					walkingDir *= 2.5;
					if(Keyboard.isKeyDown(Key.SUPERSPRINT.key)){
						walkingDir *= 4;
					}
				}
			}
			t.ground.sprint = walkingDir > 1 || walkingDir < -1;
			t.ground.setAni(walkingDir*a);
			t.ground.acc += walkingDir*a;
		} else if(t.friction.swimming){
			float a = aSwimming;
			walkingDir = 0;
			if(Keyboard.isKeyDown(Key.RIGHT.key)){
				walkingDir++;
			}
			if(Keyboard.isKeyDown(Key.LEFT.key)){
				walkingDir--;
			}
			t.acc.a.shift(walkingDir*a, 0);
			//TODO set swimming animation
		}
		int scroll = Mouse.getDWheel();
		t.inv.selectedItem += -scroll/120;
		if(t.inv.selectedItem < 0){
			t.inv.selectedItem %= t.inv.stacks.length;
			t.inv.selectedItem += t.inv.stacks.length;
		}
		t.inv.selectedItem %= t.inv.stacks.length;
		return false;
	}

	public boolean pressed(int button, Vec mousePos) {
		for(Effect e : Main.world.window.effects){
			e.pressed(button, mousePos);
		}
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		Vec worldPos = mousePos.minus(Window.WIDTH/2, Window.HEIGHT/2).shift(t.pos.p);
		Thing[] livingsClickedOn = Main.world.window.livingsAt(worldPos);
		
		switch(button){
		case 0://ATTACK
			if(livingsClickedOn.length > 0 && !t.friction.swimming){
				t.attack.attack(t.inv.getSelectedItem(), livingsClickedOn);
			}
			break;
		case 1://USE
			Thing[] objectsClickedOn = Main.world.window.objectsAt(worldPos);
			t.inv.useSelectedItem(worldPos, objectsClickedOn);
			break;
		}
		
		for(Effect e : Main.world.window.effects){
			e.released(button, mousePos, pathSincePress);
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
		
		Key bind = Key.getBinding(key);
		
		switch(bind){
		case JUMP: if(t.ground.g) t.ground.jump(); break;
		case DISMOUNT: if(t.riding.isRiding) t.riding.dismount(); break;
		case INVENTORY:
			if(Main.menu.open.stay) break;
			if(Main.menu.open != Menus.INVENTORY){
				Main.menu.setMenu(Menus.INVENTORY);
			} else {
				Main.menu.setMenu(Menus.EMPTY);
			}
			break;
		case MAIN_MENU:
			if(Main.menu.open != Menus.MAIN){
				Main.menu.setMenu(Menus.MAIN);
			} else {
				Main.menu.setMenu(Menus.EMPTY);
			}
			break;
		case DEBUG:
			if(Main.menu.open.stay) break;
			if(Main.menu.open != Menus.DEBUG){
				Settings.SHOW_BOUNDING_BOX = true;
				Main.menu.setMenu(Menus.DEBUG);
			} else {
				Settings.SHOW_BOUNDING_BOX = false;
				Main.menu.setLast();
			}
			break;
		case STOP_GRAPH:
			Settings.STOP_GRAPH = !Settings.STOP_GRAPH;
			break;
		default:
		}
		return false;
	}

	public String save() {return "";}
	public void load(String data){}

}