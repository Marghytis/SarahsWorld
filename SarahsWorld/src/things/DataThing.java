package things;

import java.util.ArrayList;
import java.util.List;

import effects.Effect;
import item.ItemStack;
import item.ItemType;
import moveToLWJGLCore.DefaultListElement;
import quest.ActiveQuest;
import render.Animator;
import things.aiPlugins.Physics.Where;
import things.aiPlugins.Speaking.ThoughtBubble;
import things.interfaces.Listable;
import things.interfaces.StructureThing;
import util.math.Vec;
import world.data.Column;

public abstract class DataThing extends DefaultListElement<Thing> implements StructureThing<Thing>, Listable {
	//DEBUG
//	public boolean selected;
//	public boolean switchedSelected;
	
	//values, that don't change, but are entity specific
	public ThingType type;
//	public Color color = new Color(Color.WHITE);
	public ItemType itemBeing;
	public double yOffset;
	public double accWalking = 1000, accSwimming = 250, accFlying;//accWalking is different from snail to snail for example
//	public double z, 
	double size = 1;
//	public Rect box = new Rect();//NOT USED FOR DRAWING!!!
	public OnInteraction onRightClick = (src, pos, dest) -> {};
	public boolean isAvatar = false;

	//dynamically changing values
//	public short index = -1;
	public int[] indices = {-1,-1,-1,-1,-1};//for diverse lists of things
	public Vec pos = new Vec(), nextPos = new Vec(), lastPos = new Vec();
	public Vec vel = new Vec(), nextVelAvDelta = new Vec(), nextVel = new Vec();
	public Vec force = new Vec(), noFricForce = new Vec(), flyForce = new Vec();//flyForce is mainly for butterflies
	public double phi;
	public Column collisionC = null;
	public double airTime;
	public boolean reallyAir, willLandInWater;
	public double walkingForce, speed, maxWalkingSpeed, buoyancyForce;
	public double rotation, /*aniRotation, */yOffsetToBalanceRotation;
	public double damageCooldown;
//	public double time;//just for unicorns at this point. Can be used to animate certain effects
	
	//Values that change, if needed
//	public Animator ani;
	public Animator itemAni;
	public String backgroundAnimation = "";
	public List<ItemType> fruits = new ArrayList<>();
	public ItemStack[] itemStacks;
	public ThoughtBubble tb;
	public Effect effect;
	public int effectTicket;
//	public boolean dir;
	public boolean immortal;
	public boolean isRiding;
	public boolean attacking;
	public boolean speaking;
//	public boolean needsRenderUpdate, needsUnusualRenderUpdate, visible, addedToVAO, freeToMakeInvisible;
	public boolean linked = false, real = false;
	public boolean willingToTrade = false;
	public boolean active = false;
	
	public Thing target;
	public Thing mountedThing;
	public Technique lastAttack;
	/**
	 * It's the column next left to the things position
	 */
	public Column newLink;
	protected Column realLink;
	public int selectedItem;
//	public int aniSet;
	public Where where = new Where();
	public Where whereBefore = new Where();
	public int coins;
	public int health;
	public double healthTimer;
	public int armor;
	public int amount;//for coins or items
	public double attackCooldown;//attackCooldown counts upwards
	public double xDest;
	public double waitTime;//for the walk around plugin
	public String currentSpeech;
	public String[] answers;
	public ActiveQuest quest;

	public double xDestMin, xDestMax;//for walking around
	public double splashCooldown1, splashCooldown2, otherCooldown;
	
	public Column getRealLink() {
		return realLink;
	}
	
//	public boolean selected() {
//		return selected;
//	}

	public int getTypeOrdinal() {
		return type.ordinal;
	}
	public void free() {
		if(next != null) next.prev = prev;
		if(prev != null) prev.next = next;
	}

	public ThingType type() {
		return type;
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
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

	public ThingType getType() {
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

//	@Override
//	public ThingType getType() {
//		return type;
//	}
//
//	@Override
//	public int getiAnimationSet() {
//		return aniSet;
//	}
//
//	@Override
//	public void setAnimator(Animator ani) {
//		this.ani = ani;
//	}
//
//	@Override
//	public void setRenderBox(Rect box) {
//		this.box = box;
//	}
//
////	@Override
////	public void setColor(Color c) {
////		this.color = c;
////	}
//
//	@Override
//	public void setZ(double z) {
//		this.z = z;
//	}
//
//	@Override
//	public void setOrientation(boolean ori) {
//		this.dir = ori;
//	}
//
//	@Override
//	public void setAniRotation(double angle) {
//		this.aniRotation = angle;
//	}
//
//	@Override
//	public void setRotation(double angle) {
//		this.rotation = angle;
//	}
//
//	@Override
//	public void setNeedsRenderUpdate(boolean needs) {
//		this.needsRenderUpdate = needs;
//	}
//
//	@Override
//	public void setNeedsUnusualRenderUpdate(boolean needs) {
//		this.needsUnusualRenderUpdate = needs;
//	}
//
//	@Override
//	public void setSwitchedSelected(boolean switched) {
//		this.switchedSelected = switched;
//	}
//
//	@Override
//	public void changeBox(int[] pixelCoords) {
//		this.box.set(pixelCoords);
//	}
//
//	@Override
//	public Animator getAnimator() {
//		return ani;
//	}
//
//	@Override
//	public Rect getRenderBox() {
//		return box;
//	}
//
////	@Override
////	public Color getColor() {
////		return color;
////	}
//
//	@Override
//	public boolean needsRenderUpdate() {
//		return needsRenderUpdate;
//	}
//
//	@Override
//	public boolean needsUnusualRenderUpdate() {
//		return needsUnusualRenderUpdate;
//	}
//
//	@Override
//	public boolean switchedSelected() {
//		return switchedSelected;
//	}
//
//	@Override
//	public int getIndex() {
//		return index;
//	}
//
//	@Override
//	public double getZ() {
//		return z;
//	}
//
//	@Override
//	public double getSize() {
//		return size;
//	}
//
//	@Override
//	public double getRotation() {
//		return rotation;
//	}
//
//	@Override
//	public Vec pos() {
//		return pos;
//	}
//
//	@Override
//	public double getYOffset() {
//		return yOffset;
//	}
//
//	@Override
//	public double getYOffsetToBalanceRotation() {
//		return yOffsetToBalanceRotation;
//	}
//
//	@Override
//	public double getAniRotation() {
//		return aniRotation;
//	}
//
//	@Override
//	public boolean getOrientation() {
//		return dir;
//	}

}
