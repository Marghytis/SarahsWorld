package world.worldGeneration.objects.ai;

import main.Main;
import util.math.UsefulF;
import util.math.Vec;
import world.worldGeneration.WorldData.Column;
import world.worldGeneration.WorldData.Vertex;

public class Collision extends AiPlugin{
	
	public Collision(Thing t){
		super(t);
	}

	public boolean action(double delta) {
		if(!t.ground.g){
			//TODO set the WorldLink of the Walking (and speed)
			Vertex f = null;
			Vec finalIntersection = new Vec(Integer.MIN_VALUE, Integer.MIN_VALUE);
			Vec lastVec = null, currentVec = null;
			for(Column c = Main.world.window.leftEnd; c != null; c = c.right){
				if(c.collisionVec == -1) continue;
				if(c.equals(Main.world.window.leftEnd)){
					lastVec = new Vec(c.xReal, c.vertices[c.collisionVec].y);
					currentVec = lastVec.copy();
				} else {
					Vec intersection = new Vec();
					boolean collision = UsefulF.intersectionLines(t.pos.p, t.pos.p.copy().shift(t.vel.v.copy().shift(t.acc.a, delta), delta), lastVec, currentVec.set(c.xReal, c.vertices[c.collisionVec].y), intersection);
					
					if(collision && intersection.y > finalIntersection.y){
						finalIntersection.set(intersection);
						f = c.vertices[c.collisionVec];
					}
					lastVec.set(currentVec);
				}
			};
			if(f != null){
				
				t.pos.p.set(finalIntersection);
				
				t.ground.speed = t.vel.v.dot(f.parent.getTopLine())/f.parent.getTopLine().length();
				t.vel.v.set(0, 0);
				t.ground.link = f;
				f.parent.add(t);
				t.ground.g = true;
				t.ground.land();
			}
		}
		return true;
	}

	public String save() {return "";}
	public void load(String data){}
	
}
