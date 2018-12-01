package things;

import java.util.ArrayList;
import java.util.List;

import effects.Effect;
import item.ItemStack;
import item.ItemType;
import main.Main;
import quest.ActiveQuest;
import render.Animator;
import things.aiPlugins.Physics.Where;
import things.aiPlugins.Speaking.ThoughtBubble;
import things.interfaces.StructureThing;
import util.Color;
import util.math.Rect;
import util.math.Vec;
import world.World;
import world.data.Column;
import world.data.DefaultListElement;

public class Thing extends DefaultListElement<Thing> implements StructureThing<Thing> {
	//DEBUG
	public boolean selected;
	public boolean switchedSelected;
	
	//values, that don't change, but are entity specific
	public ThingType type;
	public Color color = new Color(Color.WHITE);
	public ItemType itemBeing;
	public double yOffset;
	public double accWalking = 1000, accSwimming = 250, accFlying;//accWalking is different from snail to snail for example
	public double z, size = 1;
	public Rect box = new Rect();//NOT USED FOR DRAWING!!!
	public OnInteraction onRightClick = (src, pos, dest) -> {};

	//dynamically changing values
	public short index = -1;
	public Vec pos = new Vec(), nextPos = new Vec(), lastPos = new Vec();
	public Vec vel = new Vec(), nextVelAvDelta = new Vec(), nextVel = new Vec();
	public Vec force = new Vec(), noFricForce = new Vec(), flyForce = new Vec();//flyForce is mainly for butterflies
	public double phi;
	public Column collisionC = null;
	public double airTime;
	public boolean reallyAir, willLandInWater;
	public double walkingForce, speed, maxWalkingSpeed, buoyancyForce;
	public double rotation, aniRotation, yOffsetToBalanceRotation;
	public Vec orientation = new Vec(0, 1);
	public double time;//just for unicorns at this point. Can be used to animate certain effects
	public int testInt;
	
	//Values that change, if needed
	public Animator ani;
	public Animator itemAni;
	public String backgroundAnimation = "";
	public List<ItemType> fruits = new ArrayList<>();
	public ItemStack[] itemStacks;
	public ThoughtBubble tb;
	public Effect effect;
	public int effectTicket, visibilityTicket = -1;
	public boolean dir;
	public boolean immortal;
	public boolean isRiding;
	public boolean attacking;
	public boolean speaking;
	public boolean needsRenderUpdate, needsUnusualRenderUpdate, visible, addedToVAO;
	public boolean linked = false;
	
	public Thing target;
	public Thing mountedThing;
	public AttackType lastAttack;
	/**
	 * It's the column next left to the things position
	 */
	public Column link;
	private Column realLink;
	public int selectedItem;
	public int aniSet;
	public Where where = new Where();
	public Where whereBefore = new Where();
	public int coins;
	public int health;
	public double healthTimer;
	public int mana;
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
	
	public Thing(ThingType type, Column field, Vec pos, Object... extraData){
		this.type = type;
		if(pos != null)
		this.pos = pos;
		link = field;
		realLink = field;
		applyLink();
		
		setup(field, pos, extraData);
	}
	
	public void update(double delta){
		type.update(this, delta);
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.update(this, delta);
		}
	}
	
	public Column getRealLink() {
		return realLink;
	}
	
	public void onVisibilityChange(boolean visible) {
		if(type.attachment != null) {
			type.attachment.onVisibilityChange(this, visible);
		}
		this.visible = visible;
	}
	
	public void prepareRender(){
		if(visible){
			type.ani.prepareRender(this);
		}
	}
	
	public void prepareSecondRender(){
		if(visible){
			type.ani.prepareSecondRender(this);
		}
	}
	
	public void setup(Column field, Vec pos, Object... extraData){
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.setup(this);
		}
		type.setup(this, field, pos, extraData);
	}
	
	public boolean selected() {
		return selected;
	}
	
	public void applyLink() {
		if(realLink != link || !linked) {
			realLink.remove(this);
			link.add(this);
			linked = true;
			realLink = link;
		}
	}
	public void remove() {
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.remove(this);
		}
		Main.world.thingWindow.remove(this);
	}
	public void showUpAfterHiding(Column link) {
		this.realLink = link;
		this.link = link;
		applyLink();
		if(type.ani != null && !this.visible){
			World.world.thingWindow.add(this);
		}
		onVisibilityChange(true);
	}
	public void hide() {

		if(type.ani != null && this.visible){
			World.world.thingWindow.remove(this);
		}
		onVisibilityChange(false);
		if(linked) link.remove(this);
	}
	
	public String save(){return "";};
	
	public void load(String save){}

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

}
