package world.things.aiPlugins;

import main.Main;
import menu.Settings;
import world.things.AiPlugin;
import world.things.ThingProps;
import world.things.ThingType;

public class Following extends AiPlugin{

	double maxDistanceSquare;
	ThingType[] targetClasses;
	DoWithThing onReached;
	
	@SafeVarargs
	public Following(double maxDistance, DoWithThing onReached, ThingType... classes){
		this.maxDistanceSquare = maxDistance*maxDistance;
		this.onReached = onReached;
		this.targetClasses = classes;
	}

	public void setup(ThingProps t) {}
	
	public boolean update(ThingProps t, double delta) {
		if(Settings.getBoolean("AGGRESSIVE_CREATURES")){
			findTarget(t);
			double acc = 0;
			if(t.target != null){
				if(!onReached.doIt(t, t.target)) {
					if(t.target.pos.x - (t.target.box.size.x/2) > t.pos.x){
						acc += 2;
					} else if(t.target.pos.x + (t.target.box.size.x/2) < t.pos.x){
						acc -= 2;
					}
				}
			} else {
			}
			t.type.movement.setAni(t, 2);
			t.walkingForce = acc*2*t.accWalking;
		}
		return t.target != null;
	}
	
	public void findTarget(ThingProps t){
		if(t.target == null || t.target.pos.minus(t.pos).lengthSquare() > maxDistanceSquare){
			ThingProps closest = null;
			double distanceSquare = maxDistanceSquare+10;
			for(int type = 0; type < targetClasses.length; type++){
				for(ThingProps t2 = Main.world.window.leftEnd.left.things[targetClasses[type].ordinal];t2 != null && t2 != Main.world.window.rightEnd.things[targetClasses[type].ordinal];t2 = t2.right){
					if(t2.type == ThingType.DUMMY) continue;
					double distSqu = t.pos.minus(t2.pos).lengthSquare();
					if(distSqu < distanceSquare){
						closest = t2;
						distanceSquare = distSqu;
					}
				}
			}
			t.target = closest;
		}
	}

	public interface DoWithThing {
		/**
		 * 
		 * @param src
		 * @param tgt
		 * @return if it succeeded
		 */
		public abstract boolean doIt(ThingProps src, ThingProps tgt);//source thing, target thing
	}
}
