package world.things.newPlugins;

import main.Main;
import util.math.UsefulF;
import util.math.Vec;
import world.WorldData.Column;
import world.WorldData.Vertex;
import world.things.AiPlugin;
import world.things.ThingProps;

public class Physics extends AiPlugin{

	public static Vec gravity = new Vec(0, -981);
	public static double airFriction = 1;
	
	public double mass;
	public double airea;//for air friction
	public double friction = 1, stiction = 1.5;
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
	Vec nextVelAv = new Vec();
	Vec nextPos = new Vec();
	
	boolean collision, collisionTotal;
	
	static double dt = 0.005;
	public void update(ThingProps t, double delta){
		//POINT 0
		Vec clearForce = t.force.copy();
		applyEnvironmentalForces(t.force, t.vel, t.pos, t);
		
		collisionTotal = false;
		while(delta > dt){
			//POINTS IN BETWEEN
			step(t, clearForce, t.force, t.vel, t.pos, dt, true);
			delta -= dt;
		}
		//LAST POINT WITH THE REST OF delta
		step(t, clearForce, t.force, t.vel, t.pos, delta, true);
		
		if(collisionTotal){
			if(t.where == Where.AIR && t.type.ground != null)
				t.type.ground.land(t);
			t.where = Where.GROUND;
		} else {
			t.where = Where.AIR;
		}

		Column below = Main.world.window.get((int)(t.pos.x/Column.step));
		t.link = below.vertices[below.collisionVec];
		t.link.parent.add(t);
		
		t.force.set(0, 0);//reset force
	}
	
	public void step(ThingProps t, Vec clearForce, Vec lastForce, Vec vel, Vec pos, double delta, boolean collisionPossible){
		
		approxNext(t, vel, pos, lastForce, clearForce, delta);

		//Test, if the thing collides with the ground
		Vec collisionVec = new Vec(); Column c = null;
		collision = false;
		if(coll && nextVelAv.lengthSquare() != 0){
			Vec vec1 = new Vec(), vec2 = new Vec();
			for(c = Main.world.window.leftEnd; c != Main.world.window.rightEnd && c != null; c = c.right){
				collision = UsefulF.intersectionLines(
						pos,
						nextPos,
						vec1.set(c.xReal, c.vertices[c.collisionVec].y),
						vec2.set(c.right.xReal, c.right.vertices[c.right.collisionVec].y), collisionVec);
				if(collision) break;//can only collide with one vertex
			}
		}
		if(collision){
			collisionTotal = true;
			double t1 = nextVelAv.x != 0 ? (pos.x - collisionVec.x)/nextVelAv.x : (pos.y - collisionVec.y)/nextVelAv.y;
			
			approxNext(t, vel, pos, lastForce, clearForce, t1);
			
			vel.set(nextVel);
			pos.set(nextPos);
			lastForce.set(approxNextForce);
			//the thing is now officially at the point of collision
			
			//calculate collision forces and velocities
			Vec topLine = c.getTopLine(new Vec()).normalize();
			Vec ortho = topLine.ortho(false);
			
			//speed parallel to the ground (component of vel, the other (normal) component would go into the ground and deform it))
			double speed = t.vel.dot(topLine);
			vel.set(topLine).scale(speed);
			
			//forces
			double normal = lastForce.dot(ortho);
			double downhill = lastForce.dot(topLine) + t.walkingForce;
			double stiction = normal*this.stiction*c.vertices[c.collisionVec].mats.read.data.deceleration;
			double friction = normal*this.friction*c.vertices[c.collisionVec].mats.read.data.deceleration;
			if(UsefulF.equalsApprox(speed, 0, 0.05)){
				if(downhill <= stiction){
					downhill = 0;
				} else {
					downhill -= friction;
				}
			} else {
				downhill -= friction;
			}
			lastForce.set(topLine).scale(downhill);
			applyEnvironmentalForces(lastForce, vel, pos, t);
			
			delta -= t1;//shorten delta a bit, then recover the state from before the collision and continue as if nothing happened
			approxNext(t, vel, pos, lastForce, clearForce, delta);
		}
		vel.set(nextVel);
		pos.set(nextPos);
		lastForce.set(approxNextForce);
		//the thing has now officially passed dt
	}
	
	public void approxNext(ThingProps t, Vec vel, Vec pos, Vec force, Vec clearForce, double delta){
		approxNextVel.set(vel).shift(force, delta/mass);
		approxNextAvVel.set(vel).shift(force, 0.5*delta/mass);
		approxNextPos.set(pos).shift(approxNextAvVel, delta);
		approxNextForce.set(clearForce);
		applyEnvironmentalForces(approxNextForce, approxNextVel, approxNextPos, t);

		averageForce.set((force.x + approxNextForce.x)/2, (force.y + approxNextForce.y)/2);
		nextVel.set(vel).shift(averageForce, delta/mass);
		nextVelAv.set(vel).shift(averageForce, 0.5*delta/mass);
		nextPos.set(pos).shift(nextVelAv);
		
	}
	
	/**
	 * Has to be called after every external force is added to the things total force
	 * @param t
	 * @param delta
	 */
	public void update1(ThingProps t, double delta){
		
		t.noFricForce.set(t.force);//save the frictionless force for later
		applyEnvironmentalForces(t.force, t.vel, t.pos, t);
		
		t.velAv.set(t.vel).shift(t.force, 0.5*delta/mass);
		t.nextPos.set(t.pos).shift(t.velAv, delta);
		collision = false;
		if(coll && t.velAv.lengthSquare() != 0){
			Vec vec1 = new Vec(), vec2 = new Vec(), intersection = new Vec();Column c = null;
			for(c = Main.world.window.leftEnd; c != Main.world.window.rightEnd && c != null; c = c.right){
				collision = UsefulF.intersectionLines(
						t.pos,
						t.nextPos,
						vec1.set(c.xReal, c.vertices[c.collisionVec].y),
						vec2.set(c.right.xReal, c.right.vertices[c.right.collisionVec].y), intersection);
				if(collision) break;//can only collide with one vertex
			}
				
			if(collision){
				double t1 = t.velAv.x != 0 ? (t.pos.x - intersection.x)/t.velAv.x : (t.pos.y - intersection.y)/t.velAv.y;
				
				t.pos.set(intersection.shift(0, 0.05));//position is the point of collision for now
				t.vel.shift(t.force, t1/mass);//velocity on impact
				t.force.set(t.noFricForce);//friction gets added again later with the new speed
				
				double t2 = delta - t1;
				Vec topLine = c.getTopLine(new Vec()).normalize();
				Vec ortho = topLine.ortho(false);
				
				//VELOCITY AT COLLISION
				double speed = t.vel.dot(topLine);
				
				t.vel.set(topLine).scale(speed);//initial velocity on collision
				
				//FORCES AT COLLISION
				double n = t.force.dot(ortho);//normal force   ---- normally you'd've to divide by the length of the orthogonal/top line, but these are normalized
				double d = t.force.dot(topLine);//downhill force
				double f = n*c.vertices[c.collisionVec].mats.read.data.deceleration*friction;// ground friction = normal force * friction coefficient, which is always positive
				if(speed > 0)
					d -= f;
				else
					d += f;
				
				t.force.set(topLine).scale(d);//friction is included
				
				t.force.shift(topLine, t.walkingForce);//add acceleration from walking
				
				//Update vel, force and pos after the collision
				applyAirFriction(t.force, t.vel);//compute air friction based on new velocity
				
				t.velAv.set(t.vel).shift(t.force, 0.5*t2/mass);//calculate the average velocity and the end velocity of this frame
				t.vel.shift(t.force, t2/mass);
				
				t.pos.shift(t.velAv, t2);
				
				if(t.where == Where.AIR && t.type.ground != null){
					t.type.ground.land(t);
				}
				t.where = Where.GROUND;
			}
		}
		if(!collision){
			//No collision:
			t.vel.shift(t.force, delta/mass);
			t.pos.set(t.nextPos);
			t.where = Where.AIR;
		}
		
		Column below = Main.world.window.get((int)(t.pos.x/Column.step));
		t.link = below.vertices[below.collisionVec];
		t.link.parent.add(t);
		
		t.force.set(0, 0);//reset force
		t.nextPos.set(t.pos).shift(t.vel, delta);//very rough approximation of the position in the next frame
	}
	
	/**
	 * only changes the input force
	 * @param force
	 * @param vel
	 * @param pos
	 * @param t
	 * @param delta
	 */
	void applyEnvironmentalForces(Vec force, Vec vel, Vec pos, ThingProps t){
		if(grav)
			force.shift(gravity, mass);//apply gravity force
		
		if(water)
			checkWater(force, pos, t);//apply uplifting force if in water
		
		if(frict)
			applyAirFriction(force, vel);
			
	}
	
	void checkWater(Vec force, Vec pos, ThingProps t){
		Vertex v1 = findEnclosingMat(t.link.parent, pos.y + t.box.pos.y);//below feet
//		Vertex v2 = findEnclosingMat(t.link.parent, t.pos.y + t.type.ani.box.pos.y + t.type.ani.box.size.y);//above head
		
		if(v1 != null && v1.mats.read.data.solidity == 1){
			force.shift(new Vec(0, v1.mats.read.data.bouyancy*(v1.y - (pos.y + t.box.pos.y))/t.box.size.y));
			t.where = Where.WATER;
		}
	}
	
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
	
	public void leaveGround(ThingProps t, Vec vel){
		t.pos.y++;
		t.vel.set(vel);
	}
	
	public void applyForce(ThingProps t, Vec force){
		t.force.shift(force);
	}
	
	void applyAirFriction(Vec force, Vec vel){
		if(frict)
			force.shift(-vel.x*Math.abs(vel.x)*airFriction*airea, -vel.y*Math.abs(vel.y)*airFriction*airea);
	}
	
	public static enum Where {GROUND, WATER, AIR}
}
