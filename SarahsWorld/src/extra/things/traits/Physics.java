package extra.things.traits;

import basis.entities.Trait;
import extra.Main;
import extra.things.Thing;
import extra.things.ThingAttribute;
import basis.entities.Entity;
import menu.Settings;
import util.math.UsefulF;
import util.math.Vec;
import world.data.Column;
import world.data.ColumnListElement;
import world.data.Vertex;
import world.generation.Material;

public class Physics extends Trait {
	
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
	
	/**
	 * Is called when the thing collides with the terrain. To be overridden
	 * @param thing
	 * @return should return true, if the physics calculation should be stopped (e.g. because the meteor exploded).
	 */
	protected boolean onCollision(Entity thing, Vec collisionVec) { return false;}
	
	@Override
	public PhysicsPlugin createAttribute(Entity thing) {
		return new PhysicsPlugin(thing);
	}
	
	public class PhysicsPlugin extends ThingAttribute {
		
		private Vec vel = new Vec(),
				nextPos = new Vec(),
				nextVelAvDelta = new Vec(),
				nextVel = new Vec(),
				force = new Vec();
		
		private Column collisionC = null;
		public double walkingForce, speed, maxWalkingSpeed, buoyancyForce;
		private Where where = new Where();

		public PhysicsPlugin(Entity thing) {
			super(thing);
		}

	//PUBLIC FUNCTIONS
		public void setWalkingForce(double F) {					walkingForce = F;		}
		public void setMaxWalkingSpeed(double mWS) {			maxWalkingSpeed = mWS;		}
		public void setNotOnGround() {							where.g = false; }
		public void multiplyMaxWalkingSpeed(double factor) {	maxWalkingSpeed *= factor;		}
		
		public boolean onGround() {								return where.g; }
		public double waterDepth() {							return where.water; }
		
		public void copyWhere(Thing from) {
			this.where.g = from.physicsPlug.onGround();
			this.where.water = from.physicsPlug.waterDepth();
		}
		
		public void leaveGround(Vec vel){ leaveGround(vel.x, vel.y); }
		public void leaveGround(double velX, double velY){
			thing.pos.y++;
			this.vel.set(velX, velY);
			where.g = false;
		}

		public void applyForce(Vec force){ applyForce(force.x, force.y); }
		public void applyForce(double Fx, double Fy){
			force.shift(Fx, Fy);
		}
		
		@Override
		public void update(double delta){
			if(Settings.getBoolean("FREEZE")){
				return;
			}
			
			Vec constantForce = force.copy();
			if(where.water > 0.5){
				
			}
			where.water = 0;
//			t.g = false;
			
			{//COLLISION TESTING-------------------------------------------O
				//Test for collision in time intervals of dt=0.01s length
				int steps = (int)(delta/dt);
				double step = delta/steps;
				for(int i = 0; i < steps; i++){
					double time = step;
					
					//if not on ground, do free fall or collide with the ground
					if(!thing.physicsPlug.onGround())
						time = freeFallOrCollision( constantForce, time);
					
					//if on ground now, walk on it or lift of, if momentum is too high
					if(thing.physicsPlug.onGround() && walk)
						walkOrLiftOf( constantForce, time);
				}
					
			}
			
			
			{//RESET FORCE-------------------------------------------------O
				//reset force to gravity only
				if(grav)
					force.set(gravity).scale(mass);
				else
					force.set(0, 0);
			}
			
			//UPDATE ROTATION
			updateRotation(delta);
			
			//UPDATE WORLD LINK
			int column = (int)Math.floor(thing.pos.x/Column.COLUMN_WIDTH);
			while(thing.newLink.getIndex() < column && thing.newLink.right() != null) thing.newLink = thing.newLink.right().column();
			while(thing.newLink.getIndex() > column && thing.newLink.left() != null) thing.newLink = thing.newLink.left().column();
		}
		
	//PRIVATE FUNCTIONS
		
		/**
		 * Updates the rotational angle of the thing according to physical effects. Does nothing at the moment.
		 * @param delta
		 */
		private void updateRotation(double delta){}
		
		private void updatePosVelG_FreeFalling() {

			updatePosAndVel();
			where.g = false;
		}
		
		private void updatePosVelLinkG_Walking(double speed, Collision collision) {
			if(speed == 0){
				vel.set(0, 0);
			} else {
				thing.pos.set(collision.position);
				vel.set(collision.column.getCollisionLine()).setLength(speed);
				thing.newLink = collision.column;
			}
			where.g = true;
		}
		
		/**
		 * Calculates the things position and velocity after the time delta, or at the point in time when it hits the ground.
		 * @param constantForce Forces that are constant during the evaluation of physics, e.g. walking force or gravity
		 * @param delta
		 * @return The time remaining from delta when colliding, zero otherwise
		 */
		private double freeFallOrCollision(Vec constantForce, double delta){
			//constant Force minus air friction, nextVel and nextPos get set
			updateForceNextVelAndNextPos( constantForce, null, delta, true);
			
			//test for collisions
			Collision collision = collision( thing.pos, nextVelAvDelta); 
			
			if(collision != null){ //thing collided with the ground -> start walking
				
				//calculate time of collision, set the thing to that exact location 
				double t1 = calculateCollisionTime(collision.position.minus(thing.pos).length(), force.length()/mass, vel.length());
				
				//calculate next pos and next vel at the place and time of collision
				updateForceNextVelAndNextPos( constantForce, null, t1, true);

				//calculate the tangential speed
				Vec topLine = collision.column.getCollisionLine(new Vec()).normalize();
				speed = nextVel.dot(topLine);
				
				//go to the location of impact and update the state variables in walking mode
				updatePosVelLinkG_Walking(speed, collision);
				
				//call the user function onCollision (it may stop the calculations)
				if(onCollision(thing, collision.position.copy()))
					return 0;
				
				else
					//return the unused time to walk during it
					return delta - t1;
			} else {

				//update the state variables in free falling mode
				updatePosVelG_FreeFalling();
				return 0;
			}
		}
		
		private void walkOrLiftOf(Vec constantForce, double delta){
			
			Vec topLine = thing.newLink.getCollisionLine(new Vec()).normalize();
			Vec ortho = topLine.ortho(true);

			//constant Force with walking force minus air friction, nextVel and nextPos get set
			updateForceNextVelAndNextPos( constantForce, topLine, delta, true);

			calculateSpeed(topLine, ortho, delta);
			
			Collision collision = null;
			if(speed != 0) {
				collision = circleCollision(thing.pos, Math.abs(speed*delta), speed > 0);
				if(collision != null)
					ortho = collision.position.minus(thing.pos).ortho(speed > 0);
			} else {
				//check if the terrain below changed
				collision = standingCollision();
			}
			
			//if the thing stays on the ground, walk! ortho dot nextVel is <= 0, if nextVel points in the ground
			if(( collision != null && ortho.dot(nextVel) <= 0)){	
				
				updatePosVelLinkG_Walking(speed, collision);
				
			} else {//if it lifts of, fall freely!
				
				updatePosVelG_FreeFalling();
			}
		}
		

		private void updateForceNextVelAndNextPos(Vec constantForce, Vec topLine, double delta, boolean applyWalkingForce){
			force.set(constantForce);
			checkWater(force, thing.pos);
			if(where.g)
				force.shift(topLine, walkingForce);
			else if(where.water > 0.5)
				force.shift(walkingForce, 0);
			
			nextVel.set(vel).shift(force, delta/mass);
			applyDynamicFriction( nextVel, delta);
			nextVelAvDelta.set((vel.x + nextVel.x)/2, (vel.y + nextVel.y)/2).scale(delta);
			nextPos.set(thing.pos).shift(nextVelAvDelta);
		}
		
		private void updatePosAndVel(){
			thing.pos.set(nextPos);
			vel.set(nextVel);		
		}
		
		private void calculateSpeed(Vec topLine, Vec ortho, double delta){
			//forces
			double normal = force.dot(ortho);
			double downhill = force.dot(topLine);//walking force already included from before
			double friction = -(Settings.getBoolean("FRICTION") ? 1 : 0)*normal*Physics.this.friction*thing.newLink.getTopSolidVertex().getAverageDeceleration();
//								+ (Settings.airFriction ? 1 : 0)*t.speed*t.speed*airFriction*airea
								;

			speed += downhill*delta/mass;
			
			if(speed < 0){
				speed += friction*delta/mass;
				if(speed > 0) speed = 0;
			} else if(speed > 0){
				speed -= friction*delta/mass;
				if(speed < 0) speed = 0;
			}
			if(speed > maxWalkingSpeed) speed = maxWalkingSpeed;
			else if(speed < -maxWalkingSpeed) speed = -maxWalkingSpeed;
		}
		
		private class Collision {
			Vec position;
			Column column;
			
			public Collision(Vec position, Column column) {
				this.position = position;
				this.column = column;
			}
		}
		
		private Collision circleCollision(Vec pos1, double r, boolean right){
			Vec vec1 = new Vec(), vec2 = new Vec();
			for(ColumnListElement c = Main.game().world.landscapeWindow.start(); c.next() != Main.game().world.landscapeWindow.end(); c = c.next()){
				Vec[] output = UsefulF.circleIntersection(
						vec1.set(c.column().getX(), c.column().getCollisionY()),
						vec2.set(c.right().column().getX(), c.right().column().getCollisionY()),
						pos1,
						r);
				if(right && output[1] != null){
					return new Collision(output[1], c.column());
				} else if(!right && output[0] != null){
					return new Collision(output[0], c.column());
				}
			}
			return null;
		}
		
		private Collision collision(Vec pos1, Vec velt){
			if(coll){
				Vec vec1 = new Vec(), vec2 = new Vec(), collisionVec = new Vec();

				for(ColumnListElement c = Main.game().world.landscapeWindow.start(); c.next() != Main.game().world.landscapeWindow.end(); c = c.next()){
					if(UsefulF.intersectionLines2(
							pos1,
							velt,
							vec1.set(c.column().getX(), c.column().getCollisionY()),
							vec2.set(c.right().column().getX(), c.right().column().getCollisionY()), collisionVec)){
						return new Collision(collisionVec, c.column());//can only collide with one vertex
					}
				}
			}
			return null;
		}
		
		/**
		 * Checks whether the thing should still be standing by shifting it up a bit, let it fall on it's link column and check it falls approximately the right distance (+-0.1)
		 * @param pos
		 * @param c
		 * @return
		 */
		private Collision standingCollision() {
			Vec pos1 = thing.pos.copy().shift(0, 0.1);
			Vec diff = new Vec(0, -0.2);
			Vec vec1 = new Vec(thing.newLink.getX(), thing.newLink.getCollisionY());
			Vec vec2 = new Vec(thing.newLink.right().getX(), thing.newLink.right().getCollisionY());
			Vec collisionVec = new Vec();
			if(UsefulF.intersectionLines2(pos1, diff, vec1, vec2, collisionVec)) {
				return new Collision(thing.pos, thing.newLink);
			}
			return null;
		}
		
		/**
		 * Look how deep the thing is inside the water and calculate the bouyancy from that
		 * @param force
		 * @param pos
		 */
		private void checkWater(Vec force, Vec pos){
			buoyancyForce = 0;
			//check, if the column the thing is in contains water:
			if(thing.aniPlug != null && thing.newLink.getTopSolidVertex() != thing.newLink.getTopFluidVertex() && thing.newLink.right() != null && thing.newLink.right().column().getTopSolidVertex() != thing.newLink.right().column().getTopFluidVertex()){
				//get vertex of water surface
				Vertex waterVertex = thing.newLink.getTopFluidVertex();

				//check if at least part of the thing is under water
				if(waterVertex.y() > pos.y + thing.aniPlug.getRenderBox().pos.y){
					//calculate how deep the thing is in the water in units of it's height
					where.water = Math.min((waterVertex.y() - (pos.y + thing.aniPlug.getRenderBox().pos.y))/thing.aniPlug.getRenderBox().size.y, 1);//+20
					//apply a buoyancy force
					buoyancyForce = waterVertex.getAverageBouyancy()*where.water*(thing.type.physics.airea/* + (Math.abs(t.walkingForce)/1000)*/);
					force.shift(0, buoyancyForce);
					
				}
			}
		}
		
		Vec frictionVec = new Vec(), oldVel = new Vec();
		private void applyDynamicFriction(Vec vel, double delta){
			if(frict){
				if(where.water > 0){
					applyFriction(vel, -vel.x*Material.WATER.deceleration*airea, -vel.y*Material.WATER.deceleration*airea, delta);
				} else {
					applyFriction( vel, -vel.x*Math.abs(vel.x)*Material.AIR.deceleration*airea, -vel.y*Math.abs(vel.y)*Material.AIR.deceleration*airea, delta);
				}
			}
		}
		
		private void applyFriction(Vec vel, double fricX, double fricY, double delta){
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

	//Getters
		public double	velX() {								return vel.x; }
		public double	velY() {								return vel.y; }
		public String	forceString() {							return force.toString(); }
		public String	velString() {							return vel.toString(); }
	//Setters
		public void		setVel(Vec vel) {						setVel(vel.x, vel.y); }
		public void		setVel(double velX, double velY) {		this.vel.set(velX, velY); }		
	}
	
	public double angleDist(double a, double b){
		double d = Math.abs(a - b) % (2*Math.PI); 
		double r = d > Math.PI ? 2*Math.PI - d : d;

		//calculate sign 
		int sign = (a - b >= 0 && a - b <= Math.PI) || (a - b <=-Math.PI && a- b>= -2*Math.PI) ? 1 : -1; 
		r *= sign;
		return r;
	}
	
	public double calculateCollisionTime(double dist, double acc, double v0){
		double s1 = -v0;
		double s2 = Math.sqrt(v0*v0 + (2*dist*acc));
		double solution1 = s1 - s2;
		return solution1 >= 0 ? solution1/acc : (s1 + s2)/acc;
	}
	
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
	
	public static class Where {
		public boolean g;
		public double water;
	}
}
