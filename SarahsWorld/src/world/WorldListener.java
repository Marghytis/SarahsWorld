package world;

import core.Listener;
import input.PollData;
import item.ItemType;
import menu.Settings;
import menu.Settings.Key;
import things.Thing;
import things.ThingType;
import util.math.Vec;

public class WorldListener implements Listener {

	Vec worldPos = new Vec();
	World world;
	PollData input;
	
	public WorldListener(World world, PollData input) {
		this.world = world;
		this.input = input;
	}
	
	public PollData getInputData() {
		return input;
	}
	
	@Override
	public boolean pressed(int button, Vec mousePos) {
		if(world.avatar.health <= 0) return false;
		worldPos.set(mousePos);
		world.window.toWorldPos(worldPos);
		world.window.forEachEffect(e -> e.pressed(button, mousePos));
		return false;
	}

	@Override
	public boolean released(int button, Vec mousePos, Vec pathSincePress) {
		if(world.avatar.health <= 0) return false;
//		Vec worldPos = mousePos.minus(SIZE.w/2, SIZE.h/2).shift(world.avatar.pos);

		worldPos.set(mousePos);
		world.window.toWorldPos(worldPos);
		Thing[] livingsClickedOn = world.thingWindow.livingsAt(worldPos);
		
		switch(button){
		case 0://ATTACK
			if(world.avatar.where.water == 0){
				world.avatar.type.attacking.attack(world.avatar, world.avatar.type.inv.getSelectedItem(world.avatar), worldPos, livingsClickedOn);
			}
			break;
		case 1://USE
			Thing[] objectsClickedOn = world.thingWindow.thingsAt(worldPos);
			world.avatar.type.inv.useSelectedItem(world.avatar, worldPos, objectsClickedOn);
			break;
		case 2:
			if(Settings.getBoolean("DEBUGGING")) {
				objectsClickedOn = world.thingWindow.thingsAt(worldPos);
				for(Thing t : objectsClickedOn){
					if(t.selected()) {
						world.window.deselect(t);
					} else {
						world.window.select(t);
					}
				}
			}
			break;
		}
		
		world.window.forEachEffect(e -> e.released(button, mousePos, pathSincePress));
//		int hitThingLoc = -3;
//		Thing[] hitThing = new Thing[1];
//		for(List<Thing> list : world.objects) for(Thing t : list){
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
		if(world.avatar.health <= 0) return false;
		
		Key bind = Key.getBinding(key);
		
		switch(bind){
		case JUMP: if(world.avatar.where.g) world.avatar.type.movement.jump(world.avatar); break;
		case DISMOUNT: if(world.avatar.isRiding) world.avatar.type.ride.dismount(world.avatar); break;
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
			break;
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
				world.thingWindow.add(new Thing(ThingType.COIN, world.avatar.link, world.window.toWorldPos(input.getMousePos()), 1, new Vec(World.rand.nextInt(401)-200, World.rand.nextInt(300) + 100)));
			break;
		case THROW_ITEM:
			ItemType type = world.avatar.type.inv.getSelectedItem(world.avatar);
			if(type != null) {
				world.thingWindow.add(new Thing(ThingType.ITEM, world.avatar.link, world.avatar.pos.copy().shift(0, 60), type, new Vec(World.rand.nextInt(401)-200, World.rand.nextInt(300) + 100)));
				world.avatar.type.inv.addItem(world.avatar, type, -1);
			}
			break;
		case REFILL_BUFFERS:
			world.thingWindow.refillBuffers();
			break;
		case RELOAD_TERRAIN:
			world.landscapeWindow.reload();
			break;
		default:
		}
		//No, don't add if-clauses here!! Add the keys legally!!
		return false;
	}
	public boolean keyReleased(int key) {
		if(world.avatar.health <= 0) return false;
		return false;
	}

	public boolean charTyped(char ch) {
		if(world.avatar.health <= 0) return false;
		return false;
	}
}
