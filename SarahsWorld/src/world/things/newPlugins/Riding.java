package world.things.newPlugins;

import main.Main;
import util.math.Rect;
import world.things.AiPlugin;
import world.things.ThingProps;

public class Riding extends AiPlugin {

	Rect normalBox, ridingBox;
	
	/**
	 * mount and dismount textures are at the end of the animation array
	 * @param normalBox
	 * @param ridingBox
	 */
	public Riding(Rect normalBox, Rect ridingBox) {
		this.normalBox = normalBox;
		this.ridingBox = ridingBox;
	}
	
	public void mount(ThingProps rider, ThingProps horse){
		
		rider.isRiding = true;
		
		rider.type.file = rider.type.ani.animations[1][0].atlas;
		rider.box = ridingBox;
		rider.aniSet = 1;
		
		if(rider.mountedThing != null){//rider is already riding on something
			rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount+1], () -> {
				rider.mountedThing.pos.set(rider.pos);
				rider.mountedThing.vel.set(rider.vel);
				rider.mountedThing.dir = rider.dir;
				rider.link.parent.add(rider.mountedThing);

				rider.mountedThing = horse;
				rider.pos.set(horse.pos);
				Main.world.window.deletionRequested.add(horse);
				rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount], () -> rider.type.ani.setAnimation(rider, "stand"));
			});
		} else {
			rider.mountedThing = horse;
			rider.pos.set(horse.pos);
			rider.where = horse.where;
			Main.world.window.deletionRequested.add(horse);
			rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount], () -> rider.type.ani.setAnimation(rider, "stand"));
		}
	}
	
	public void dismount(ThingProps rider){
		rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount+1], () -> {
			rider.isRiding = false;
			rider.box = normalBox;
			rider.mountedThing.pos.set(rider.pos);
			rider.mountedThing.vel.set(rider.vel);
			rider.mountedThing.dir = rider.dir;
			rider.mountedThing.where = rider.where;
			rider.link.parent.add(rider.mountedThing);
			rider.mountedThing = null;
			rider.type.ani.setAnimation(rider, "stand");
			rider.type.file = rider.type.ani.animations[0][0].atlas;
		});
	}

	public boolean action(double delta) {
		return false;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
