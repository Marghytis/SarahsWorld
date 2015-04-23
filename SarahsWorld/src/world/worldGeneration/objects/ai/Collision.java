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
			Vertex[] f = new Vertex[1];
			Vec finalIntersection = new Vec(Integer.MIN_VALUE, Integer.MIN_VALUE);
			Vec lastVec = null, currentVec = null;
			for(Column c = Main.world.window.leftEnd; c != null; c = c.right){
				if(c.equals(Main.world.window.leftEnd)){
					lastVec = new Vec(c.xReal, c.collisionVecs[0]);
					currentVec = lastVec.copy();
				} else {
					Vec intersection = new Vec();
					boolean collision = UsefulF.intersectionLines(t.pos.p, t.pos.p.copy().shift(t.vel.v, delta), lastVec, currentVec.set(c.xReal, c.collisionVecs[0]), intersection);
					
					if(collision && intersection.y > finalIntersection.y){
						finalIntersection.set(intersection);
						f[0] = c.vertices[0];
					}
				}
			};
			if(f[0] != null){
				
				t.pos.p.set(finalIntersection);
				
				t.ground.speed = -t.vel.v.dot(f[0].parent.getTopLine())/f[0].parent.getTopLine().length();
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
