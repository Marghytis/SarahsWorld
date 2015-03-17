package world.objects.ai;

import main.Main;
import util.math.UsefulF;
import util.math.Vec;
import world.WorldContainer.WorldField;
import world.objects.Thing;

public class Collision extends AiPlugin{
	
	public Collision(Thing t){
		super(t);
	}

	public boolean action(double delta) {
		if(!t.ground.g){
			//TODO set the WorldLink of the Walking (and speed)
			WorldField[] f = new WorldField[1];
			Vec finalIntersection = new Vec(Integer.MIN_VALUE, Integer.MIN_VALUE);
			Main.world.columns.forEach((c) -> {
				Vec intersection = new Vec();
				boolean collision = UsefulF.intersectionLines(t.pos.p, t.pos.p.copy().shift(t.vel.v, delta), c.topLeft.p, c.topRight.p, intersection);
				
				if(collision && intersection.y > finalIntersection.y){
					finalIntersection.set(intersection);
					f[0] = c.fields[0];
				}
			});
			if(f[0] != null){
				
				t.pos.p.set(finalIntersection);
				
				t.ground.speed = -t.vel.v.dot(f[0].getTopLine().size)/f[0].getTopLine().size.length();
				t.vel.v.set(0, 0);
				t.ground.link = f[0];
				t.ground.g = true;
				t.ground.land();
			}
		}
		return true;
	}

	public String save() {return "";}
	public void load(String data){}
	
}
