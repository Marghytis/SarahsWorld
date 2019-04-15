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
		
		private Thing mountedThing;
		private boolean isRiding;

		public RidingPlugin(Entity thing) {
			super(thing);
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
					thing.where = horse.where;
					horse.hide();
					thing.aniPlug.getAnimator().setAnimation(thing.type.ani.animations[1][thing.type.ani.aniCount], () -> thing.aniPlug.setAnimation( "stand"));
				});
			} else {
				mountedThing = horse;
				thing.pos.set(horse.pos);
				thing.where = horse.where;
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
			mountedThing.vel.set(thing.vel);
			mountedThing.aniPlug.setOrientation( thing.aniPlug.getOrientation());
			mountedThing.where = thing.where;
			mountedThing = null;
		}
	}
	
	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

}
