package things.aiPlugins;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import core.Listener;
import core.Window;
import effects.Effect;
import main.Main;
import menu.Menu.Menus;
import menu.Settings;
import menu.Settings.Key;
import things.AiPlugin;
import things.Thing;
import util.math.Vec;
import world.World;

public  class AvatarControl extends AiPlugin implements Listener{

	public boolean action(Thing t, double delta) {
		t.maxWalkingSpeed = t.accWalking/5;
		
		double a = 0;
		if(t.where.g)
			if(t.where.water < 0.5) a = t.accWalking;
			else a = t.accSwimming;
		else if(t.where.water > 0)
			a = t.accSwimming;
		else a = t.accFlying;
		
		int walkingDir = 0;
		if(Keyboard.isKeyDown(Key.RIGHT.key)){
			walkingDir++;
		}
		if(Keyboard.isKeyDown(Key.LEFT.key)){
			walkingDir--;
		}
		if(walkingDir != 0 && Keyboard.isKeyDown(Key.SPRINT.key)){
			walkingDir *= 2;
			t.maxWalkingSpeed *= 2;
			if(Keyboard.isKeyDown(Key.SUPERSPRINT.key)){
				walkingDir *= 4;
				t.maxWalkingSpeed *= 4;
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
					walkingDir *= 4;
					t.maxWalkingSpeed *= 4;
				}
			}
		}
		t.type.movement.setAni(t, walkingDir);
		t.walkingForce = walkingDir*a;
		if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			t.force.shift(0, 10000);
		}
		
		//Scroll through inventory
		int scroll = Mouse.getDWheel();
		t.selectedItem += -scroll/120;
		if(t.selectedItem < 0){
			t.selectedItem %= t.itemStacks.length;
			t.selectedItem += t.itemStacks.length;
		}
		t.selectedItem %= t.itemStacks.length;
		return true;
	}

	public boolean pressed(int button, Vec mousePos) {
		for(Effect e : Main.world.window.effects){
			e.pressed(button, mousePos);
		}
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		Vec worldPos = mousePos.minus(Window.WIDTH/2, Window.HEIGHT/2).shift(Main.world.avatar.pos);
		Thing[] livingsClickedOn = Main.world.window.livingsAt(worldPos);
		
		switch(button){
		case 0://ATTACK
			if(livingsClickedOn.length > 0 && Main.world.avatar.where.water == 0){
				Main.world.avatar.type.attack.attack(Main.world.avatar, null, Main.world.avatar.type.inv.getSelectedItem(Main.world.avatar), "", livingsClickedOn);
			}
			break;
		case 1://USE
			Thing[] objectsClickedOn = Main.world.window.objectsAt(worldPos);
			Main.world.avatar.type.inv.useSelectedItem(Main.world.avatar, worldPos, objectsClickedOn);
			break;
		case 2:
			objectsClickedOn = Main.world.window.objectsAt(worldPos);
			for(Thing t : objectsClickedOn){
				t.selected = !t.selected;
				t.switchedSelected = true;
			}
			break;
		}
		
		for(Effect e : Main.world.window.effects){
			e.released(button, mousePos, pathSincePress);
		}
//		int hitThingLoc = -3;
//		Thing[] hitThing = new Thing[1];
//		for(List<Thing> list : Main.world.objects) for(Thing t : list){
//			if(t.ani.behind > hitThingLoc && t.life != null && !t.equals(this) && worldPos.containedBy(t.ani.box.pos.x + t.pos.x, t.ani.box.pos.y + t.pos.y, t.ani.box.size.x, t.ani.box.size.y)){
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
		case JUMP: if(Main.world.avatar.where.g) Main.world.avatar.type.movement.jump(Main.world.avatar); break;
		case DISMOUNT: if(Main.world.avatar.isRiding) Main.world.avatar.type.ride.dismount(Main.world.avatar); break;
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
		case FASTER:
			Settings.timeScale *= 1.1;
			break;
		case SLOWER:
			Settings.timeScale *= 0.9;
			break;
		case FREEZE:
			Settings.FREEZE = !Settings.FREEZE;
			break;
		case JUMPDOWN:
			World.world.avatar.pos.y -= 200;
			World.world.avatar.where.g = false;
		default:
		}
		//No, don't add if-clauses here!! Add the keys legally!!
		return false;
	}
	public boolean keyReleased(int key) {
		return false;
	}

}