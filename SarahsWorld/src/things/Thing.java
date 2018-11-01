package things;

import java.util.ArrayList;
import java.util.List;

import effects.WorldEffect;
import item.ItemStack;
import item.ItemType;
import main.Main;
import quest.ActiveQuest;
import render.Animator;
import things.aiPlugins.Physics.Where;
import things.aiPlugins.Speaking.ThoughtBubble;
import util.Color;
import util.math.Rect;
import util.math.Vec;
import world.World;
import world.data.Column;
import world.data.WorldData;

public class Thing {
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

	//dynamically changing values
	public Thing prev, next;
	public short index = -1;
	public Vec pos = new Vec(), nextPos = new Vec();
	public Vec vel = new Vec(), nextVelAvDelta = new Vec(), nextVel = new Vec();
	public Vec force = new Vec(), noFricForce = new Vec(), flyForce = new Vec();//flyForce is mainly for butterflies
	public double phi;
	public Column collisionC = null;
	public double airTime;
	public boolean reallyAir, willLandInWater;
	public double walkingForce, speed, maxWalkingSpeed;
	public double rotation, aniRotation;
	public Vec orientation = new Vec(0, 1);
	public double time;//just for unicorns at this point. Can be used to animate certain effects
	public int testInt;
	
	//Values that change, if needed
	public Animator ani;
	public Animator itemAni;
	public List<ItemType> fruits = new ArrayList<>();
	public ItemStack[] itemStacks;
	public ThoughtBubble tb;
	public WorldEffect effect;
	public int effectTicket;
	public boolean dir;
	public boolean immortal;
	public boolean isRiding;
	public boolean attacking;
	public boolean speaking;
	public boolean needsRenderUpdate, needsUnusualRenderUpdate, visible;
	public boolean linked = false;
	
	public Thing target;
	public Thing mountedThing;
	public AttackType lastAttack;
	/**
	 * It's the column next left to the things position
	 */
	public Column link, oldLink;
	public int selectedItem;
	public int aniSet;
	public Where where = new Where();
	public Where whereBefore = new Where();
	public int coins;
	public int health;
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
	
	public Thing(ThingType type, WorldData world, Column field, Vec pos, Object... extraData){
		this.type = type;
		if(pos != null)
		this.pos = pos;
		link = field;
		oldLink = field;
		applyLink();
		
		setup(world, field, pos, extraData);
	}
	
	public void update(double delta){
		type.update(this, delta);
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.update(this, delta);
		}
	}
	
	public void setVisible(boolean visible){
		if(type.attachment != null) {
			type.attachment.onVisibilityChange(this, visible);
		}
		if(type.ani != null) {
			if(this.visible && !visible){
				World.world.window.getVAO(type).remove(this);
			} else if(!this.visible && visible){
				World.world.window.getVAO(type).add(this);
			}
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
	
	public void setup(WorldData world, Column field, Vec pos, Object... extraData){
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.setup(this, world);
		}
		type.setup(this, world, field, pos, extraData);
	}
	
	public boolean selected() {
		return selected;
	}
	
	public void disconnectFrom(Column link) {
		if(next != null) next.prev = prev;
		if(prev != null) prev.next = next;
		if(link.things[type.ordinal] == this) link.things[type.ordinal] = null;
	}
	public void applyLink() {
		if(oldLink != link || !linked) {
			disconnectFrom(oldLink);
			link.add(this);
			linked = true;
		}
	}
	public void remove() {
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.remove(this);
		}
		Main.world.window.getVAO(type).remove(this);
	}
	
	public String save(){return "";};
	
	public void load(String save){}

}
