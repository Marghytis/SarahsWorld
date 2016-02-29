package things;

import item.ItemStack;
import item.ItemType;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import quest.ActiveQuest;
import render.Animator;
import things.aiPlugins.Physics.Where;
import things.aiPlugins.Speaking.ThoughtBubble;
import util.Color;
import util.math.Rect;
import util.math.Vec;
import world.WorldData;
import world.WorldData.Column;

public class Thing {
	
	
	//values, that don't change, but are entity specific
	public ThingType type;
	public Color color = null;
	public ItemType itemBeing;
	public double yOffset;
	public double accWalking = 1000, accSwimming = 250, accFlying;//accWalking is different from snail to snail for example
	public int behind;
	public Rect box = new Rect();

	public Thing right, left;
	public Vec pos = new Vec(), nextPos = new Vec();
	public Vec vel = new Vec(), nextVelAvDelta = new Vec(), nextVel = new Vec();
	public Vec force = new Vec(), noFricForce = new Vec(), flyForce = new Vec();//flyForce is mainly for butterflies
	public Column collisionC = null;
	public double airTime;
	public boolean reallyAir, willLandInWater;
	public double walkingForce, speed, maxWalkingSpeed;
	public Animator ani;
	public Animator itemAni;
	public double rotation;
	public List<ItemType> fruits = new ArrayList<>();
	public ItemStack[] itemStacks;
	public ThoughtBubble tb;
	public boolean dir;
	public boolean immortal;
	public boolean isRiding;
	public boolean attacking;
	public boolean speaking;
	
	public Thing target;
	public Thing mountedThing;
	public AttackType lastAttack;
	/**
	 * It's the column next left to the things position
	 */
	public Column link;
	public int selectedItem;
	public int aniSet;
	public Where where = new Where();
	public Where whereBefore = new Where();
	public int coins;
	public int health;
	public int mana;
	public int armor;
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
		if(type == ThingType.DUMMY){
			field.things[(int)extraData[0]] = this;
		} else {
			field.add(this);
		}
		
		setup(world, field, pos, extraData);
	}
	
	public void update(double delta){
		type.update(this, delta);
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.update(this, delta);
		}
	}
	
	public void render(){
		type.file.file.bind();
		GL11.glPushMatrix();
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.partRender(this);
		}
		GL11.glPopMatrix();
	}
	
	public void setup(WorldData world, Column field, Vec pos, Object... extraData){
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.setup(this, world);
		}
		type.setup(this, world, field, pos, extraData);
	}

	public void remove() {
		for(AiPlugin plugin : type.plugins){
			if(plugin != null) plugin.remove(this);
		}
	}
	
	public void disconnect(){
		if(left != null) left.right = right;
		if(right != null) right.left = left;
	}
	
	public String save(){return "";};
	
	public void load(String save){};
}
