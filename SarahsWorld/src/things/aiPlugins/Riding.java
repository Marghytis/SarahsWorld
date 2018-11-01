package things.aiPlugins;

import main.Main;
import things.AiPlugin;
import things.Thing;
import util.math.Rect;

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
	
	public void mount(Thing rider, Thing horse){
		
		rider.isRiding = true;
		
		rider.type.file = rider.type.ani.animations[1][0].atlas;
		rider.box = ridingBox;
		rider.aniSet = 1;
		rider.needsUnusualRenderUpdate = true;
		
		if(rider.mountedThing != null){//rider is already riding on something
			rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount+1], () -> {
				rider.mountedThing.pos.set(rider.pos);
				rider.mountedThing.vel.set(rider.vel);
				rider.mountedThing.dir = rider.dir;
				rider.mountedThing.oldLink = rider.mountedThing.link;
				rider.mountedThing.link = rider.link;
				rider.mountedThing.applyLink();

				rider.mountedThing = horse;
				rider.pos.set(horse.pos);
				Main.world.engine.requestDeletion(horse);
				rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount], () -> rider.type.ani.setAnimation(rider, "stand"));
			});
		} else {
			rider.mountedThing = horse;
			rider.pos.set(horse.pos);
			rider.where = horse.where;
			Main.world.engine.requestDeletion(horse);
			rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount], () -> rider.type.ani.setAnimation(rider, "stand"));
		}
	}
	
	public void dismount(Thing rider){
		rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount+1], () -> {
			rider.isRiding = false;
			rider.box = normalBox;
			rider.mountedThing.pos.set(rider.pos);
			rider.mountedThing.vel.set(rider.vel);
			rider.mountedThing.dir = rider.dir;
			rider.mountedThing.where = rider.where;
			rider.mountedThing.oldLink = rider.mountedThing.link;
			rider.mountedThing.link = rider.link;
			rider.mountedThing.applyLink();
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
