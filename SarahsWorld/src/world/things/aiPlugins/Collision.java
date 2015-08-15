package world.things.aiPlugins;

import main.Main;
import util.math.UsefulF;
import util.math.Vec;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.things.AiPlugin;
import world.things.Thing;

public class Collision extends AiPlugin{
	
	public Collision(Thing t){
		super(t);
	}

	Vec finalIntersection = new Vec(), lastVec = new Vec(), currentVec = new Vec(), intersection = new Vec(), topLine = new Vec();
	public boolean action(double delta) {
		if(!t.ground.g){
			//TODO set the WorldLink of the Walking (and speed)
			Vertex f = null;
			finalIntersection.set(Integer.MIN_VALUE, Integer.MIN_VALUE);
			for(Column c = Main.world.window.leftEnd; c != Main.world.window.rightEnd && c != null; c = c.right){
				if(c.collisionVec == -1) continue;
				if(c.equals(Main.world.window.leftEnd)){
					lastVec.set(c.xReal, c.vertices[c.collisionVec].y);
					currentVec = lastVec.copy();
				} else {
					boolean collision = UsefulF.intersectionLines(t.pos.p, t.pos.copy().shift(t.vel.v.copy().shift(t.acc.a, delta), delta), lastVec, currentVec.set(c.xReal, c.vertices[c.collisionVec].y), intersection);
					
					if(collision && intersection.y > finalIntersection.y){
						finalIntersection.set(intersection);
						f = c.vertices[c.collisionVec];
					}
					lastVec.set(currentVec);
				}
			};
			if(f != null){
				t.pos.set(finalIntersection);

				if(t.friction != null && !t.friction.swimming){
					t.ground.land();
				}
				t.ground.speed = t.vel.v.dot(f.parent.getTopLine(topLine))/f.parent.getTopLine(topLine).length();
				t.vel.v.set(0, 0);
				t.ground.g = true;
				t.ground.link = f;
				f.parent.add(t);
			}
		}
		return true;
	}

	public String save() {return "";}
	public void load(String data){}
	
}
