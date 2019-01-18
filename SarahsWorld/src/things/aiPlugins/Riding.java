package things.aiPlugins;

import things.AiPlugin;
import things.Thing;
import util.math.Rect;

public class Riding extends AiPlugin<Thing> {

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
		
		rider.box = ridingBox;
		rider.aniSet = 1;
		rider.needsUnusualRenderUpdate = true;
		
		if(rider.mountedThing != null){//rider is already riding on something
			rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount+1], () -> {
				
				freeHorse(rider);

				rider.mountedThing = horse;
				rider.pos.set(horse.pos);
				rider.where = horse.where;
				horse.hide();
				rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount], () -> rider.aniPlug.setAnimation( "stand"));
			});
		} else {
			rider.mountedThing = horse;
			rider.pos.set(horse.pos);
			rider.where = horse.where;
			horse.hide();
			rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount], () -> rider.aniPlug.setAnimation( "stand"));
		}
	}
	
	public void dismount(Thing rider){
		rider.ani.setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount+1], () -> {
			rider.isRiding = false;
			freeHorse(rider);
			rider.aniSet = 0;
			rider.box = normalBox;
			rider.needsUnusualRenderUpdate = true;
			rider.aniPlug.setAnimation( "stand");
		});
	}
	
	public void freeHorse(Thing rider) {
		rider.mountedThing.showUpAfterHiding(rider.link);
		rider.mountedThing.pos.set(rider.pos);
		rider.mountedThing.vel.set(rider.vel);
		rider.mountedThing.dir = rider.dir;
		rider.mountedThing.where = rider.where;
		rider.mountedThing = null;
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
