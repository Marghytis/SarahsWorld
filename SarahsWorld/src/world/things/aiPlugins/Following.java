package world.things.aiPlugins;

import world.things.AiPlugin;
import world.things.Thing;
import world.things.ThingType;
import main.Main;
import main.Settings;

public class Following extends AiPlugin{

	double a = 1000;
	public Thing target;
	double maxDistanceSquare;
	ThingType[] targetClasses;
	DoWithThing onReached;
	
	@SafeVarargs
	public Following(Thing t, double a, double maxDistance, DoWithThing onReached, ThingType... classes){
		super(t);
		this.a = a;
		this.maxDistanceSquare = maxDistance*maxDistance;
		this.onReached = onReached;
		this.targetClasses = classes;
	}
	
	public boolean action(double delta) {
		if(Settings.AGGRESSIVE_CREATURES){
			if(t.ground.g){
				findTarget();
				double acc = 0;
				if(target != null){
					t.ground.sprint = true;
					if(target.pos.p.x - (target.ani.box.size.x/2) > t.pos.p.x){
						acc += a;
					} else if(target.pos.p.x + (target.ani.box.size.x/2) < t.pos.p.x){
						acc -= a;
					} else {
						t.ground.sprint = false;
						onReached.doIt(target);
					}
				} else {
					t.ground.sprint = false;
				}
				t.ground.setAni(acc);
				t.ground.acc += acc;
			}
		}
		return target != null;
	}
	
	public void findTarget(){
		if(target == null || target.pos.p.minus(t.pos.p).lengthSquare() > maxDistanceSquare){
			Thing closest = null;
			double distanceSquare = maxDistanceSquare+10;
			for(int type = 0; type < targetClasses.length; type++){
				for(Thing t = Main.world.window.leftEnd.left.things[targetClasses[type].ordinal()];t != null && t != Main.world.window.rightEnd.things[targetClasses[type].ordinal()];t = t.right){
					if(t.type == ThingType.DUMMY) continue;
					double distSqu = this.t.pos.p.minus(t.pos.p).lengthSquare();
					if(distSqu < distanceSquare){
						closest = t;
						distanceSquare = distSqu;
					}
				}
			}
			target = closest;
		}
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}

	public interface DoWithThing {
		public abstract void doIt(Thing t);
	}
}
