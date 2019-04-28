package things;

import effects.Effect;
import item.ItemStack;
import item.ItemType;
import render.Animator;
import things.aiPlugins.Physics.Where;
import things.interfaces.Listable;
import things.interfaces.StructureThing;
import util.math.Vec;
import world.data.Column;

public abstract class DataThing extends Entity implements StructureThing<Entity>, Listable {

	//values, that don't change, but are entity specific
	public double accWalking = 1000, accSwimming = 250, accFlying;//accWalking is different from snail to snail for example
	double size = 1;
	public OnInteraction onRightClick = (src, pos, dest) -> {};
	public boolean isAvatar = false;

	//dynamically changing values
	public int[] indices = {-1,-1,-1,-1,-1};//for diverse lists of things
	public Vec nextPos = new Vec(), lastPos = new Vec();
	public Vec vel = new Vec(), nextVelAvDelta = new Vec(), nextVel = new Vec();
	public Vec force = new Vec(), noFricForce = new Vec(), flyForce = new Vec();//flyForce is mainly for butterflies
	public double phi;
	public Column collisionC = null;
	public double airTime;
	public boolean reallyAir, willLandInWater;
	public double walkingForce, speed, maxWalkingSpeed, buoyancyForce;
	public double rotation, /*aniRotation, */yOffsetToBalanceRotation;
//	public double time;//just for unicorns at this point. Can be used to animate certain effects
	
	//Values that change, if needed
//	public Animator ani;
	public Animator itemAni;
	public String backgroundAnimation = "";
	public Effect effect;
	public int effectTicket;
//	public boolean dir;
//	public boolean needsRenderUpdate, needsUnusualRenderUpdate, visible, addedToVAO, freeToMakeInvisible;
	public boolean linked = false, real = false;
	public boolean willingToTrade = false;
	public boolean active = false;
	
//	public int aniSet;
	public Where where = new Where();
	public Where whereBefore = new Where();
	public double healthTimer;
	public int armor;
	public int amount;//for coins or items
	public double xDest;
	public double waitTime;//for the walk around plugin

	public double xDestMin, xDestMax;//for walking around
	public double splashCooldown1, splashCooldown2, otherCooldown;

	public DataThing(Species<Thing> type, Column field, Vec pos, Object[] extraData) {
		super(type, field, pos, extraData);
	}
	
	public Column getRealLink() {
		return realLink;
	}
	
	public int getTypeOrdinal() {
		return type.ordinal;
	}
	
	@SuppressWarnings("unchecked")
	public Species<Thing> type() {
		return (Species<Thing>) type;
	}

	@Override
	public int getIndex(int indexIndex) {
		return indices[indexIndex];
	}

	@Override
	public void setIndex(int indexIndex, int index) {
		indices[indexIndex] = index;
	}

	public void setRotation(double angle) {
		rotation = angle;
	}

	public Species<Thing> getType() {
		return type();
	}

	public double getSize() {
		return size;
	}
	
	public double getRotation() {
		return rotation;
	}

	public Vec pos() {
		return pos;
	}

	public double getYOffset() {
		return yOffset;
	}

	public double getYOffsetToBalanceRotation() {
		return yOffsetToBalanceRotation;
	}

}
