package extra.things.traits;

import basis.entities.Entity;
import basis.entities.Species;
import basis.entities.Trait;
import extra.things.Thing;
import extra.things.ThingAttribute;
import extra.things.ThingType;
import util.math.Rect;

public class Riding extends Trait {

	Rect normalBox, ridingBox;
	private ThingType[] rideableThingTypes;
	
	/**
	 * mount and dismount textures are at the end of the animation array
	 * @param normalBox
	 * @param ridingBox
	 */
	public Riding(Rect normalBox, Rect ridingBox, ThingType... rideableThingTypes) {
		this.normalBox = normalBox;
		this.ridingBox = ridingBox;
		this.rideableThingTypes = rideableThingTypes;
	}
	
	@Override
	public RidingPlugin createAttribute(Entity thing) {
		return new RidingPlugin(thing);
	}
	
	public class RidingPlugin extends ThingAttribute {
		
		private Thing mountedThing;
		private boolean isRiding;

		public RidingPlugin(Entity thing) {
			super(thing);
		}
		
		public boolean canRide(Species<?> type) {
			for(ThingType rideable : rideableThingTypes) {
				if(type == rideable)
					return true;
			}
			return false;
		}
		
		public boolean isRiding() {
			return isRiding;
		}

		public void mount(Thing horse){
			
			isRiding = true;
			
			thing.aniPlug.setRenderBox( ridingBox);
			thing.aniPlug.setAniSet( 1);
			thing.aniPlug.setNeedsUnusualRenderUpdate( true);
			
			if(mountedThing != null){//rider is already riding on something
				thing.aniPlug.getAnimator().setAnimation(thing.type.ani.animations[1][thing.type.ani.aniCount+1], () -> {
					
					freeHorse();

					mountedThing = horse;
					thing.pos.set(horse.pos);
					thing.physicsPlug.copyWhere(horse);
					horse.hide();
					thing.aniPlug.getAnimator().setAnimation(thing.type.ani.animations[1][thing.type.ani.aniCount], () -> thing.aniPlug.setAnimation( "stand"));
				});
			} else {
				mountedThing = horse;
				thing.pos.set(horse.pos);
				thing.physicsPlug.copyWhere(horse);
				horse.hide();
				thing.aniPlug.getAnimator().setAnimation(thing.type.ani.animations[1][thing.type.ani.aniCount], () -> thing.aniPlug.setAnimation( "stand"));
			}
		}
		
		public void dismount(){
			thing.aniPlug.getAnimator().setAnimation(thing.type.ani.animations[1][thing.type.ani.aniCount+1], () -> {
				isRiding = false;
				freeHorse();
				thing.aniPlug.setAniSet( 0);
				thing.aniPlug.setRenderBox( normalBox);
				thing.aniPlug.setNeedsUnusualRenderUpdate( true);
				thing.aniPlug.setAnimation( "stand");
			});
		}
		
		public void freeHorse() {
			mountedThing.showUpAfterHiding(thing.newLink);
			mountedThing.pos.set(thing.pos);
			mountedThing.physicsPlug.setVel(thing.physicsPlug.velX(), thing.physicsPlug.velY());
			mountedThing.aniPlug.setOrientation( thing.aniPlug.getOrientation());
			mountedThing.physicsPlug.copyWhere(thing);
			mountedThing = null;
		}
	}
	
	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
