package world.things;

import java.util.Random;

import main.Savable;

import org.lwjgl.opengl.GL11;

import world.things.aiPlugins.Acceleration;
import world.things.aiPlugins.Animating;
import world.things.aiPlugins.Attacking;
import world.things.aiPlugins.Aura;
import world.things.aiPlugins.AvatarControl;
import world.things.aiPlugins.Collision;
import world.things.aiPlugins.Coloration;
import world.things.aiPlugins.Controller;
import world.things.aiPlugins.FlyAround;
import world.things.aiPlugins.Following;
import world.things.aiPlugins.Fruits;
import world.things.aiPlugins.Gravity;
import world.things.aiPlugins.Grounding;
import world.things.aiPlugins.Inventory;
import world.things.aiPlugins.ItemBeing;
import world.things.aiPlugins.Life;
import world.things.aiPlugins.Magic;
import world.things.aiPlugins.MatFriction;
import world.things.aiPlugins.Position;
import world.things.aiPlugins.Riding;
import world.things.aiPlugins.Speaking;
import world.things.aiPlugins.Velocity;
import world.things.aiPlugins.WalkAround;

public class Thing implements Savable{

	public ThingType type;
	public Random rand;
	
	//for the thing lists
	public Thing right, left;
	
	public AiPlugin[] ai;
	
	//No action
	public Life life;
	public Magic magic;
	public Fruits fruits;
	public Attacking attack;
	public Riding riding;
	public Inventory inv;
	public ItemBeing item;
	public Speaking speak;
	public Aura aura;
	
	//set acceleration and animation
	public Controller cont;//summarize all controlling plug-ins in one controller
	public AvatarControl avatar;//not directly in a.i.
	public FlyAround flyAround;//not directly in a.i.
	public WalkAround walkAround;//not directly in a.i.
	public Following follow;//not directly in a.i.
	
	/**0:standing, 1:walking, 2:sprinting, 3:jumping, 4:flying, 5:landing*/
	public Grounding ground;
	
	//physics
	public Gravity gravity;
	public MatFriction friction;
	public Acceleration acc;
	public Collision collision;
	public Velocity vel;
	public Position pos;
	
	//graphics
	public Coloration color;
	public Animating ani;
	
	public void createAi(){
		ai = new AiPlugin[]{
		attack,
		cont,
		ground,
		gravity,
		friction,
		acc,
		collision,
		vel,
		pos,
		color,
		ani,
		inv,
		life,
		speak,
		aura
		};
	}
	
	Thing(ThingType type, Random random){
		this.type = type;
		this.rand = random;
	}
	
	public void update(double delta){
		for(AiPlugin plugin : ai){
			if(plugin != null) plugin.action(delta);
		}
	}
	
	public void render(){
		GL11.glPushMatrix();
		for(AiPlugin plugin : ai){
			if(plugin != null) plugin.partRender();
		}
		GL11.glPopMatrix();
	}
	
	public void disconnect(){
		if(left != null) left.right = right;
		if(right != null) right.left = left;
	}

	public String save() {
		String save = "";
		for(AiPlugin plugin : ai){
			if(plugin != null) save += plugin.save() + object;
		}
		return save;
	}

	public void load(String save) {}
	
	/**
	 * Can't be done inside this object
	 */
	public static Thing load(ThingType type, String save, Random rand) {
		String[] infos = save.split(Savable.object, -1);
		Thing t = new Thing(type, rand);
		t.ai = new AiPlugin[infos.length];
		
		for(int k = 0; k < infos.length-1; k++){
			t.ai[k].load(infos[k]);
		}
		return t;
	}

	public void remove() {
		for(AiPlugin plugin : ai){
			if(plugin != null) plugin.remove();
		}
	}
}
