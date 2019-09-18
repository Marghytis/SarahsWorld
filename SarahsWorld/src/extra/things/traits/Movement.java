package extra.things.traits;

import basis.entities.Entity;
import basis.entities.Trait;
import extra.Main;
import extra.things.ThingAttribute;
import menu.Settings.Key;
import util.math.Function;
import util.math.Vec;
import world.data.Column;



public class Movement extends Trait {
	public static enum Location {
		GROUND {
			@Override
			public void transitionTo(Location loc) {
				switch(loc) {
				case GROUND:
				case WATER:
				case AIR:
				}
			}
		}, WATER {
			@Override
			public void transitionTo(Location loc) {
				switch(loc) {
				case GROUND:
				case WATER:
				case AIR:
				}
			}
		}, AIR {
			@Override
			public void transitionTo(Location loc) {
				switch(loc) {
				case GROUND:
				case WATER:
				case AIR:
				}
			}
		};
		
		private double acceleration;
		
		public abstract void transitionTo(Location loc);
	}
	
	public String stand, sneak, walk1, walk2, fly, plunge, swim, sneakyStand;
	//			  W->W   W->W   W->W   W->W   F->F P->P    S->S  W->W
	public String liftOf, land, dive;
	
	private double defaultAccWalking = 1000, defaultAccSwimming = 250, defaultAccFlying = 0;
	private boolean keepOrthoToFloor;

	public Movement(String stand, String walk0, String walk1, String walk2, String swim, String jump, String land, String fly, String dive, String plunge, String sneakyStand, double defaultAccWalking, double defalutAccSwimming, double defaultAccFlying){
		this(stand, walk0, walk1, walk2, swim, jump, land, fly, dive, plunge, sneakyStand);
		this.defaultAccWalking = defaultAccWalking;
		this.defaultAccSwimming = defalutAccSwimming;
		this.defaultAccFlying = defaultAccFlying;
	}
	
	public Movement(String stand, String walk0, String walk1, String walk2, String swim, String jump, String land, String fly, String dive, String plunge, String sneakyStand){
		this.stand = stand;
		this.sneak = walk0;
		this.walk1 = walk1;
		this.walk2 = walk2;
		this.swim = swim;
		this.liftOf = jump;
		this.land = land;
		this.fly = fly;
		this.dive = dive;
		this.plunge = plunge;
		this.sneakyStand = sneakyStand;
	}
	
	/**
	 * Pipe command to reduce constructor
	 * @return this same Movement
	 */
	public Movement keepOrthoToFloor() {
		this.keepOrthoToFloor = true;
		return this;
	}
	
	@Override
	public MovePlugin createAttribute(Entity thing) {
		return new MovePlugin(thing, defaultAccWalking, defaultAccSwimming, defaultAccFlying);
	}
	
	public class MovePlugin extends ThingAttribute {
		
		private double accWalking, accSwimming, accFlying;
		private double airTime;
		private double movementRotation, yOffsetToBalanceRotation;
		
		private boolean reallyAir;
		private boolean willLandInWater;

		private String backgroundAnimation = "";

		public MovePlugin(Entity thing, double accWalking, double accSwimming, double accFlying) {
			super(thing);
			this.accWalking = accWalking;
			this.accSwimming = accSwimming;
			this.accFlying = accFlying;
		}
		
		@Override
		public void update(double delta) {

			if(thing.physicsPlug.onGround()){
				if(airTime > 0.5 && thing.type.movement != null)
					land();
				reallyAir = false;
				airTime = 0;
			} else {
				airTime += delta;
				if(!reallyAir && airTime > 0.4){
					reallyAir = true;
					if(thing.type.movement != null)
						thing.aniPlug.setAnimation(thing.type.movement.fly);
				}
			}
			
			updateRotation();
		}
		
		private void updateRotation() {
			yOffsetToBalanceRotation = 0;
			movementRotation = 0;
			if(!thing.physicsPlug.onGround()){
				if(thing.physicsPlug.waterDepth() > 0.3){
//					Vec ori = t.vel.copy();
//					double speed = ori.length();
//					ori.shift(0, 70);
//					t.rotation = Math.atan2(ori.x, ori.y);
//					t.yOffsetToBalanceRotation = 0.65*t.box.size.y*(1-Math.cos(t.rotation));
//					double v = t.vel.length();
//					if(v > 10 && (t.force.y != 0 || t.force.x != 0)){
//						if(t.vel.dot(t.orientation) >= 0){
//							t.orientation.set(t.vel).shift(1, 0);
//						} else {
//							t.orientation.set(t.vel).scale(-1).shift(1, 0);
//						}
//						t.rotation = t.orientation.angle() + Math.PI/2;
	//
//						t.rotation = Math.atan2(t.vel.x, t.vel.y);
//					} else {
//						t.rotation = 0;
//					}
				} else if(willLandInWater){
					movementRotation = Math.atan2(thing.physicsPlug.velX(), thing.physicsPlug.velY());
				}
			} else if(thing.physicsPlug.onGround()){
				willLandInWater = false;
				if(keepOrthoToFloor){
					movementRotation = -thing.newLink.getCollisionLine(new Vec()).angle();
				} else {
					movementRotation = 0;
				}
			}
		}
		
		public double accWalking() { return accWalking; }
		public double accSwimming() { return accSwimming; }
		public double accFlying() {			return accFlying;		}
		public double getMovementRotation() { 		return movementRotation;}
		public double getYOffsetToBalanceRotation() {			return yOffsetToBalanceRotation;		}
		
		public void setAccWalking(double accWalking) {			this.accWalking = accWalking;		}
		public void setAccSwimming(double accSwimming) {			this.accSwimming = accSwimming;		}
		public void setAccFlying(double accFlying) {			this.accFlying = accFlying;		}

		public void setReallyAir() { 							this.reallyAir = true;}
		
		public void setBackgroundAni() {
			thing.aniPlug.setAnimation(backgroundAnimation, null);
		}

		public void setAni(double acc) {
//			String aniName = t.ani.ani.name;
			String foregroundRequest = "";
			if(thing.physicsPlug.onGround()){
				if(thing.physicsPlug.waterDepth() < 0.3){
					if(acc != 0){
						if(Math.abs(acc) > 1){
							backgroundAnimation = walk2;
						} else if(Math.abs(acc) == 1){
							backgroundAnimation = walk1;
						} else {
							backgroundAnimation = sneak;
						}
					} else {
						if(Main.game().input2.isKeyDown(Key.CROUCH.key)) {
							backgroundAnimation = sneakyStand;
						} else {
							backgroundAnimation = stand;
						}
					}
				}
				else if(thing.physicsPlug.waterDepth() < 0.3 && thing.aniPlug.getAnimator().ani.name != swim){
					foregroundRequest = dive;
					backgroundAnimation = swim;
				}
			} else {
				if(thing.physicsPlug.waterDepth() > 0){
					if(acc != 0){
						backgroundAnimation = swim;
						//TODO add animation for keeping itself at the surface and possibly a transition with rotation
					} else {
						backgroundAnimation = fly;
					}
				} else if(!willLandInWater){
					if(reallyAir){
						backgroundAnimation = fly;
					}
				} else {
					backgroundAnimation = plunge;
				}
			}

			if(thing.aniPlug.getAnimator().endTask == null){
				if(acc > 0) thing.aniPlug.setOrientation( false);
				else if(acc < 0) thing.aniPlug.setOrientation( true);
				if(foregroundRequest != "") {
					thing.aniPlug.setAnimation(foregroundRequest, () -> thing.aniPlug.setAnimation(backgroundAnimation));
				} else if(backgroundAnimation != ""){
					thing.aniPlug.setAnimation(backgroundAnimation);
				}
			}
		}
		
		/**
		 * Transitions from GROUND to AIR
		 */
		public void jump(){
			thing.aniPlug.setAnimation( liftOf, () -> {
				if(thing.physicsPlug.onGround()){
					willLandInWater = willLandInWater();
					thing.physicsPlug.leaveGround(thing.physicsPlug.velX(), thing.physicsPlug.velY() + Physics.jump);//ortho(t.vel.x > 0)
					reallyAir = true;
					if(willLandInWater){
						thing.aniPlug.setAnimation( dive, () -> {
							thing.aniPlug.setAnimation( plunge);
						});
					} else {
						thing.aniPlug.setAnimation( fly);
						backgroundAnimation = fly;
					}
				} else {
					willLandInWater = false;
				}
			});
		}
		
		private boolean willLandInWater(){
			double vX = thing.physicsPlug.velX(), vY = thing.physicsPlug.velY() + Physics.jump;
//			double xPeak = (vX*vY)/Physics.gravity.y + t.pos.x;
			double a = vY/vX, b = 0.5*Physics.gravity.y/(thing.type.physics.mass*vX*vX);
			Function f = (x) -> thing.pos.y + (a*x) + (b*x*x);
			Column cursor = thing.newLink;
			boolean lastOne = false;
			while(cursor != null){
				if(/*cursor.xReal > xPeak && */cursor.getCollisionY() != cursor.getCollisionYFluid()){
					double y = f.f(cursor.getX() - thing.pos.x);
					//if one cursor is below the graph and the other above, that's where she'll collide
					if(cursor.getTopFluidVertex().y() < y){
						lastOne = true;
					} else {
						if(lastOne){
							return true;
						} else {
							return false;
						}
					}
				} else {
					lastOne = false;
				}
				cursor = thing.physicsPlug.velX() > 0 ? cursor.right() :  cursor.left();
			}
			return false;
		}
		
		/**
		 * Transitions from AIR to GROUND
		 */
		public void land() {
			thing.aniPlug.setAnimation( land, () -> {
				thing.aniPlug.setAnimation( stand);
				backgroundAnimation = stand;
			});
		}
		
		/**
		 * Transitions from GROUND to AIR
		 */
		public void liftOf() {
			thing.aniPlug.setAnimation( liftOf, () -> {
				thing.aniPlug.setAnimation( fly);
				backgroundAnimation = fly;
			});
		}
		
		/**
		 * Transitions from GROUND to WATER
		 */
		public void dive(){
			thing.aniPlug.setAnimation( dive, () -> {
				thing.aniPlug.setAnimation( swim);
			});
		}
	}
	
}