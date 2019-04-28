package things.aiPlugins;

import main.Main;
import menu.Settings;
import things.AiPlugin2;
import things.Entity;
import things.Thing;
import things.ThingPlugin;
import things.ThingType;
import world.data.ColumnListElement;

public class Following extends AiPlugin2 {

	double maxDistanceSquare;
	double rAimSq, rAim;
	ThingType[] targetClasses;
	
	public Following(double maxDistance, double rAim, ThingType... classes){
		this.maxDistanceSquare = maxDistance*maxDistance;
		this.rAim = rAim;
		this.rAimSq = rAim*rAim;
		this.targetClasses = classes;
	}
	
	@Override
	public FollowPlugin createAttribute(Entity thing) {
		return new FollowPlugin(thing);
	}
	
	public class FollowPlugin extends ThingPlugin {
		
		private Thing target;

		public FollowPlugin(Entity thing) {
			super(thing);
		}
		
		public Thing getTarget() {
			return target;
		}
		
		public boolean followTarget(double delta) {
			if(Settings.getBoolean("AGGRESSIVE_CREATURES")){
				findTarget();
				int acc = 0;
				if(target != null){
					double r = rAim + (thing.aniPlug.getRenderBox().size.x/2);
					if(target.pos.minus(thing.pos).lengthSquare() > r*r) {
						if(target.pos.x > thing.pos.x){
							acc += 2;
						} else if(target.pos.x < thing.pos.x){
							acc -= 2;
						}
					} else {
					}
					thing.movementPlug.setAni(acc);
					thing.physicsPlug.setWalkingForce(acc*2*thing.movementPlug.accWalking());
					thing.physicsPlug.setMaxWalkingSpeed(thing.movementPlug.accWalking()/5);

					if(acc == 0)
						return true;
				}
			} else {
				target = null;
			}
			return false;
		}
		
		private void findTarget(){
			if(target == null || target.pos.minus(thing.pos).lengthSquare() > maxDistanceSquare || target.lifePlug.health() <= 0){
				Thing closest = null;
				double distanceSquare = maxDistanceSquare+10;
				for(int type = 0; type < targetClasses.length; type++){
					for(ColumnListElement c = Main.world.landscapeWindow.start(); c != Main.world.landscapeWindow.end(); c = c.next())
					for(Entity t2 = c.firstThing(targetClasses[type].ordinal); t2 != null; t2 = t2.next()){
						double distSqu = thing.pos.minus(t2.pos).lengthSquare();
						if(distSqu < distanceSquare){
							closest = (Thing)t2;
							distanceSquare = distSqu;
						}
					}
				}
				target = closest;
			}
		}
		
	}
}
