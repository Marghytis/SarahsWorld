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

		public FollowPlugin(Entity thing) {
			super(thing);
		}
		
		public boolean followTarget(double delta) {
			if(Settings.getBoolean("AGGRESSIVE_CREATURES")){
				findTarget(thing);
				int acc = 0;
				if(thing.target != null){
					double r = rAim + (thing.aniPlug.getRenderBox().size.x/2);
					if(thing.target.pos.minus(thing.pos).lengthSquare() > r*r) {
						if(thing.target.pos.x > thing.pos.x){
							acc += 2;
						} else if(thing.target.pos.x < thing.pos.x){
							acc -= 2;
						}
					} else {
					}
					thing.type.movement.setAni(thing, acc);
					thing.walkingForce = acc*2*thing.accWalking;
					thing.maxWalkingSpeed = thing.accWalking/5;

					if(acc == 0)
						return true;
				}
			} else {
				thing.target = null;
			}
			return false;
		}
		
	}

	public void findTarget(Thing t){
		if(t.target == null || t.target.pos.minus(t.pos).lengthSquare() > maxDistanceSquare || t.target.health <= 0){
			Thing closest = null;
			double distanceSquare = maxDistanceSquare+10;
			for(int type = 0; type < targetClasses.length; type++){
				for(ColumnListElement c = Main.world.landscapeWindow.start(); c != Main.world.landscapeWindow.end(); c = c.next())
				for(Entity t2 = c.firstThing(targetClasses[type].ordinal); t2 != null; t2 = t2.next()){
					double distSqu = t.pos.minus(t2.pos).lengthSquare();
					if(distSqu < distanceSquare){
						closest = (Thing)t2;
						distanceSquare = distSqu;
					}
				}
			}
			t.target = closest;
		}
	}
}
