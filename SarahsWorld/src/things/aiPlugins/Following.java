package things.aiPlugins;

import main.Main;
import menu.Settings;
import things.AiPlugin;
import things.Thing;
import things.ThingType;
import world.data.Column;
import world.data.Dir;

public class Following extends AiPlugin{

	double maxDistanceSquare;
	double rAimSq, rAim;
	ThingType[] targetClasses;
	
	public Following(double maxDistance, double rAim, ThingType... classes){
		this.maxDistanceSquare = maxDistance*maxDistance;
		this.rAim = rAim;
		this.rAimSq = rAim*rAim;
		this.targetClasses = classes;
	}

	public boolean action(Thing t, double delta) {
		if(Settings.AGGRESSIVE_CREATURES){
			findTarget(t);
			int acc = 0;
			if(t.target != null){
				double r = rAim + (t.box.size.x/2);
				if(t.target.pos.minus(t.pos).lengthSquare() > r*r) {
					if(t.target.pos.x > t.pos.x){
						acc += 2;
					} else if(t.target.pos.x < t.pos.x){
						acc -= 2;
					}
				} else {
				}
				t.type.movement.setAni(t, acc);
				t.walkingForce = acc*2*t.accWalking;
				t.maxWalkingSpeed = t.accWalking/5;

				if(acc == 0){
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
				for(Column c = Main.world.landscapeWindow.getEnd(Dir.l); c != Main.world.landscapeWindow.getEnd(Dir.r).next(Dir.r); c = c.next(Dir.r))
				for(Thing t2 = c.things[targetClasses[type].ordinal]; t2 != null; t2 = t2.next){
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
