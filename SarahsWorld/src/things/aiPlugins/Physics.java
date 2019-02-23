package things.aiPlugins;

import main.Main;
import menu.Settings;
import things.AiPlugin;
import things.Thing;
import util.math.UsefulF;
import util.math.Vec;
import world.data.Column;
import world.data.ColumnListElement;
import world.data.Vertex;
import world.generation.Material;

public class Physics extends AiPlugin {
	
	static double dt = 0.010;
	public static double lowestSpeed = 0.1;
	
	public static double jump = 500;
	public static float grassFriction = 1;
	public static Vec gravity = new Vec(0, -981);
	public static double airFriction = 0.001;
	
	public double mass = 1;
	public double airea = 1;//for air friction
	public double friction = 0.5, stiction = 0.01;
	public boolean frict, grav, coll, water, walk, stickToGround;

	/**
	 * Should generally be one of the last plugins to update, so that other forces can come to effect
	 * @param mass
	 * @param airea
	 */
	public Physics(double mass, double airea, boolean frict, boolean grav, boolean coll, boolean water, boolean walk, boolean stickToGround) {
		this.mass = mass;
		this.airea = airea;
		this.frict = frict;
		this.grav = grav;
		this.coll = coll;
		this.water = water;
		this.walk = walk;
		this.stickToGround = stickToGround;
	}
	
	public Physics(double mass, double airea){
		this(mass, airea, true, true, true, true, true, false);
	}
	public Physics(double mass, double airea, boolean walk){
		this(mass, airea, true, true, true, true, walk, false);
	}
	
	public void update(Thing t, double delta){
		if(Settings.getBoolean("FREEZE")){
			return;
		}
		
		Vec constantForce = t.force.copy();
		if(t.where.water > 0.5){
			
		}
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
				if(t.where.g && walk)
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
				if(t.airTime > 0.5 && t.type.movement != null)
					t.type.movement.land(t);
				t.reallyAir = false;
				t.airTime = 0;
			} else {
				t.airTime += delta;
				if(!t.reallyAir && t.airTime > 0.4){
					t.reallyAir = true;
					if(t.type.movement != null)
						t.aniPlug.setAnimation(t.type.movement.fly);
				}
			}
		}
		//UPDATE ROTATION
		updateRotation(t, delta);
		
		//UPDATE WORLD LINK

		int column = (int)Math.floor(t.pos.x/Column.COLUMN_WIDTH);
		while(t.newLink.getIndex() < column && t.newLink.right() != null) t.newLink = t.newLink.right().column();
		while(t.newLink.getIndex() > column && t.newLink.left() != null) t.newLink = t.newLink.left().column();
	}
	
	public void updateRotation(Thing t, double delta){
		t.yOffsetToBalanceRotation = 0;
		t.rotation = 0;
		if(!t.where.g){
			if(t.where.water > 0.3){
//				Vec ori = t.vel.copy();
//				double speed = ori.length();
//				ori.shift(0, 70);
//				t.rotation = Math.atan2(ori.x, ori.y);
//				t.yOffsetToBalanceRotation = 0.65*t.box.size.y*(1-Math.cos(t.rotation));
//				double v = t.vel.length();
//				if(v > 10 && (t.force.y != 0 || t.force.x != 0)){
//					if(t.vel.dot(t.orientation) >= 0){
//						t.orientation.set(t.vel).shift(1, 0);
//					} else {
//						t.orientation.set(t.vel).scale(-1).shift(1, 0);
//					}
//					t.rotation = t.orientation.angle() + Math.PI/2;
//
//					t.rotation = Math.atan2(t.vel.x, t.vel.y);
//				} else {
//					t.rotation = 0;
//				}
			} else if(t.willLandInWater){
				t.rotation = Math.atan2(t.vel.x, t.vel.y);
			}
		} else if(t.where.g){
			if(stickToGround){
				t.rotation = -t.newLink.getCollisionLine(new Vec()).angle();
			} else {
				t.rotation = 0;
			}
		}
	}
	
	public double angleDist(double a, double b){
		double d = Math.abs(a - b) % (2*Math.PI); 
		double r = d > Math.PI ? 2*Math.PI - d : d;

		//calculate sign 
		int sign = (a - b >= 0 && a - b <= Math.PI) || (a - b <=-Math.PI && a- b>= -2*Math.PI) ? 1 : -1; 
		r *= sign;
		return r;
	}
	
	Vec circleCollision = new Vec();
	public double freeFallOrCollision(Thing t, Vec constantForce, double delta){
		//constant Force minus air friction, nextVel and nextPos get set
		updateForceNextVelAndNextPos(t, constantForce, null, delta, true);
		if(collision(t, t.pos, t.nextVelAvDelta)){
//			if(t.type == ThingType.SARAH) System.out.println("Collision! " + collisionVec);
			double t1 = calculateCollisionTime(collisionVec.minus(t.pos).length(), t.force.length()/mass, t.vel.length());
			Vec topLine = t.collisionC.getCollisionLine(new Vec()).normalize();
			updateForceNextVelAndNextPos(t, constantForce, null, t1, true);
			t.pos.set(collisionVec);
			t.newLink = t.collisionC;
			t.willLandInWater = false;
			
			t.speed = t.nextVel.dot(topLine);
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
		
		Vec topLine = t.newLink.getCollisionLine(new Vec()).normalize();
		Vec ortho = topLine.ortho(true);

		//constant Force with walking force minus air friction, nextVel and nextPos get set
		updateForceNextVelAndNextPos(t, constantForce, topLine, delta, true);
//		if(t.type == ThingType.SARAH) System.out.println(t.force);

		calculateSpeed(topLine, ortho, t, delta);
		
		//test if the thing stays on the ground, or if it lifts of
		if(( t.speed == 0 && /*topLine.y >= nextVel.y*/ortho.dot(t.nextVel) <= 0) ||/*circleCollision.minus(t.pos).y >= nextVel.y*///ortho dot nextVel is <= 0, if nextVel points in the ground
			(t.speed != 0 && circleCollision(t, t.pos, Math.abs(t.speed*delta), t.speed > 0) && circleCollision.minus(t.pos).ortho(t.speed > 0).dot(t.nextVel) <= 0 )
		   ){
			if(t.speed == 0){
				t.vel.set(0, 0);
			} else {
				t.pos.set(circleCollision);
				t.vel.set(t.collisionC.getCollisionLine(topLine)).setLength(t.speed);
				t.newLink = t.collisionC;
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
		if(t.where.g)
			t.force.shift(topLine, t.walkingForce);
		else if(t.where.water > 0.5)
			t.force.shift(t.walkingForce, 0);
		
		t.nextVel.set(t.vel).shift(t.force, delta/mass);
		applyDynamicFriction(t, t.nextVel, delta);
		t.nextVelAvDelta.set((t.vel.x + t.nextVel.x)/2, (t.vel.y + t.nextVel.y)/2).scale(delta);
		t.nextPos.set(t.pos).shift(t.nextVelAvDelta);
	}
	
	public void updatePosAndVel(Thing t){
		t.pos.set(t.nextPos);
		t.vel.set(t.nextVel);		
	}
	
	public void calculateSpeed(Vec topLine, Vec ortho, Thing t, double delta){
		//forces
		double normal = t.force.dot(ortho);
		double downhill = t.force.dot(topLine);//walking force already included from before
		double friction = -(Settings.friction ? 1 : 0)*normal*this.friction*t.collisionC.getTopSolidVertex().getAverageDeceleration();
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
	
	public boolean circleCollision(Thing t, Vec pos1, double r, boolean right){
		Vec vec1 = new Vec(), vec2 = new Vec();
		for(ColumnListElement c = Main.world.landscapeWindow.start(); c.next() != Main.world.landscapeWindow.end(); c = c.next()){
			Vec[] output = UsefulF.circleIntersection(
					vec1.set(c.column().getX(), c.column().getCollisionY()),
					vec2.set(c.right().column().getX(), c.right().column().getCollisionY()),
					pos1,
					r);
			if(right && output[1] != null){
				t.collisionC = c.column();
				circleCollision.set(output[1]);
				return true;
			} else if(!right && output[0] != null){
				t.collisionC = c.column();
				circleCollision.set(output[0]);
				return true;
			}
		}
		return false;
	}
	
	Vec collisionVec = new Vec();
	public boolean collision(Thing t, Vec pos1, Vec velt){
		if(coll){
			Vec vec1 = new Vec(), vec2 = new Vec();

			for(ColumnListElement c = Main.world.landscapeWindow.start(); c.next() != Main.world.landscapeWindow.end(); c = c.next()){
				if(UsefulF.intersectionLines2(
						pos1,
						velt,
						vec1.set(c.column().getX(), c.column().getCollisionY()),
						vec2.set(c.right().column().getX(), c.right().column().getCollisionY()), collisionVec)){
					t.collisionC = c.column();
					return true;//can only collide with one vertex
				}
			}
		}
		return false;
	}
	
	/**
	 * Look how deep the thing is inside the water and calculate the bouyancy from that
	 * @param force
	 * @param pos
	 * @param t
	 */
	public void checkWater(Vec force, Vec pos, Thing t){
		t.buoyancyForce = 0;
		//check, if the column the thing is in contains water:
		if(t.newLink.getTopSolidVertex() != t.newLink.getTopFluidVertex() && t.newLink.right() != null && t.newLink.right().column().getTopSolidVertex() != t.newLink.right().column().getTopFluidVertex()){
			//get vertex of water surface
			Vertex waterVertex = t.newLink.getTopFluidVertex();

			//check if at least part of the thing is under water
			if(waterVertex.y() > pos.y + t.aniPlug.getRenderBox().pos.y){
				//calculate how deep the thing is in the water in units of it's height
				t.where.water = Math.min((waterVertex.y() - (pos.y + t.aniPlug.getRenderBox().pos.y))/t.aniPlug.getRenderBox().size.y, 1);//+20
				//apply a buoyancy force
				t.buoyancyForce = waterVertex.getAverageBouyancy()*t.where.water*(t.type.physics.airea/* + (Math.abs(t.walkingForce)/1000)*/);
				force.shift(0, t.buoyancyForce);
				
			}
		}
	}
//	
//	public Vertex findWater(Column c, double yMin, double yMax){
//		boolean empty = true;
//		for(int index = 0; index < c.vertices.length; index++){
//			if(c.vertices(index).y > yMin && c.vertices(index).mats.read.data == Material.WATER){
//				
//			}
//		}
//	}
	
	public Vertex findEnclosingMat(Column c, double y){
		Vertex vert = null;
		try {
			int yIndex = -1;
			//get the material the thing is located in
			while(c.vertices(yIndex+1).y() > y) yIndex++;
			
			if(yIndex != -1 && !c.vertices(yIndex).empty()) vert = c.vertices(yIndex);
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
