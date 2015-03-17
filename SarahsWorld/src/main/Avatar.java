package main;

import java.io.IOException;

import menu.Menu;

import org.lwjgl.input.Keyboard;

import util.math.Vec;
import world.objects.Thing;
import core.Listener;
import core.Window;

public class Avatar implements Listener{
	public Thing sarah;
	public Vec pos;
	
	public Avatar(Thing sarah){
		this.sarah = sarah;
		this.pos = sarah.pos.p;
	}

	public boolean pressed(int button, Vec mousePos) {
		return false;
	}

	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		Vec worldPos = mousePos.minus(new Vec(Window.WIDTH/2, Window.HEIGHT/2)).shift(pos);
		Thing[] livingsClickedOn = Main.world.livingsAt(worldPos);
		
		switch(button){
		case 0://ATTACK
			if(livingsClickedOn.length > 0) sarah.attack.attack(sarah.inv.getSelectedItem(), livingsClickedOn);
			break;
		case 1://USE
			sarah.inv.useSelectedItem(worldPos, livingsClickedOn);
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
		case Keyboard.KEY_SPACE: if(sarah.ground.g) sarah.ground.jump(); break;
		case Keyboard.KEY_E: if(sarah.riding.isRiding) sarah.riding.dismount(); break;
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
}
