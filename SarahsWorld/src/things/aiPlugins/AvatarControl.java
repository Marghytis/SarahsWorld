package things.aiPlugins;

import core.Listener;
import main.Main;
import menu.Menu.Menus;
import menu.Settings;
import menu.Settings.Key;
import things.AiPlugin;
import things.Thing;
import things.ThingType;
import util.math.Vec;
import world.World;

public  class AvatarControl extends AiPlugin implements Listener {

	public boolean action(Thing t, double delta) {
		
		boolean riding = t.isRiding;
		boolean debugging = Settings.getBoolean("DEBUGGING");

		boolean walk_right = Listener.isKeyDown(Main.WINDOW, Key.RIGHT.key);
		boolean walk_left = Listener.isKeyDown(Main.WINDOW, Key.LEFT.key);
		boolean crouch = Listener.isKeyDown(Main.WINDOW, Key.CROUCH.key);
		boolean sprint = Listener.isKeyDown(Main.WINDOW, Key.SPRINT.key);
		boolean super_sprint = debugging && Listener.isKeyDown(Main.WINDOW, Key.SUPERSPRINT.key);
		boolean mega_sprint = debugging && Listener.isKeyDown(Main.WINDOW, Key.MEGASPRINT.key);
		
		
		
		double cowFactor = t.isRiding ? 2 : 1;
		t.maxWalkingSpeed = t.accWalking*cowFactor/5;
		
		double a = 0;
		if(t.where.g)
			if(t.where.water < 0.5) {//normal walking
				a = t.accWalking*cowFactor;
			} else {//walking under water
				a = t.accSwimming/cowFactor;
				t.maxWalkingSpeed = t.accWalking/cowFactor/5;
			}
		else if(t.where.water > 0) {//swimming
			a = t.accSwimming/cowFactor;
			t.maxWalkingSpeed = t.accWalking/cowFactor/5;
		} else {//flying
			a = t.accFlying*cowFactor;
		}
		
		double walkingDir = 0;
		if(walk_right )
		{
			walkingDir++;
		}
		if(walk_left )
		{
			walkingDir--;
		}
		if(!riding && crouch )
		{
			a *= 2;
			walkingDir *= 0.5;
			t.maxWalkingSpeed *= 0.5;
		}
		
		if(sprint )
		{
			walkingDir *= 2;
			t.maxWalkingSpeed *= 2;
			if(super_sprint )
			{
				walkingDir *= 4;
				t.maxWalkingSpeed *= 4;
				if(mega_sprint )
				{
					walkingDir *= 4;
					t.maxWalkingSpeed *= 4;
				}
			}
		}
		t.type.movement.setAni(t, walkingDir);
		t.walkingForce = walkingDir*a;
		if(debugging) {
			if(Listener.isKeyDown(Main.WINDOW, Key.ANTIGRAVITY.key)){
				t.force.shift(0, 5000);
			}
			if(Listener.isKeyDown(Main.WINDOW, Key.SUPERGRAVITY.key)){
				t.force.shift(0, -5000);
			}
			if(Listener.isKeyDown(Main.WINDOW, Key.FLY_RIGHT.key)){
				t.force.shift(5000, 1100);
			}
			if(Listener.isKeyDown(Main.WINDOW, Key.FLY_LEFT.key)){
				t.force.shift(-5000, 1100);
			}
		}
		
		//Scroll through inventory
		double scroll = Listener.getDWheel(Main.WINDOW);
		t.selectedItem += -scroll;
		if(t.selectedItem < 0){
			t.selectedItem %= t.itemStacks.length;
			t.selectedItem += t.itemStacks.length;
		}
		t.selectedItem %= t.itemStacks.length;
		return true;
	}

	@Override
	public boolean pressed(int button, Vec mousePos) {
		if(Main.world.avatar.health <= 0) return false;
		Main.world.window.forEachEffect(e -> e.pressed(button, mousePos));
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		if(Main.world.avatar.health <= 0) return false;
		Vec worldPos = mousePos.minus(Main.SIZE.w/2, Main.SIZE.h/2).shift(Main.world.avatar.pos);
		Thing[] livingsClickedOn = Main.world.thingWindow.livingsAt(worldPos);
		
		switch(button){
		case 0://ATTACK
			if(livingsClickedOn.length > 0 && Main.world.avatar.where.water == 0){
				Main.world.avatar.type.attack.attack(Main.world.avatar, null, Main.world.avatar.type.inv.getSelectedItem(Main.world.avatar), "", livingsClickedOn);
			}
			break;
		case 1://USE
			Thing[] objectsClickedOn = Main.world.thingWindow.thingsAt(worldPos);
			Main.world.avatar.type.inv.useSelectedItem(Main.world.avatar, worldPos, objectsClickedOn);
			break;
		case 2:
			objectsClickedOn = Main.world.thingWindow.thingsAt(worldPos);
			for(Thing t : objectsClickedOn){
				if(t.selected()) {
					Main.world.window.deselect(t);
				} else {
					Main.world.window.select(t);
				}
			}
			break;
		}
		
		Main.world.window.forEachEffect(e -> e.released(button, mousePos, pathSincePress));
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

	@Override
	public boolean keyPressed(int key) {
		if(Main.world.avatar.health <= 0) return false;
		
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
			if(Main.menu.open != Menus.EMPTY){
				Main.menu.setMenu(Menus.EMPTY);
			} else {
				Main.menu.setMenu(Menus.MAIN);
			}
			break;
		case DEBUG:
			if(Main.menu.open.stay) break;
			if(Main.menu.open != Menus.DEBUG && Settings.getBoolean("DEBUGGING")){
				Settings.set("SHOW_BOUNDING_BOX",true);
				Main.menu.setMenu(Menus.DEBUG);
			} else {
				Settings.set("SHOW_BOUNDING_BOX",false);
				Main.menu.setLast();
			}
			break;
		case STOP_GRAPH:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("STOP_GRAPH",!Settings.getBoolean("STOP_GRAPH"));
			break;
		case FASTER:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("timeScale", Settings.getDouble("timeScale")*1.25);
			break;
		case SLOWER:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("timeScale", Settings.getDouble("timeScale")*0.8);
			break;
		case FREEZE:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("FREEZE",!Settings.getBoolean("FREEZE"));
			break;
		case JUMPDOWN:
			if(Settings.getBoolean("DEBUGGING")) {
				World.world.avatar.pos.y -= 200;
				World.world.avatar.where.g = false;
			}
		case LAYERCOUNT_UP:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("LAYERS_TO_DRAW", Settings.getInt("LAYERS_TO_DRAW") + 1);
			break;
		case LAYERCOUNT_DOWN:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("LAYERS_TO_DRAW", Settings.getInt("LAYERS_TO_DRAW") - 1);
			break;
		case ZOOM_IN:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("ZOOM", Settings.getDouble("ZOOM")*1.25);
			break;
		case ZOOM_OUT:
			if(Settings.getBoolean("DEBUGGING"))
				Settings.set("ZOOM", Settings.getDouble("ZOOM")*0.8);
			break;
		case TOSS_COIN:
			if(Settings.getBoolean("DEBUGGING"))
				new Thing(ThingType.COIN, Main.world.avatar.link, Main.world.window.toWorldPos(Listener.getMousePos(Main.core.getWindow().getHandle())), 1, new Vec(World.rand.nextInt(401)-200, World.rand.nextInt(300) + 100));
			break;
		default:
		}
		//No, don't add if-clauses here!! Add the keys legally!!
		return false;
	}
	public boolean keyReleased(int key) {
		if(Main.world.avatar.health <= 0) return false;
		return false;
	}

	public boolean charTyped(char ch) {
		if(Main.world.avatar.health <= 0) return false;
		return false;
	}

}