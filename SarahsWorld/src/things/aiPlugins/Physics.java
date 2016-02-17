package things.aiPlugins;

import main.Main;
import menu.Settings;
import things.AiPlugin;
import things.Thing;
import things.ThingType;
import util.math.UsefulF;
import util.math.Vec;
import world.Material;
import world.WorldData.Column;
import world.WorldData.Vertex;

public class Physics extends AiPlugin{
	
	public static double lowestSpeed = 0.1;
	
	public static double walk = 1000;
	public static double jump = 500;
	public static float grassFriction = 1;
	public static Vec gravity = new Vec(0, -981);
	public static double airFriction = 0.001;
	
	public double mass = 1;
	public double airea = 1;//for air friction
	public double friction = 0.5, stiction = 0.01;
	public boolean frict, grav, coll, water;

	/**
	 * Should generally be one of the last plugins to update, so that other forces can come to effect
	 * @param mass
	 * @param airea
	 */
	public Physics(double mass, double airea, boolean frict, boolean grav, boolean coll, boolean water) {
		this.mass = mass;
		this.airea = airea;
		this.frict = frict;
		this.grav = grav;
		this.coll = coll;
		this.water = water;
	}
	
	public Physics(double mass, double airea){
		this(mass, airea, true, true, true, true);
	}
	Vec approxNextVel = new Vec();
	Vec approxNextAvVel = new Vec();
	Vec approxNextPos = new Vec();
	Vec approxNextForce = new Vec();
	Vec averageForce = new Vec();
	Vec nextVel = new Vec();
	Vec nextVelAvDelta = new Vec();
	Vec nextPos = new Vec();
	
	boolean collision, collisionTotal;
	
	static double dt = 0.010;
	public void update(Thing t, double delta){
		if(ThingType.SARAH.avatar.freeze){
			return;
		}
		
		Vec constantForce = t.force.copy();
		t.where.water = 0;
//		t.g = false;
		
		{//COLLISION TESTING-------------------------------------------O
			//Test for collision in time intervals of dt=0.01s length
			int steps = (int)(delta/dt);
			double step = delta/steps;
			for(int i = 0; i < steps; i++){
				double time = step;
				if(!t.where.g)
					time = freeFallOrCollision(t, constantForce, time);
				if(t.where.g)
					walkOrLiftOf(t, constantForce, time);
			}
				
		}
		
		
		{//RESET FORCE-------------------------------------------------O
			//reset force to gravity only
			if(grav)
				t.force.set(gravity).scale(mass);
			else
				t.force.set(0, 0);
		}
		
		
		{//UPDATE WHERE------------------------------------------------O
			if(t.where.g){
				if(airTime > 0.5 && t.type.movement != null)
					t.type.movement.land(t);
				t.reallyAir = false;
				airTime = 0;
			} else {
				airTime += delta;
				if(!t.reallyAir && airTime > 0.4 && t.type.movement != null){
					t.type.ani.setAnimation(t, t.type.movement.fly);
					t.reallyAir = true;
				}
			}
		}
		
		
		//UPDATE WORLD LINK
		t.link = Main.world.window.get((int)Math.floor(t.pos.x/Column.step));
		t.link.add(t);
	}
	
	Vec circleCollision = new Vec(), walkingForce = new Vec();
	
	public double freeFallOrCollision(Thing t, Vec constantForce, double delta){
		//constant Force minus air friction, nextVel and nextPos get set
		updateForceNextVelAndNextPos(t, constantForce, null, delta, true);
		if(collision(t.pos, nextVelAvDelta)){
//			if(t.type == ThingType.SARAH) System.out.println("Collision! " + collisionVec);
			double t1 = calculateCollisionTime(collisionVec.minus(t.pos).length(), t.force.length()/mass, t.vel.length());
			Vec topLine = collisionC.getTopLine(new Vec()).normalize();
			updateForceNextVelAndNextPos(t, constantForce, null, t1, true);
			t.pos.set(collisionVec);
			t.link = collisionC;
			t.willLandInWater = false;
			
			t.speed = nextVel.dot(topLine);
			t.vel.set(topLine).scale(t.speed);
			
			t.where.g = true;
			
			return delta - t1;
		} else {
//			if(t.type == ThingType.BUTTERFLY) System.out.println(t.vel + "  " + t.pos);
			updatePosAndVel(t);
//			if(t.type == ThingType.BUTTERFLY) System.out.println("2: " + t.vel + "  " + t.pos);
			t.where.g = false;
			return 0;
		}
	}
	public void walkOrLiftOf(Thing t, Vec constantForce, double delta){
		
		Vec topLine = t.link.getTopLine(new Vec()).normalize();
		Vec ortho = topLine.ortho(true);

		//constant Force with walking force minus air friction, nextVel and nextPos get set
		updateForceNextVelAndNextPos(t, constantForce, topLine, delta, true);
//		if(t.type == ThingType.SARAH) System.out.println(t.force);

		calculateSpeed(topLine, ortho, t, delta);
		
		//test if the thing stays on the ground, or if it lifts of
		if(( t.speed == 0 && /*topLine.y >= nextVel.y*/ortho.dot(nextVel) <= 0) ||/*circleCollision.minus(t.pos).y >= nextVel.y*///ortho dot nextVel is <= 0, if nextVel points in the ground
			(t.speed != 0 && circleCollision(t.pos, Math.abs(t.speed*delta), t.speed > 0) && circleCollision.minus(t.pos).ortho(t.speed > 0).dot(nextVel) <= 0 )
		   ){
			if(t.speed == 0){
				t.vel.set(0, 0);
			} else {
				t.pos.set(circleCollision);
				t.vel.set(collisionC.getTopLine(topLine)).setLength(t.speed);
				t.link = collisionC;
			}
			t.where.g = true;
		} else {
//			if(t.type == ThingType.SARAH) System.out.println("Lift of!");
			updatePosAndVel(t);
			t.where.g = false;
		}
	}//-1.7763568394002505E-15
	
	public void updateForceNextVelAndNextPos(Thing t, Vec constantForce, Vec topLine, double delta, boolean applyWalkingForce){
		t.force.set(constantForce);
		checkWater(t.force, t.pos, t);
		if(t.where.water > 0.5)
			t.force.shift(t.walkingForce, 0);
		else if(t.where.g)
			t.force.shift(topLine, t.walkingForce);
		
		nextVel.set(t.vel).shift(t.force, delta/mass);
		applyDynamicFriction(t, nextVel, delta);
		nextVelAvDelta.set((t.vel.x + nextVel.x)/2, (t.vel.y + nextVel.y)/2).scale(delta);
		nextPos.set(t.pos).shift(nextVelAvDelta);
	}
	
	public void updatePosAndVel(Thing t){
		t.pos.set(nextPos);
		t.vel.set(nextVel);		
	}
	
	public void calculateSpeed(Vec topLine, Vec ortho, Thing t, double delta){
		//forces
		double normal = t.force.dot(ortho);
		double downhill = t.force.dot(topLine);//walking force already included from before
		double friction = -(Settings.friction ? 1 : 0)*normal*this.friction*collisionC.vertices[collisionC.collisionVec].mats.read.data.deceleration
//							+ (Settings.airFriction ? 1 : 0)*t.speed*t.speed*airFriction*airea
							;

		t.speed += downhill*delta/mass;
		
		if(t.speed < 0){
			t.speed += friction*delta/mass;
			if(t.speed > 0) t.speed = 0;
		} else if(t.speed > 0){
			t.speed -= friction*delta/mass;
			if(t.speed < 0) t.speed = 0;
		}
		if(t.speed > t.maxWalkingSpeed) t.speed = t.maxWalkingSpeed;
		else if(t.speed < -t.maxWalkingSpeed) t.speed = -t.maxWalkingSpeed;
	}
	
	public double calculateCollisionTime(double dist, double acc, double v0){
		double s1 = -v0;
		double s2 = Math.sqrt(v0*v0 + (2*dist*acc));
		double solution1 = s1 - s2;
		return solution1 >= 0 ? solution1/acc : (s1 + s2)/acc;
	}
	
	public boolean circleCollision(Vec pos1, double r, boolean right){
		Vec vec1 = new Vec(), vec2 = new Vec();
		for(Column c = Main.world.window.leftEnd; c != Main.world.window.rightEnd && c != null; c = c.right){
			Vec[] output = UsefulF.circleIntersection(
					vec1.set(c.xReal, c.vertices[c.collisionVec].y),
					vec2.set(c.right.xReal, c.right.vertices[c.right.collisionVec].y),
					pos1,
					r);
			if(right && output[1] != null){
				collisionC = c;
				circleCollision.set(output[1]);
				return true;
			} else if(!right && output[0] != null){
				collisionC = c;
				circleCollision.set(output[0]);
				return true;
			}
		}
		return false;
	}
	
	Vec collisionVec = new Vec();
	Column collisionC = null;
	public boolean collision(Vec pos1, Vec velt){
		if(coll){
			Vec vec1 = new Vec(), vec2 = new Vec();
			for(Column c = Main.world.window.leftEnd; c != Main.world.window.rightEnd && c != null; c = c.right){
				if(UsefulF.intersectionLines2(
						pos1,
						velt,
						vec1.set(c.xReal, c.vertices[c.collisionVec].y),
						vec2.set(c.right.xReal, c.right.vertices[c.right.collisionVec].y), collisionVec)){
					collisionC = c;
					return true;//can only collide with one vertex
				}
			}
		}
		return false;
	}
	
	public void checkWater(Vec force, Vec pos, Thing t){
		if(t.link.collisionVec != t.link.collisionVecWater && t.link.right.collisionVec != t.link.right.collisionVecWater){
			Vertex waterVertex = t.link.vertices[t.link.collisionVecWater];
			if(waterVertex.y > pos.y + t.box.pos.y){
				t.where.water = Math.min((waterVertex.y - (pos.y + t.box.pos.y))/t.box.size.y, 1);//+20
				force.shift(new Vec(0, waterVertex.mats.read.data.bouyancy*t.where.water*(t.type.physics.airea + (Math.abs(t.walkingForce)/1000))));
			}
		}
	}

	double airTime = 0;
//	
//	public Vertex findWater(Column c, double yMin, double yMax){
//		boolean empty = true;
//		for(int index = 0; index < c.vertices.length; index++){
//			if(c.vertices[index].y > yMin && c.vertices[index].mats.read.data == Material.WATER){
//				
//			}
//		}
//	}
	
	public Vertex findEnclosingMat(Column c, double y){
		Vertex vert = null;
		try {
			int yIndex = -1;
			//get the material the thing is located in
			while(c.vertices[yIndex+1].y > y) yIndex++;
			
			if(yIndex != -1 && !c.vertices[yIndex].mats.empty()) vert = c.vertices[yIndex];
		} catch(IndexOutOfBoundsException e){
			//not being in any material
		}
		return vert;
	}
	
	public void leaveGround(Thing t, Vec vel){
		t.pos.y++;
		t.vel.set(vel);
	}
	
	public void applyForce(Thing t, Vec force){
		t.force.shift(force);
	}
	
	Vec frictionVec = new Vec(), oldVel = new Vec();
	void applyDynamicFriction(Thing t, Vec vel, double delta){
		if(frict){
			if(t.where.water > 0){
				applyFriction(t, vel, -vel.x*Material.WATER.deceleration*airea, -vel.y*Material.WATER.deceleration*airea, delta);
			} else {
				applyFriction(t, vel, -vel.x*Math.abs(vel.x)*Material.AIR.deceleration*airea, -vel.y*Math.abs(vel.y)*Material.AIR.deceleration*airea, delta);
			}
		}
	}
	
	void applyFriction(Thing t, Vec vel, double fricX, double fricY, double delta){
		if(frict){
			oldVel.set(vel);
			frictionVec.set(fricX, fricY);
			//Taylor magic ahead! :D approximating E-function
			vel.shift(frictionVec, 0.5*delta/mass);
			if(vel.x*oldVel.x < 0) vel.x = 0;
			if(vel.y*oldVel.y < 0){
				vel.y = 0;
			}
		}
	}
	
	public static class Where {
		public boolean g;
		public double water;
	}
}
