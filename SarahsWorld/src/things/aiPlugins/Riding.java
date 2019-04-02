package things.aiPlugins;

import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;
import util.math.Rect;

public class Riding extends AiPlugin2 {

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
	
	@Override
	public RidingPlugin createAttribute(Entity thing) {
		return new RidingPlugin(thing);
	}
	
	public class RidingPlugin extends ThingPlugin {

		public RidingPlugin(Entity thing) {
			super(thing);
		}
		
	}
	
	public void mount(Thing rider, Thing horse){
		
		rider.isRiding = true;
		
		rider.aniPlug.setRenderBox( ridingBox);
		rider.aniPlug.setAniSet( 1);
		rider.aniPlug.setNeedsUnusualRenderUpdate( true);
		
		if(rider.mountedThing != null){//rider is already riding on something
			rider.aniPlug.getAnimator().setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount+1], () -> {
				
				freeHorse(rider);

				rider.mountedThing = horse;
				rider.pos.set(horse.pos);
				rider.where = horse.where;
				horse.hide();
				rider.aniPlug.getAnimator().setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount], () -> rider.aniPlug.setAnimation( "stand"));
			});
		} else {
			rider.mountedThing = horse;
			rider.pos.set(horse.pos);
			rider.where = horse.where;
			horse.hide();
			rider.aniPlug.getAnimator().setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount], () -> rider.aniPlug.setAnimation( "stand"));
		}
	}
	
	public void dismount(Thing rider){
		rider.aniPlug.getAnimator().setAnimation(rider.type.ani.animations[1][rider.type.ani.aniCount+1], () -> {
			rider.isRiding = false;
			freeHorse(rider);
			rider.aniPlug.setAniSet( 0);
			rider.aniPlug.setRenderBox( normalBox);
			rider.aniPlug.setNeedsUnusualRenderUpdate( true);
			rider.aniPlug.setAnimation( "stand");
		});
	}
	
	public void freeHorse(Thing rider) {
		rider.mountedThing.showUpAfterHiding(rider.newLink);
		rider.mountedThing.pos.set(rider.pos);
		rider.mountedThing.vel.set(rider.vel);
		rider.mountedThing.aniPlug.setOrientation( rider.aniPlug.getOrientation());
		rider.mountedThing.where = rider.where;
		rider.mountedThing = null;
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
