package things.aiPlugins;

import main.Main;
import menu.Settings;
import things.AiPlugin;
import things.Thing;
import things.ThingType;

public class Following extends AiPlugin{

	double maxDistanceSquare;
	double rAimSq;
	ThingType[] targetClasses;
	
	@SafeVarargs
	public Following(double maxDistance, double rAim, ThingType... classes){
		this.maxDistanceSquare = maxDistance*maxDistance;
		this.rAimSq = rAim*rAim;
		this.targetClasses = classes;
	}

	public boolean action(Thing t, double delta) {
		if(Settings.AGGRESSIVE_CREATURES){
			findTarget(t);
			int acc = 0;
			if(t.target != null){
				if(t.target.pos.minus(t.pos).lengthSquare() > rAimSq) {
					if(t.target.pos.x > t.pos.x){
						acc += 2;
					} else if(t.target.pos.x < t.pos.x){
						acc -= 2;
					}
					if(acc != 0){
						t.type.movement.setAni(t, acc);
						t.walkingForce = acc*2*t.accWalking;
						t.maxWalkingSpeed = t.accWalking/5;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	public void findTarget(Thing t){
		if(t.target == null || t.target.pos.minus(t.pos).lengthSquare() > maxDistanceSquare){
			Thing closest = null;
			double distanceSquare = maxDistanceSquare+10;
			for(int type = 0; type < targetClasses.length; type++){
				for(Thing t2 = Main.world.window.leftEnd.left.things[targetClasses[type].ordinal];t2 != null && t2 != Main.world.window.rightEnd.things[targetClasses[type].ordinal];t2 = t2.right){
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
}
